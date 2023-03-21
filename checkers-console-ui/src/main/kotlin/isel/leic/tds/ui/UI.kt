package isel.leic.tds.ui

import isel.leic.tds.checkers.model.*

fun <T> readCommandsOop(cmds: Map<String, CommandOop<T>>) {
    var context: T? = null
    while(true) {
        /**
         * E.g. words = ["play", "3c", "4d"]
         */
        print("> ")
        val words = readln().trim().split(" ")
        val cmdName = words[0].uppercase()
        val cmd: CommandOop<T>? = cmds[cmdName]
        if(cmd == null) {
            println("Unknown command $cmdName")
            continue
        }
        val args = words.drop(1) // e.g. ["3c", "4d"]
        try {
            context = cmd.action(context, args)
            if(context == null) break
            cmd.show(context)
        }
        catch(e:Exception) {
            println(e.message)
        }
    }
    println("BYE.")
}

fun printBoard(game: Game) {
    println("  +" + "-".repeat(BOARD_DIM * 2 - 1) + "+ Turn:${game.board.turn.opposite().symbol} " +
            "Player: ${game.player.symbol}")
    Square.values.forEach { pos ->
        if(pos.column.index == 0) print("${pos.row.number}" + " ".repeat(BOARD_DIM.toString().length) + "\b".repeat(pos.row.number.toString().length-1) + "|")
        if(game.board.getMove(pos)?.isKing == true) print("${game.board.getMove(pos)?.player?.symbol?.uppercaseChar() ?: if(pos.black) "-" else " "} ")
        else print("${game.board.getMove(pos)?.player?.symbol ?: if(pos.black) "-" else " "} ")
        if (pos.column.index == BOARD_DIM -1) println("\b|")
    }
    println("  +" + "-".repeat(BOARD_DIM * 2 - 1) + "+")
    for (i in 0 until BOARD_DIM) {
        if (i == 0) print(" ".repeat(3))
        print('a'+ i + " ")
    }
    println()

    if(game.board is BoardWinner)
        game.board.winner.apply{ println("Player ${game.board.winner} wins.") }
}