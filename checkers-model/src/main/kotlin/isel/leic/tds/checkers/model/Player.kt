package isel.leic.tds.checkers.model

import java.lang.IllegalArgumentException

enum class Player(val symbol: Char) {
    BLACK('b'), WHITE('w');
    fun opposite() = if(this == BLACK) WHITE else BLACK
}

fun String.toPlayer(): Player {
    require(this.length == 1) {"Illegal symbol with more than a single char."}
    return Player
        .values()
        .find { it.symbol == this[0] }
        ?: throw IllegalArgumentException("There is no player for symbol $this")
}
