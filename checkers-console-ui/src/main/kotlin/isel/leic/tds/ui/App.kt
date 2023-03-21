package isel.leic.tds.ui

import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardRun
import isel.leic.tds.storage.FileStorage
import isel.leic.tds.storage.MongoStorage
import isel.leic.tds.storage.StringSerializer
import com.mongodb.ConnectionString
import org.litote.kmongo.KMongo
import com.mongodb.client.MongoDatabase

fun main() {
    val tttFolder = "out"

    val collectionName = "TestGame"
    val connectionString =
        ConnectionString("mongodb+srv://leic32dgrupo08:1O7idNrAZTFq8YX0@cluster0.r60hmzc.mongodb.net/?retryWrites=true&w=majority")
    val client = KMongo.createClient(connectionString)
    val database: MongoDatabase = client.getDatabase("Checkers") // creates a database with given name.

    val serializer = object : StringSerializer<Board> {
        override fun write(obj: Board) = obj.serialize()
        override fun parse(input: String) =  Board.deserialize(input)
    }

    val fs = FileStorage<String,Board>(tttFolder, { BoardRun() }, serializer)

    //val fs = MongoStorage<Board>(collectionName,{ BoardRun ()},serializer,database)

    readCommandsOop(mapOf(
        "EXIT" to QuitCommandOop,
        "START" to StartCommandOop(fs),
        "PLAY" to PlayCommandOop(fs),
        "REFRESH" to RefreshCommandOop(fs),
    ))
}
