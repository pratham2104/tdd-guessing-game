package game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static game.GuessingGame.GuessOutcome.*;

public class GuessingGameTest {

    /** Constructor should reject invalid ranges and secrets. */
    @Test
    void ctorValidatesRangeAndSecret() {
        assertThrows(IllegalArgumentException.class, () -> new GuessingGame(10, 10, (a, b) -> 10));
        assertThrows(IllegalArgumentException.class, () -> GuessingGame.withFixedSecret(1, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> GuessingGame.withFixedSecret(1, 10, 11));
    }

    /** Out-of-range guesses do not count as attempts. */
    @Test
    void outOfRangeDoesNotCountAttempts() {
        GuessingGame g = GuessingGame.withFixedSecret(1, 100, 50);
        assertEquals(OUT_OF_RANGE, g.guess(0));
        assertEquals(0, g.getAttempts());
        assertEquals(OUT_OF_RANGE, g.guess(101));
        assertEquals(0, g.getAttempts());
    }

    /** Valid sequence low -> high -> correct ends game and counts attempts. */
    @Test
    void lowHighThenCorrect() {
        GuessingGame g = GuessingGame.withFixedSecret(1, 100, 42);
        assertEquals(TOO_LOW, g.guess(10));
        assertEquals(1, g.getAttempts());
        assertEquals(TOO_HIGH, g.guess(90));
        assertEquals(2, g.getAttempts());
        assertEquals(CORRECT, g.guess(42));
        assertTrue(g.isFinished());
        assertEquals(3, g.getAttempts());
    }

    /** Further guesses after finish remain CORRECT and stable. */
    @Test
    void guessesAfterFinishRemainStable() {
        GuessingGame g = GuessingGame.withFixedSecret(1, 100, 7);
        assertEquals(CORRECT, g.guess(7));
        int attempts = g.getAttempts();
        assertEquals(CORRECT, g.guess(7));
        assertEquals(attempts, g.getAttempts());
    }

    /** Boundary: guessing min and max directly works. */
    @Test
    void boundaryValues() {
        GuessingGame g1 = GuessingGame.withFixedSecret(1, 100, 1);
        assertEquals(CORRECT, g1.guess(1));
        GuessingGame g2 = GuessingGame.withFixedSecret(1, 100, 100);
        assertEquals(CORRECT, g2.guess(100));
    }

    /** Multiple out-of-range then correct counts only the final attempt. */
    @Test
    void multipleOutOfRangeThenCorrect() {
        GuessingGame g = GuessingGame.withFixedSecret(1, 100, 50);
        g.guess(-5); g.guess(200);
        assertEquals(0, g.getAttempts());
        assertEquals(CORRECT, g.guess(50));
        assertEquals(1, g.getAttempts());
    }
}
