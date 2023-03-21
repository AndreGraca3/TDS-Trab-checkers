package isel.leic.tds.gui

import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.INITIAL_PIECES
import isel.leic.tds.checkers.model.Player
import isel.leic.tds.checkers.model.Player.BLACK
import isel.leic.tds.checkers.model.Player.WHITE
import isel.leic.tds.checkers.model.Square
import isel.leic.tds.storage.StorageAsync

data class GameAsync(
    val name: String,
    val board: Board,
    val player: Player = BLACK
)

suspend fun startGame(_id: String, storage: StorageAsync<String, Board>) : GameAsync {
    val board = storage.load(_id) ?: return GameAsync(_id, storage.new(_id), WHITE)
    if(board.moves.count { !INITIAL_PIECES.contains(it) } <= 1 && board.moves.size == INITIAL_PIECES.size)
        return GameAsync(_id, board, BLACK)
    storage.delete(_id)
    return GameAsync(_id, storage.new(_id), WHITE)
}

suspend fun GameAsync.play(storage: StorageAsync<String, Board>, src: Square, dst: Square) : GameAsync {
    val board = this.board.play(src, dst, this.player)
    storage.save(this.name, board)
    return this.copy(board = board)
}