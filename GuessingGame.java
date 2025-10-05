package game;

import java.util.Random;

public class GuessingGame {

    /** Result of a single guess. */
    public enum GuessOutcome { TOO_LOW, TOO_HIGH, CORRECT, OUT_OF_RANGE }

    /** Functional interface for secret number providers. */
    @FunctionalInterface
    public interface NumberSource {
        int nextSecret(int minInclusive, int maxInclusive);

        /** RNG-backed implementation used by default. */
        class RandomNumberSource implements NumberSource {
            private final Random rnd;
            public RandomNumberSource() { this(new Random()); }
            public RandomNumberSource(Random rnd) { this.rnd = rnd; }

            @Override
            public int nextSecret(int min, int max) {
                if (min > max) throw new IllegalArgumentException("min > max");
                return rnd.nextInt((max - min) + 1) + min; // inclusive
            }
        }
    }

    public static final int DEFAULT_MIN = 1, DEFAULT_MAX = 100;

    private final int min, max, secret;
    private int attempts;
    private boolean finished;

    /** Default constructor: 1..100 with RNG source. */
    public GuessingGame() {
        this(DEFAULT_MIN, DEFAULT_MAX, new NumberSource.RandomNumberSource());
    }

    /** Create game with a custom source (useful for seeded RNG). */
    public GuessingGame(NumberSource src) {
        this(DEFAULT_MIN, DEFAULT_MAX, src);
    }

    /** Create game with a custom range and source. */
    public GuessingGame(int min, int max, NumberSource src) {
        if (src == null) throw new IllegalArgumentException("NumberSource required");
        if (min >= max) throw new IllegalArgumentException("min must be < max");
        this.min = min; this.max = max;
        this.secret = src.nextSecret(min, max);
    }

    /** Factory for deterministic tests with fixed secret. */
    public static GuessingGame withFixedSecret(int min, int max, int fixed) {
        if (min >= max) throw new IllegalArgumentException("min must be < max");
        if (fixed < min || fixed > max) throw new IllegalArgumentException("secret out of range");
        return new GuessingGame(min, max, (a, b) -> fixed);
    }

    /** Apply a guess and return the outcome. */
    public GuessOutcome guess(int value) {
        if (finished) return GuessOutcome.CORRECT;
        if (value < min || value > max) return GuessOutcome.OUT_OF_RANGE;

        attempts++;
        if (value < secret) return GuessOutcome.TOO_LOW;
        if (value > secret) return GuessOutcome.TOO_HIGH;
        finished = true;
        return GuessOutcome.CORRECT;
    }

    /** Get total valid attempts so far. */
    public int getAttempts() { return attempts; }

    /** Check if the game has ended. */
    public boolean isFinished() { return finished; }

    /** Get lower bound of the range. */
    public int getMin() { return min; }

    /** Get upper bound of the range. */
    public int getMax() { return max; }

    /** For diagnostics/tests only. */
    int getSecret() { return secret; }
}
