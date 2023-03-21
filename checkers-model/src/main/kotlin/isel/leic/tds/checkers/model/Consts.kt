package isel.leic.tds.checkers.model

const val BOARD_DIM = 8
const val MAX_SQUARES = BOARD_DIM * BOARD_DIM
const val A_CHAR_CODE = 96
const val SQUARES_TO_FILL = (BOARD_DIM * BOARD_DIM  - 2 * BOARD_DIM ) / 2 // 24 squares for each side
const val LAST_COORD = BOARD_DIM - 1
val INITIAL_PIECES:List<Move> = initialPieces()

private fun initialPieces(): List<Move> {
    var list = emptyList<Move>()
    for (i in 0 until SQUARES_TO_FILL) {
        if (Square.values[MAX_SQUARES - 1 -i].black) list += Move(Player.WHITE, Square.values[MAX_SQUARES - 1 -i], false)
        if (Square.values[i].black) list += Move(Player.BLACK, Square.values[i], false) //the order is important for printboard's turn
    }
    return list
}
