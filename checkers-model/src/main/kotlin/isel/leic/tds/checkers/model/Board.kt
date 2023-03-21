package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.model.Player.WHITE
import isel.leic.tds.checkers.model.Player.BLACK
import kotlin.math.absoluteValue

sealed class Board(val moves: List<Move>, val turn: Player) {

    var plays: List<Play> = emptyList()
    val mandatoryList get() = plays.filter { it.captured != null && it.player == turn }

    companion object {
        fun deserialize(input: String): Board {
            val lines = input.split("\n")
            val kind = lines[0]
            val turn = lines[1].toPlayer()
            val moves = lines.drop(2).map { Move.deserialize(it) }
            return when (kind) {
                BoardRun::class.simpleName -> BoardRun(moves, turn)
                BoardDraw::class.simpleName -> BoardDraw(moves, turn)
                BoardWinner::class.simpleName -> BoardWinner(moves, turn)
                else -> {
                    throw IllegalArgumentException("There is no board type for input $kind")
                }
            }
        }

        fun getSlashDiagonal(i: Int): List<Square> {
            return Square.values.filter { it.black && (it.row.index + it.column.index == i) }.reversed()
        }

        fun getBackslashDiagonal(i: Int): List<Square> {
            return Square.values.filter { it.black && (it.row.index - it.column.index == i) }
        }
    }

    fun serialize() = "${this::class.simpleName}\n${turn.symbol}\n${moves.joinToString("\n") { it.serialize() }}"

    fun getMove(square: Square, moves: List<Move> = this.moves): Move? {
        return moves.find { it.square == square }
    }

    abstract fun play(src: Square, dst: Square, player: Player): Board
}

class BoardWinner(moves: List<Move>, val winner: Player) : Board(moves, winner) {
    override fun play(src: Square, dst: Square, player: Player): Board {
        throw IllegalStateException("Player $winner has already won this game.Therefore you can't play in it.")
    }
}

class BoardDraw(moves: List<Move>, turn: Player) : Board(moves, turn) {
    override fun play(src: Square, dst: Square, player: Player): Board {
        throw IllegalStateException("This game has already finished with a draw.")
    }
}

class BoardRun(moves: List<Move> = INITIAL_PIECES, turn: Player = WHITE) : Board(moves, turn) {

    init {
        plays = getAllPlays()
    }

    override fun play(src: Square, dst: Square, player: Player): Board {
        require(dst in Square.values) { "${dst.row.number}${dst.column.symbol} isn't a valid square." }
        require(dst.black) { "Invalid destination square." }
        require(turn == player) { "It's not your turn." }
        val srcPiece = getMove(src)
        require(srcPiece != null && srcPiece.player == player) { "position $src does not have your piece" }
        require(getMove(dst) == null) { "Position $dst is occupied." }
        require(plays.any { it.dst == dst && it.src == src }) { "Invalid Move." }

        val m: Move =
            if ((player == WHITE && dst.row.index == 0) || (player == BLACK && dst.row.index == BOARD_DIM - 1))
                Move(player, dst, true) else Move(player, dst, srcPiece.isKing)

        if (mandatoryList.isNotEmpty()) {
            val play = mandatoryList.find { it.dst == dst && it.src == src }
            if (play == null)
                throw IllegalArgumentException("There is mandatory capture in ${mandatoryList.first().src}")
            else {
                return makePlay(m, srcPiece, player, getMove(play.captured!!))
            }
        }
        return makePlay(m, srcPiece, player)
    }


    private fun makePlay(m: Move, srcPiece: Move, player: Player, capturedPiece: Move? = null): Board {
        return if (capturedPiece == null) BoardRun(moves + m - srcPiece, player.opposite())
        else {
            val tempMoves = moves + m - srcPiece - capturedPiece
            val turn =
                if (getAllPlays(tempMoves).filter { it.src == m.square && it.captured != null }
                        .isNotEmpty()) //all next mandatory plays for same piece
                    player
                else player.opposite()
            when {
                checkDraw(m) -> BoardDraw(tempMoves, turn)
                checkWinner(m) -> BoardWinner(tempMoves, tempMoves.last().player)
                else -> BoardRun(tempMoves, turn)
            }
        }
    }

    private fun getAllPlays(moves: List<Move> = this.moves): List<Play> {
        var possiblePLays = emptyList<Play>()
        for (i in BOARD_DIM - 1 downTo -BOARD_DIM + 1) {     //Backslashes
            val b = getBackslashDiagonal(i)
            if (b.size >= 2) possiblePLays += getDiagonalPlays(b.reversed(), moves) //reverse cause of diagonal's POV
        }

        for (i in 0..BOARD_DIM * 2) {   //Slashes
            val s = getSlashDiagonal(i)
            if (s.size >= 2) possiblePLays += getDiagonalPlays(s, moves)
        }
        return possiblePLays
    }

    private fun getDiagonalPlays(d: List<Square>, moves: List<Move>): List<Play> {
        var i = 0
        var j = 0
        val diagonalMoves = d.map { getMove(it, moves) }
        var res = emptyList<Play>()
        while (i in diagonalMoves.indices) {
            val iPiece = diagonalMoves[i]
            if (iPiece == null) {
                i++
                continue
            }
            for (dir in -1..1 step 2) {    //analyse both directions
                j = i + dir
                while (j in diagonalMoves.indices) {
                    if (!iPiece.isKing && ((j - i).absoluteValue > 1 || (!checkJump(diagonalMoves, iPiece, j, dir)
                                && ((iPiece.player == WHITE && dir == -1) || (iPiece.player == BLACK && dir == 1))))
                    ) break     //if piece isn't king and (distance is > 1 or if piece cant eat and is wrong dir)
                    if (diagonalMoves[j] != null) {
                        if (checkJump(diagonalMoves, iPiece, j, dir)) {
                            var k = j + dir
                            while (k in diagonalMoves.indices && diagonalMoves[k] == null) {
                                res += Play(d[i], d[j], d[k], iPiece.player)
                                k += dir
                                if (!iPiece.isKing) break
                            }
                        }
                        break
                    } else {
                        res += Play(d[i], null, d[j], iPiece.player)
                    }
                    j += dir
                }
            }
            i = j
        }
        return res
    }

    private fun checkJump(diagonalMoves: List<Move?>, iPiece: Move, sqrIdx: Int, dir: Int): Boolean {
        return (diagonalMoves[sqrIdx]?.player == iPiece.player.opposite() && sqrIdx + dir in diagonalMoves.indices
                && diagonalMoves[sqrIdx + dir] == null)     //checks if iPiece can eat jPiece no matter the distance
    }

    private fun checkWinner(m: Move): Boolean { //haven't played final move
        return moves.count { it.player == m.player.opposite() } == 1
    }

    private fun checkDraw(m: Move): Boolean { //haven't played final move
        return moves.count { it.player == m.player } == 2 && moves.count { it.player == m.player.opposite() } == 3
    }
}