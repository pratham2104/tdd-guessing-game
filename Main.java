package game;

import java.util.Scanner;

public class Main {

    /** Entry point for console play. */
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("🎯 TDD Guessing Game (1–100)");
            GuessingGame game = new GuessingGame();

            while (!game.isFinished()) {
                System.out.print("Enter a number (" + game.getMin() + "-" + game.getMax() + "): ");
                String line = in.nextLine().trim();

                int guess;
                try {
                    guess = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                    continue;
                }

                switch (game.guess(guess)) {
                    case OUT_OF_RANGE -> System.out.println("Out of range. Try again.");
                    case TOO_LOW     -> System.out.println("Too low. Guess higher!");
                    case TOO_HIGH    -> System.out.println("Too high. Guess lower!");
                    case CORRECT     -> System.out.println("✅ Correct in " + game.getAttempts() + " attempts!");
                }
            }
            System.out.println("Thanks for playing!");
        }
    }
}
