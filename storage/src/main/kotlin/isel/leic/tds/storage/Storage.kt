package isel.leic.tds.storage


interface Storage<K, T> {
    val serializer: StringSerializer<T>
    fun new(id: K) : T
    fun load(id: K) : T?
    fun save(id: K, obj: T)
    fun delete(id: K)
}
