package isel.leic.tds.checkers.model

class Row private constructor(var number: Int) {
    var index = calcRowIdx(number)

    companion object {
        var values = List<Row>(BOARD_DIM) { Row(BOARD_DIM - it) }
        operator fun invoke(idx: Int): Row {
            return values[idx]
        }
    }
}

fun Int.toRowOrNull(): Row? {
    require(this in 1..BOARD_DIM) { return null }
    return Row.values[calcRowIdx(this)]
}

fun Int.indexToRow(): Row {
    require(this in 0 until BOARD_DIM) { throw IndexOutOfBoundsException("This index is out of bounds!") }
    return Row.values[this]
}

fun calcRowIdx(number: Int): Int {
    val result = BOARD_DIM - number
    if (result !in 0..BOARD_DIM - 1) return -1
    return result
}