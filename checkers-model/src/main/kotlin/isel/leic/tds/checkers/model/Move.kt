package isel.leic.tds.checkers.model

data class Move(val player: Player, val square: Square, val isKing:Boolean = false) {
    fun serialize() = "${player.symbol}${square.row.number}${square.column.symbol}${if (isKing) "t" else "f"}"
    companion object {
        /**
         * @param input Must have 4 characters corresponding to b|w row, col and king!
         */
        fun deserialize(input: String) : Move {
            require(input.length == 4) { "The input string must have 4 characters corresponding to b|w row, col and king!" }
            return Move(
                input[0].toString().toPlayer(),
                Square(calcRowIdx(input[1].toString().toInt()), calcColumnIdx(input[2])),
                input[3].toString() == "t"
            )
        }
    }
}