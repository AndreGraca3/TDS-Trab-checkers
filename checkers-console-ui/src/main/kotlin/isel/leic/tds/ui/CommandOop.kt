package isel.leic.tds.ui


interface CommandOop<T> {
    val syntax: String
    fun show(ctx: T) : Unit
    fun action(ctx: T?, args: List<String>): T?
}
