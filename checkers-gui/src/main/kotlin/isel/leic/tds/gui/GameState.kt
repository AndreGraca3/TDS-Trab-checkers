package isel.leic.tds.gui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import isel.leic.tds.checkers.model.*
import isel.leic.tds.storage.StorageAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameState(val scope: CoroutineScope, val storage: StorageAsync<String, Board>) {
    private var jobRefresh: Job? = null
    private val gameState: MutableState<GameAsync?> = mutableStateOf(null)
    private val messageState: MutableState<String?> = mutableStateOf(null)
    private val srcState: MutableState<Square?> = mutableStateOf(null)

    val board get() = gameState.value
    val message get() = messageState.value
    val selectedSquare get() = srcState.value

    fun startGame(name: String) {
        scope.launch {
            gameState.value = startGame(name, storage)
            val (game, setGame) = gameState
            jobRefresh?.cancel()
            jobRefresh = scope.launch {
                if (game != null) {
                    while (true) {
                        val (autoRefresh, setAutoRefresh) = autoRefreshState
                        delay(3000)
                        if(autoRefresh) refresh()
                    }
                }
            }
        }
    }

    private fun play(src: Square, dst: Square) {
        val (game, setGame) = gameState
        if (game == null) {
            messageState.value = "You should start a new Game before playing!"
            return
        }
        scope.launch {
            try {
                val newGame = game.play(storage, src, dst)
                setGame(newGame)
                messageState.value = boardMessage(newGame.board)
            } catch (ex: Exception) {
                if (ex is IllegalStateException) messageState.value = ex.message
            }
        }
    }

    fun refresh() {
        val (game, setGame) = gameState
        if (game == null) {
            messageState.value = "You should start a new Game before playing!"
            return
        }
        scope.launch {
            val newBoard = storage.load(game.name)
            if (newBoard != null && newBoard.moves != game.board.moves)
                setGame(game.copy(board = newBoard))
        }
    }

    fun chooseSquare(sqr: Square) {
        if (board == null || board?.board?.turn != board?.player) return
        val (srcState, setSrcState) = srcState
        if (srcState != sqr && board!!.board.getMove(sqr)?.player == board!!.player) {
            setSrcState(sqr)
        } else if (srcState != null) {
            val src = srcState
            setSrcState(null)
            play(src, sqr)
        }
    }

    private fun boardMessage(board: Board): String? {
        return when (board) {
            is BoardWinner -> "Game finished with winner ${board.winner}"
            is BoardDraw -> "Game finished with DRAW!"
            is BoardRun -> null
        }
    }

    fun dismissMessage() {
        messageState.value = null
    }
}