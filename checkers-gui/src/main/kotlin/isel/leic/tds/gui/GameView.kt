package isel.leic.tds.gui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.model.Player.BLACK
import kotlin.reflect.KSuspendFunction0
import kotlin.system.exitProcess

val autoRefreshState: MutableState<Boolean> = mutableStateOf(true)
val showTargetsState: MutableState<Boolean> = mutableStateOf(true)

@Composable
fun FrameWindowScope.CheckersMenu(onNew: (String) -> Unit, onRefresh: () -> Unit) {
    val (newGameDialog, setNewGameDialog) = remember { mutableStateOf(false) }
    val (autoRefresh, setAutoRefresh) = autoRefreshState
    val (showTargets, setShowTargets) = showTargetsState
    if (newGameDialog)
        DialogInput({ name ->
            onNew(name)
            setNewGameDialog(false)
        }) {
            setNewGameDialog(false)
        }
    MenuBar {
        Menu("Game", mnemonic = 'G') {
            Item("New", onClick = { setNewGameDialog(true) })
            Item("Refresh", onClick = onRefresh, enabled = !autoRefresh)
            Item("Exit", onClick = { exitProcess(0) })
        }
        Menu("Options", mnemonic = 'O') {
            RadioButtonItem("Show Targets", showTargets, onClick = { setShowTargets(showTargets.not()) })
            RadioButtonItem("Auto-refresh", autoRefresh, onClick = { setAutoRefresh(autoRefresh.not()) })
        }
    }
}

@Composable
fun DialogMessage(msg: String?, onClose: () -> Unit) {
    if (msg != null)
        Dialog(
            title = "Message",
            onCloseRequest = onClose,
            state = rememberDialogState(
                position = WindowPosition(Alignment.Center),
                size = DpSize.Unspecified
            )
        ) {
            Text(msg)
        }
}

@Composable
fun GameInfoTexts(game: GameState) {
    Row() {
        Text(
            modifier = Modifier.width(130.dp),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            text = if (game.board != null) "Game: ${game.board!!.name}" else "Start a new Game!"
        )
        Text(
            modifier = Modifier.width(150.dp),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            text = if (game.board != null) "You: ${game.board!!.player}" else ""
        )
        Text(
            modifier = Modifier.width(150.dp),
            textAlign = TextAlign.End,
            fontSize = 15.sp,
            text =
            if (game.board == null) ""
            else {
                if (game.board!!.board.turn == game.board!!.player) "Your turn"
                else "Waiting.."
            }
        )
    }
}

@Composable
fun BoardLetters() {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until BOARD_DIM) {
            Text(
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                text = "${'a' + i}"
            )
        }
    }
}

@Composable
fun BoardNumbers(player: Player?) {
    Column(
        modifier = Modifier
            .width(15.dp)
    ) {
        var list = (BOARD_DIM downTo 1).let { if (player == BLACK) it.reversed() else it }     //invert Board's numbers
        for (i in list) {
            Text(
                modifier = Modifier.height(50.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                text = "\n$i",
            )
        }
    }
}

@Composable
fun BoardView(board: Board?, selSqr: Square?, player: Player?, onCellClick: (Square) -> Unit) {
    Column {
        repeat(BOARD_DIM) { line ->
            var line = line
            if (player == BLACK) line = BOARD_DIM-1 - line    //invert Board
            Row {
                repeat(BOARD_DIM) { col ->
                    val move = board?.moves?.find { it.square == Square(line, col) }
                    val targets = if (board != null && selSqr != null) {
                        board.mandatoryList.filter { it.player == player }
                            .ifEmpty { board.plays.filter { it.player == player } }
                    } else emptyList()
                    Cell(move, targets, selSqr, Square(line, col), onCellClick)
                }
            }
        }
    }
}

@Composable
fun Cell(move: Move?, targets: List<Play>, selSqr: Square?, sqr: Square, onCellClick: (Square) -> Unit) {
    var col =
        if (sqr == selSqr) Color.Red
        else Color.Transparent

    val mod = Modifier
        .border(2.dp, col)
        .background(if (sqr.black) Color.DarkGray else Color.White)
        .size(50.dp)
        .clickable(onClick = { onCellClick(sqr) })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = mod
    ) {
        if (move != null) {
            val name =
                if (move.player == BLACK) {
                    if (move.isKing) "piece_bk.png"
                    else "piece_b.png"
                } else {
                    if (move.isKing) "piece_wk.png"
                    else "piece_w.png"
                }

            Image(
                painter = painterResource(name),
                contentDescription = name,
                modifier = Modifier.fillMaxSize()
            )
        }
        col =
            if (targets.any { it.dst == (sqr) && it.src == selSqr } && showTargetsState.value) Color.Green else Color.Transparent
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape).background(col)
        )
    }
}





