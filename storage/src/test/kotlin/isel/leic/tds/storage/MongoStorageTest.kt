package isel.leic.tds.storage

import com.mongodb.ConnectionString
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.deleteOneById
import isel.leic.tds.checkers.model.*
import org.litote.kmongo.getCollection
import kotlin.test.*

class MongoStorageTest {

    class Game(val _id: String, val name: String, val Number : Int )

    val connectionString =
        ConnectionString("mongodb+srv://leic32dgrupo08:1O7idNrAZTFq8YX0@cluster0.r60hmzc.mongodb.net/?retryWrites=true&w=majority")
    val client = KMongo.createClient(connectionString)
    val database: MongoDatabase = client.getDatabase("Checkers") // creates a database with given name.

    @BeforeTest fun setup() {
        database.getCollection("Board").deleteOneById("super")
        database.getCollection("test").deleteOneById("1")
    }

    @Test fun `Check Cloud Mongo DB connection`() {
        val collection: MongoCollection<Game> = database.getCollection("test", Game::class.java) // creates a collection in the Checkers database.
        collection.insertOne(Game("1", "Game_1", 6))
    }

    @Test fun `Save a complex entity and load it`() {
        val serializer = object : StringSerializer<Board> {
            override fun write(obj: Board) = obj.serialize()
            override fun parse(input: String) =  Board.deserialize(input)
        }
        val fs = MongoStorage<Board>("Board", { BoardRun() }, serializer, database)
        val board = fs
            .new("super")
            .play(Square(5, 2), Square(4, 3), Player.WHITE)
            .play(Square(2, 3), Square(3, 4), Player.BLACK)
            .play(Square(5, 6), Square(4, 7), Player.WHITE)
            .play(Square(3, 4), Square(5, 2), Player.BLACK)
            .play(Square(6, 3), Square(4, 1), Player.WHITE)
            .play(Square(2, 1), Square(3, 0), Player.BLACK) //play 6b 5a
            .play(Square(4, 1), Square(3, 2), Player.WHITE) //play 4b 5c
            .play(Square(2, 5), Square(3, 4), Player.BLACK) //play 6f 5e
            .play(Square(6, 1), Square(5, 2), Player.WHITE) //play 2b 3c
            .play(Square(2, 7), Square(3, 6), Player.BLACK) //play 6h 5g
            .play(Square(4, 7), Square(2, 5), Player.WHITE) //play 4h 6f
            .play(Square(2, 5), Square(4, 3), Player.WHITE) //play 6f 4d
            .play(Square(1, 2), Square(2, 1), Player.BLACK) //play 7c 6b
            .play(Square(5, 4), Square(4, 5), Player.WHITE) //play 3e 4f
            .play(Square(3, 0), Square(4, 1), Player.BLACK) //play 5a 4b
            .play(Square(5, 2), Square(3, 0), Player.WHITE) //play 3c 5a
            .play(Square(3, 0), Square(1, 2), Player.WHITE) //play 5a 7c
            .play(Square(0, 3), Square(2, 1), Player.BLACK) //play 8d 6b
            .play(Square(4, 5), Square(3, 6), Player.WHITE) //play 4f 5g
            .play(Square(1, 4), Square(2, 5), Player.BLACK) //play 7e 6f
            .play(Square(3, 6), Square(1, 4), Player.WHITE) //play 5g 7e
            .play(Square(0, 5), Square(2, 3), Player.BLACK) //play 8f 6d
            .play(Square(2, 3), Square(4, 1), Player.BLACK) //play 6d 4b
            .play(Square(5, 0), Square(3, 2), Player.WHITE) //play 3a 5c
            .play(Square(2, 1), Square(3, 0), Player.BLACK) //play 6b 5a
            .play(Square(3, 2), Square(2, 3), Player.WHITE) //play 5c 6d
            .play(Square(0, 1), Square(1, 2), Player.BLACK) //play 8b 7c
            .play(Square(2, 3), Square(0, 1), Player.WHITE) //play 6d 8b
            .play(Square(3, 0), Square(4, 1), Player.BLACK) //play 5a 4b
            .play(Square(6, 5), Square(5, 6), Player.WHITE) //play 2f 3g
            .play(Square(4, 1), Square(5, 2), Player.BLACK) //play 4b 3c
            .play(Square(4, 3), Square(6, 1), Player.WHITE) //play 4d 2b
            .play(Square(1, 0), Square(2, 1), Player.BLACK) //play 7a 6b
            .play(Square(0, 1), Square(1, 0), Player.WHITE) //play 8b 7a
            .play(Square(1, 6), Square(2, 5), Player.BLACK) //play 7g 6f
            .play(Square(1, 0), Square(3, 2), Player.WHITE) //play 7a 5c
            .play(Square(0, 7), Square(1, 6), Player.BLACK) //play 8h 7g
            .play(Square(3, 2), Square(4, 3), Player.WHITE) //play 5c 4d
            .play(Square(1, 6), Square(2, 7), Player.BLACK) //play 7g 6h
            .play(Square(4, 3), Square(0, 7), Player.WHITE) //play 4d 8h
            .play(Square(2, 7), Square(3, 6), Player.BLACK) //play 6h 5g
            .play(Square(0, 7), Square(3, 4), Player.WHITE) //play 8h 5e
            .play(Square(3, 6), Square(4, 7), Player.BLACK) //play 5g 4h
            .play(Square(6, 1), Square(5, 2), Player.WHITE) //play 2b 3c
            .play(Square(4, 7), Square(6, 5), Player.BLACK) //play 4h 2f
            .play(Square(7, 4), Square(5, 6), Player.WHITE) //play 1e 3g

        fs.save("super", board)

        val actual = fs.load("super")

        assertNotNull(actual)
        assertNotSame(board, actual)

        assertIs<BoardWinner>(actual)
        assertIsNot<BoardRun>(actual)

        val iter = actual.moves.iterator()
        board.moves.forEach { assertEquals(it, iter.next()) }
    }
}
