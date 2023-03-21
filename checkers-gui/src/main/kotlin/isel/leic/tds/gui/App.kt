package isel.leic.tds.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.mongodb.ConnectionString
import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardRun
import isel.leic.tds.storage.FileStorageAsync
import isel.leic.tds.storage.MongoStorageAsync
import isel.leic.tds.storage.StringSerializer
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main() {
    val connectionString =
        ConnectionString("mongodb+srv://leic32dgrupo08:1O7idNrAZTFq8YX0@cluster0.r60hmzc.mongodb.net/?retryWrites=true&w=majority")
    val client = KMongo.createClient(connectionString).coroutine
    val database: CoroutineDatabase = client.getDatabase("Checkers")
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Checkers",
            icon = painterResource("piece_bk.png"),
            state = WindowState(
                position = WindowPosition(Alignment.Center),
                size = DpSize(450.dp, 510.dp)   //only works with 8x8 Board
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(245, 125, 45))
            ) {
                val scope = rememberCoroutineScope()
                val game = remember {
                    GameState(
                        scope,
                        //MongoStorageAsync<Board>("TestGame", { BoardRun() }, BoardSerializer, database)
                        FileStorageAsync<String, Board>("out", { BoardRun() }, BoardSerializer)
                    )
                }
                CheckersMenu(game::startGame, game::refresh)
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BoardLetters()
                    Row() {
                        BoardNumbers(game.board?.player)
                        BoardView(game.board?.board, game.selectedSquare, game.board?.player, game::chooseSquare)
                    }
                    DialogMessage(game.message, game::dismissMessage)
                    GameInfoTexts(game)
                }
            }
        }
    }
}

object BoardSerializer : StringSerializer<Board> {
    override fun write(obj: Board) = obj.serialize()
    override fun parse(input: String) = Board.deserialize(input)
}