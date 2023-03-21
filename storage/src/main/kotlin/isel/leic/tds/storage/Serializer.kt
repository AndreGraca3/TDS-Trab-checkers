package isel.leic.tds.storage

interface StringSerializer<T> {
    fun write(obj: T) : String
    fun parse(input: String): T
}
