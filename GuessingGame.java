package game;
import java.util.Random;

/**
 * Pure game logic for a 1..100 guessing game (no I/O).
 * TDD-friendly: deterministic via withFixedSecret(), injectable NumberSource,
 * and small, explicit outcomes for each guess.
 */
public class GuessingGame {

    /** Outcome of a single guess: simple and type-safe. */
    public enum GuessOutcome { TOO_LOW, TOO_HIGH, CORRECT, OUT_OF_RANGE }

    /** Abstraction for secret generation; default is Random-backed. */
    public interface NumberSource {
        int nextSecret(int minInclusive, int maxInclusive);

        /** Default RNG-backed implementation. */
        class RandomNumberSource implements NumberSource {
            private final Random rnd;
            public RandomNumberSource() { this(new Random()); }
            public RandomNumberSource(Random rnd) { this.rnd = rnd; }
            @Override public int nextSecret(int min, int max) {
                if (min > max) throw new IllegalArgumentException("min > max");
                return rnd.nextInt((max - min) + 1) + min; // inclusive
            }
        }
    }

    public static final int DEFAULT_MIN = 1, DEFAULT_MAX = 100;

    private final int min, max, secret; // immutable config + answer
    private int attempts;               // counts only valid (in-range) guesses
    private boolean finished;           // latched when CORRECT

    /** Default 1..100 range using RNG source. */
    public GuessingGame() { this(DEFAULT_MIN, DEFAULT_MAX, new NumberSource.RandomNumberSource()); }

    /** Inject a custom source (useful for seeding or testing). */
    public GuessingGame(NumberSource src) { this(DEFAULT_MIN, DEFAULT_MAX, src); }

    /** Custom range + source (kept small but validated). */
    public GuessingGame(int min, int max, NumberSource src) {
        if (src == null) throw new IllegalArgumentException("NumberSource required");
        if (min >= max) throw new IllegalArgumentException("min must be < max");
        this.min = min; this.max = max;
        this.secret = src.nextSecret(min, max);
    }

    /** Deterministic factory for unit tests. */
    public static GuessingGame withFixedSecret(int min, int max, int fixed) {
        if (min >= max) throw new IllegalArgumentException("min must be < max");
        if (fixed < min || fixed > max) throw new IllegalArgumentException("secret out of range");
        return new GuessingGame(min, max, (a, b) -> fixed);
    }

    /** Single step: apply a guess and return outcome. */
    public GuessOutcome guess(int value) {
        if (finished) return GuessOutcome.CORRECT;             // idempotent after win
        if (value < min || value > max) return GuessOutcome.OUT_OF_RANGE; // no attempt count

        attempts++;                                            // only valid inputs count
        if (value < secret)  return GuessOutcome.TOO_LOW;
        if (value > secret)  return GuessOutcome.TOO_HIGH;
        finished = true;                                       // exact hit
        return GuessOutcome.CORRECT;
    }

    // minimal API for UI/tests
    public int getAttempts()  { return attempts; }
    public boolean isFinished(){ return finished; }
    public int getMin()       { return min; }
    public int getMax()       { return max; }
    int getSecret()           { return secret; } // package-private for diagnostics/tests
}
