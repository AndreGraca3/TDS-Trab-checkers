package isel.leic.tds.checkers.model

class Square private constructor(val sqrNum: Int) {

    val row = (sqrNum / BOARD_DIM).indexToRow()
    val column = (sqrNum % BOARD_DIM).indexToColumn()
    val black = (this.row.index + this.column.index) % 2 != 0

    companion object {
        val values: List<Square> = List(MAX_SQUARES) { Square(it) }
        operator fun invoke(rowIdx: Int, columnIdx: Int): Square {
            require(rowIdx in 0 until BOARD_DIM) { " Illegal Row index must be between 0 and $LAST_COORD "}
            require(columnIdx in 0 until BOARD_DIM) { " Illegal Column index must be between 0 and $LAST_COORD "}
            val calc = rowIdx * BOARD_DIM + columnIdx
            return values[calc]
        }

        operator fun invoke(row: Row, column: Column): Square {
            require(row in Row.values){ " Illegal Row must be between 0 and $LAST_COORD "}
            require(column in Column.values){ " Illegal Column index must be between 0 and ${LAST_COORD.toChar()} "}
            val calc = row.index * BOARD_DIM + column.index
            return values[calc]
        }
    }

    override fun toString(): String {
        return "${this.row.number}${this.column.symbol}"
    }
}

fun String.toSquareOrNull(): Square? {
    var stringtoInt = ""
    var i = 0

    while (i < this.length) {
        if (this[i].isDigit()) {
            stringtoInt += this[i++]
        } else break
    }

    if (this.length < 2 || stringtoInt.length < 1 || this.length > stringtoInt.length + 1) return null
    val rowIdx = calcRowIdx(stringtoInt.toInt())
    val columnIdx = calcColumnIdx(this[i])
    if (columnIdx == -1 || rowIdx == -1) return null
    return Square(rowIdx, columnIdx)
}
