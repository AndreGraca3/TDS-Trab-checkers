package isel.leic.tds.checkers.model


class Column private constructor(var symbol: Char) {

    var index = calcColumnIdx(symbol)

    companion object {
        val values: List<Column> = List(BOARD_DIM) { Column((it + 1 + A_CHAR_CODE).toChar()) }
        operator fun invoke(symbol: Char): Column {
            return values[calcColumnIdx(symbol)]
        }
    }
}

fun Char.toColumnOrNull(): Column? {
    val colidx = calcColumnIdx(this)
    require(colidx != -1) { return null }
    return Column.values[colidx]
}

fun Int.indexToColumn(): Column {
    require(this in 0 until BOARD_DIM) { throw IndexOutOfBoundsException("This index is out of bounds!") }
    return Column.values[this]
}

fun calcColumnIdx(symbol: Char): Int {
    val result = symbol.code - A_CHAR_CODE - 1
    if (symbol.code !in 'a'.code..('a' + BOARD_DIM - 1).code) return -1
    return result
}
