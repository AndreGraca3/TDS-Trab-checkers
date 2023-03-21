package isel.leic.tds.checkers.model

import kotlin.IllegalArgumentException
import kotlin.test.*


class TestBoard {

    @Test fun `We cannot instantiate a Square with coordinates out of board size , using indexes`() {

        /**
           Test if instance of Square fails with wrong coordinate for Row.
         */
        val ex1 = assertFailsWith<IllegalArgumentException> {
            Square(9,4)
        }
        assertEquals(" Illegal Row index must be between 0 and $LAST_COORD ",ex1.message)

        /**
           Test if instance of Square fails with wrong coordinate for Column.
         */
        val ex2 = assertFailsWith<IllegalArgumentException> {
            Square(3,-2)
        }
        assertEquals(" Illegal Column index must be between 0 and $LAST_COORD ",ex2.message)
    }

    @Test fun `Test invalid instances of Player`(){

        /**
           Testing if we can instantiate a player with name with more than 2 characters.
         */
        val ex1 = assertFailsWith<IllegalArgumentException> {
            "bw".toPlayer()
        }
        assertEquals("Illegal symbol with more than a single char.",ex1.message)

        /**
           Testing if we can instantiate a player with name that's not w or b.
         */
        val ex2 = assertFailsWith< java.lang.IllegalArgumentException> {
            "t".toPlayer()
        }
        assertEquals("There is no player for symbol t",ex2.message)
    }

    @Test fun `We cannot play on a board that has winner` () {
        val ex = assertFailsWith<IllegalStateException> {
            BoardWinner(emptyList(),"w".toPlayer())
                .play(Square(3,4),Square(4,5),"b".toPlayer())

        }
        assertEquals("Player WHITE has already won this game.Therefore you can't play in it.",ex.message)
    }

    @Test fun `Testing mandatory moves` () {
        val ex1 = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5, 2), Square(4, 3), Player.WHITE) //play 3c 4d
                .play(Square(2, 3), Square(3, 4), Player.BLACK) //play 6d 5e
                .play(Square(5, 6), Square(4, 7), Player.WHITE) //play 3g 4h
                .play(Square(3, 4), Square(5, 2), Player.BLACK) //play 5e 3c
                .play(Square(5, 4), Square(4, 3), Player.WHITE) //play 3e 4d
        }
        assertEquals("There is mandatory capture in 2d", ex1.message)
    }

    @Test fun `Testing wrong player turn`() {
        val ex1 = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5, 2), Square(4, 3), Player.WHITE)
                .play(Square(5, 6), Square(4, 7), Player.WHITE)
        }
        assertEquals("It's not your turn.", ex1.message)
    }

    @Test fun `Testing wrong moves`() {
        val ex1 = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(5, 6), Square(4, 7), Player.WHITE)
                .play(Square(5, 4), Square(4, 5), Player.BLACK)
        }
        assertEquals("position 3e does not have your piece", ex1.message)

        val ex2 = assertFailsWith<IllegalArgumentException> {
            BoardRun()
                .play(Square(6, 3), Square(5, 4), Player.WHITE)
        }
        assertEquals("Position 3e is occupied.", ex2.message)
    }
}


