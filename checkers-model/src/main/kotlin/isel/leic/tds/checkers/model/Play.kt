package isel.leic.tds.checkers.model

data class Play (val src: Square, val captured: Square?, var dst: Square, val player : Player)