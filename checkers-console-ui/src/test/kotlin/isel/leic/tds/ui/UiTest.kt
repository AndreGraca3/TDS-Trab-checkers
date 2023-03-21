package isel.leic.tds.ui

import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardRun
import isel.leic.tds.checkers.model.Player
import isel.leic.tds.checkers.model.Square
import isel.leic.tds.storage.FileStorage
import isel.leic.tds.storage.StringSerializer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.*

class UiTest {

    private val folder = "outTest"

    private val gameId1 = "newGame"

    private val serializer = object : StringSerializer<Board> {
        override fun write(obj: Board) = obj.serialize()
        override fun parse(input: String) = Board.deserialize(input)
    }

    @BeforeTest
    fun setup() {
        val fs1 = FileStorage<String, Board>(folder, { BoardRun() }, serializer)
        if (fs1.load(gameId1) != null) fs1.delete(gameId1)
        fs1.new(gameId1)
    }

    @Test fun `Check command action returning null finishes de readCommands loop`() {
        redirectInOut(listOf("exit")) {
            readCommandsOop(
                mapOf(
                    "EXIT" to QuitCommandOop
                )
            )
        }
    }

    @Test fun `Check invalid command`() {
        val lines = redirectInOut(listOf("dummy", "exit")) {
            readCommandsOop(
                mapOf(
                    "EXIT" to QuitCommandOop
                )
            )
        }
        assertEquals("Unknown command DUMMY", lines[0])
        lines.forEach { println(it) }
    }

    @Test fun `Checking if we can't give a name for a game to start`() {

        val startCommand = "start ".trim().split(" ").drop(1)

        val start = assertFailsWith<IllegalArgumentException> {
            StartCommandOop(FileStorage(folder, { BoardRun() }, serializer)).action(null, startCommand)
        }

        assertEquals("You should provide a unique name for the new game!", start.message)

    }

    @Test fun `Checking if start Command returns error message if we try to start a new game before exiting the current one `() {
        val game = startGame(gameId1, FileStorage(folder, { BoardRun() }, serializer))

        val startCommand = "start $gameId1".trim().split(" ")

        val start = assertFailsWith<IllegalArgumentException> {
            StartCommandOop(FileStorage(folder, { BoardRun() }, serializer)).action(game, startCommand)
        }

        assertEquals("Game already started", start.message)
    }

    @Test fun `Checking if executing refresh command on a null game returns an error message`(){

        val refreshCommand = "refresh".trim().split(" ")

        val refresh = assertFailsWith<IllegalArgumentException>{
            RefreshCommandOop(FileStorage(folder, { BoardRun() }, serializer)).action(null,refreshCommand)
        }

        assertEquals("You should start a new game before playing",refresh.message)

    }

    @Test fun `Checking checkersPlays returns an error message when not enough coordinates`(){

        val game = startGame(gameId1, FileStorage(folder, { BoardRun() }, serializer))

        val args = "play 3a".trim().split(" ").drop(1)

        val play = assertFailsWith<IllegalArgumentException>{
            checkersPlay(FileStorage(folder, { BoardRun() }, serializer),game,args)
        }

        assertEquals("Missing positions\nUse: PLAY <fromSquare> <toSquare>",play.message)
    }

    @Test fun `Checking checkersPlays returns an error message when game is null`(){

        val args = "play 3a 4b".trim().split(" ").drop(1)

       val play = assertFailsWith<IllegalArgumentException> {
           checkersPlay(FileStorage(folder, { BoardRun () }, serializer),null,args)
       }

        assertEquals("You should start a new game before playing",play.message)
    }

    fun redirectInOut(stmts: List<String>, block: () -> Unit): List<String> {
        /**
         * First store the standard input and output and redirect to memory.
         */
        val oldOut = System.out
        val oldIn = System.`in`
        val mem = ByteArrayOutputStream()

        System.setOut(PrintStream(mem)) // defines output to memory.
        System.setIn(ByteArrayInputStream("dummy\nexit\n".toByteArray()))

        /**
         * Execute given block.
         */
        block()
        /**
         * Restore standard input and output and return the resulting output lines.
         */
        System.setOut(oldOut)
        System.setIn(oldIn)
        return mem.toString().split(System.lineSeparator()).map { if (it.startsWith("> ")) it.drop(2) else it }

    }
}