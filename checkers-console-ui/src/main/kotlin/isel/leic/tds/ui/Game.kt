package isel.leic.tds.ui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.model.Player.WHITE
import isel.leic.tds.checkers.model.Player.BLACK
import isel.leic.tds.storage.Storage

data class Game(
    val name: String,
    val board: Board,
    val player: Player = BLACK
)

fun startGame(_id: String, storage: Storage<String,Board>) : Game {
    val board = storage.load(_id) ?: return Game(_id, storage.new(_id), WHITE)
    if(board.moves.count { !INITIAL_PIECES.contains(it) } <= 1 && board.moves.size == INITIAL_PIECES.size)
        return Game(_id, board, BLACK)
    storage.delete(_id)
    return Game(_id, storage.new(_id), WHITE)
}