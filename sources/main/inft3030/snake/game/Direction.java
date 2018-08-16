package inft3030.snake.game;

import java.util.Random;

/**
 * Snake moving direction.
 */
public enum Direction {
    UP(0), RIGHT(1), DOWN(2), LEFT(3), NONE(-1);

    private final int value;

    Direction(int value) {
        this.value = value;
    }


    public static Direction random() {
        Random random = new Random();
        if (random.nextDouble() < 0.8) {
            return NONE;
        } else {
            Direction[] values = values();
            return values[random.nextInt(values.length)];
        }
    }

    @Override
    public String toString() {
        if (this == UP) {
            return "^";
        } else if (this == DOWN) {
            return "v";
        } else if (this == RIGHT) {
            return ">";
        } else if (this == LEFT) {
            return "<";
        } else {
            return " ";
        }
    }
}
