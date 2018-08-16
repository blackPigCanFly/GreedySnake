package inft3030.snake.game;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test snake class.
 */
public class SnakeTest {

    private Snake snake;

    @Before
    public void setUp() throws Exception {
        // mock game
        Game game = mock(Game.class);
        when(game.isFood(new Position(1, 0))).thenReturn(true);
        when(game.getSnakes()).thenReturn(new ArrayList<>());
        when(game.getSize()).thenReturn(4);

        snake = new Snake("s", new Position(0, 0), game);
    }

    @Test
    public void getName() {
        assertEquals("s", snake.getName());
    }

    @Test
    public void isDead() {
        assertEquals(false, snake.isDead());
    }

    @Test
    public void getScore() {
        assertEquals(0, snake.getScore());
    }

    @Test
    public void getHead() {
        assertEquals(new Position(0, 0), snake.getHead());
    }

    @Test
    public void getPositions() {
        assertEquals(Arrays.asList(new Position(0, 0)), snake.getPositions());
    }

    @Test
    public void isCollided() {
        assertEquals(false, snake.isCollided());
    }

    /**
     * Move snake, assert result.
     */
    @Test
    public void move() {
        assertEquals(0, snake.getScore());

        // move to right and eat a food
        snake.move(Direction.RIGHT);
        assertEquals(new Position(1, 0), snake.getHead());
        assertEquals(1, snake.getScore());

        snake.move(Direction.RIGHT);
        assertEquals(new Position(2, 0), snake.getHead());
        assertEquals(1, snake.getScore());

        // back to (0, 0)
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        assertEquals(new Position(0, 0), snake.getHead());

        // move down
        snake.move(Direction.DOWN);
        assertEquals(new Position(0, 1), snake.getHead());
        snake.move(Direction.DOWN);
        snake.move(Direction.DOWN);
        snake.move(Direction.DOWN);
        assertEquals(new Position(0, 0), snake.getHead());
    }
}