package inft3030.snake.game;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test Game class.
 */
public class GameTest {
    private Game game;

    @Before
    public void setUp() throws Exception {
        game = new Game(4);
        game.setFoods(Arrays.asList(new Position(1, 1)));
        game.setSnakes(Arrays.asList(new Snake("name", new Position(2, 2), game)));
    }

    @Test
    public void isOver() {
        assertEquals(false, game.isOver());
    }

    @Test
    public void getSize() {
        assertEquals(4, game.getSize());
    }

    @Test
    public void isFood() {
        assertEquals(true, game.isFood(new Position(1, 1)));
        assertEquals(false, game.isFood(new Position(0, 0)));
    }

    @Test
    public void moveSnake() {
        assertEquals(true, game.moveSnake("name", Direction.UP));
        assertEquals(false, game.moveSnake("none", Direction.UP));
    }
}