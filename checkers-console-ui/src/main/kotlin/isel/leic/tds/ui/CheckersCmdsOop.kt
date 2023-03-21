package isel.leic.tds.ui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.storage.MongoStorage
import isel.leic.tds.storage.Storage

/** Cmds for our checkers game **/

object QuitCommandOop: CommandOop<Game> {
    override val syntax get() = "exit"
    override fun show(game: Game) { }
    override fun action(game: Game?, args: List<String>) = null
}

class StartCommandOop(val storage: Storage<String,Board>) : CommandOop<Game> {
    override val syntax get() = "start name"
    override fun show(game: Game) = printBoard(game)
    override fun action(game: Game?, args: List<String>): Game {
        require(game == null) {"Game already started"}
        require(args.size == 1) { "You should provide a unique name for the new game!" }
        return startGame(args[0], storage)
    }
}

class PlayCommandOop(val storage: Storage<String,Board>): CommandOop<Game> {
    override val syntax get() = "play"
    override fun show(game: Game) = printBoard(game)
    override fun action(game: Game?, args: List<String>) = checkersPlay(storage, game, args)
}

class RefreshCommandOop(val storage: Storage<String,Board>): CommandOop<Game> {
    override val syntax get() = "refresh"
    override fun show(game: Game) = printBoard(game)
    override fun action(game: Game?, args: List<String>): Game {
        require(game != null) { "You should start a new game before playing" }
        val board = storage.load(game.name)
        check(board != null) { "Cannot refresh without known game!" }
        return game.copy(board = board)
    }
}

fun checkersPlay(storage: Storage<String, Board>, game: Game?, args: List<String>):Game {
    require(args.size == 2) { "Missing positions\nUse: PLAY <fromSquare> <toSquare>" }
    require(game != null) { "You should start a new game before playing" }
    val srcSqr = Square(calcRowIdx(args[0].elementAt(0).digitToInt()), calcColumnIdx(args[0].elementAt(1)))
    val dstSqr = Square(calcRowIdx(args[1].elementAt(0).digitToInt()), calcColumnIdx(args[1].elementAt(1)))
    val board = game.board.play(srcSqr, dstSqr, game.player)  //verify rules and conditions on board's play function
    storage.save(game.name, board)
    return game.copy(board = board)
}