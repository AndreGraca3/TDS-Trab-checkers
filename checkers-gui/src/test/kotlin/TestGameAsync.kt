package isel.leic.tds.gui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.storage.FileStorage
import isel.leic.tds.storage.FileStorageAsync
import isel.leic.tds.storage.StringSerializer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class TestGameState {

    val name = "Teste1"
    val folder = "out"
    private val serializer = object : StringSerializer<Board> {
        override fun write(obj: Board) = obj.serialize()
        override fun parse(input: String) = Board.deserialize(input)
    }

    @BeforeTest
    fun setup(): Unit =  runBlocking {
        val fs1 = FileStorageAsync<String, Board>(folder, { BoardRun() }, serializer)
        if (fs1.load(name) != null) fs1.delete(name)
    }


    @Test fun `Start game Async and make a play Async`(): Unit = runBlocking {
        val newGame = startGame(name,FileStorageAsync("out", { BoardRun() }, BoardSerializer))
        assertNotNull(newGame.board)
        assertEquals(newGame.player, Player.WHITE)
        assertEquals(newGame.name,name)

        val play1 = newGame.play(FileStorageAsync("out",{ BoardRun()}, BoardSerializer),Square(5,0),Square(4,1))
        assertEquals(play1.player,Player.WHITE)
        assertNotEquals(play1.board,newGame.board)
    }

}