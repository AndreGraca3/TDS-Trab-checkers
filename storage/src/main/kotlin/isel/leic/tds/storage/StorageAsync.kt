package isel.leic.tds.storage

interface StorageAsync<K, T> {
    val serializer: StringSerializer<T>
    suspend fun new(id: K) : T
    suspend fun load(id: K) : T?
    suspend fun save(id: K, obj: T)
    suspend fun delete(id: K)
}