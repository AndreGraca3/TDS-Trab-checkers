package isel.leic.tds.storage

import com.mongodb.ConnectionString
import isel.leic.tds.checkers.model.*
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*
import kotlin.random.Random
import kotlin.test.*

class MongoStorageAsyncTest {
    class Student(val _id: String, val name: String, val address: String)

    val connectionString =
        ConnectionString("mongodb+srv://leic32dgrupo08:1O7idNrAZTFq8YX0@cluster0.r60hmzc.mongodb.net/?retryWrites=true&w=majority")
    val client = KMongo.createClient(connectionString).coroutine
    val database: CoroutineDatabase = client.getDatabase("Checkers")

    @BeforeTest
    fun setup() {
        runBlocking {
            database.getCollection<Student>("test").deleteOneById("1")
        }
    }

    @Test
    fun `Check Cloud Mongo DB connection`() {
        /**
         * Weakly typed.
         */
        // val collection: MongoCollection<Document> = database.getCollection("test")
        runBlocking {
            val collection: CoroutineCollection<Student> = database.getCollection<Student>("test")
            collection.insertOne(Student(Random.nextInt().toString(), "Ze Manel", "Rua Rosa"))
        }
    }

    @Test
    fun `Save a complex entity and load it`() {
        val serializer = object : StringSerializer<Board> {
            override fun write(obj: Board) = obj.serialize()
            override fun parse(input: String) = Board.deserialize(input)
        }
        val fs = MongoStorageAsync<Board>("test", { BoardRun() }, serializer, database)
        runBlocking {
            val board = fs
                .new("1")
                .play(Square(5, 2), Square(4, 3), Player.WHITE)
                .play(Square(2, 3), Square(3, 4), Player.BLACK)
                .play(Square(5, 6), Square(4, 7), Player.WHITE)
                .play(Square(3, 4), Square(5, 2), Player.BLACK)
            fs.save("1", board)
            val actual = fs.load("1")
            assertNotNull(actual)
            assertNotSame(board, actual)
            val iter = actual.moves.iterator()
            board.moves.forEach { assertEquals(it, iter.next()) }
        }
    }
}
