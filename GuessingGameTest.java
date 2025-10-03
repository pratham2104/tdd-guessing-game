package game;
import org.junit.jupiter.api.Test;

import static game.GuessingGame.GuessOutcome.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD tests define expected behavior:
 * - constructor/range validation
 * - out-of-range guesses don't count attempts
 * - typical flow: low -> high -> correct
 * - idempotency after success
 */
public class GuessingGameTest {

    @Test
    void ctorValidatesRangeAndSecret() {
        assertThrows(IllegalArgumentException.class, () -> new GuessingGame(10, 10, (a,b) -> 10));
        assertThrows(IllegalArgumentException.class, () -> GuessingGame.withFixedSecret(1, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> GuessingGame.withFixedSecret(1, 10, 11));
    }

    @Test
    void outOfRangeDoesNotIncrementAttempts() {
        GuessingGame g = GuessingGame.withFixedSecret(1, 100, 50);
        assertEquals(OUT_OF_RANGE, g.guess(0));
        assertEquals(0, g.getAttempts());
        assertEquals(OUT_OF_RANGE, g.guess(101));
        assertEquals(0, g.getAttempts());
    }

    @Test
    void lowHighThenCorrectTracksAttemptsAndFinish() {
        GuessingGame g = GuessingGame.withFixedSecret(1, 100, 42);
        assertEquals(TOO_LOW, g.guess(10));
        assertEquals(1, g.getAttempts());
        assertEquals(TOO_HIGH, g.guess(90));
        assertEquals(2, g.getAttempts());
        assertEquals(CORRECT, g.guess(42));
        assertTrue(g.isFinished());
        assertEquals(3, g.getAttempts());
    }

    @Test
    void guessesAfterFinishRemainCorrectAndStable() {
        GuessingGame g = GuessingGame.withFixedSecret(1, 100, 7);
        assertEquals(CORRECT, g.guess(7));
        int attempts = g.getAttempts();
        assertEquals(CORRECT, g.guess(7));       // no change
        assertEquals(attempts, g.getAttempts());
        assertTrue(g.isFinished());
    }
}
