package inft3030.snake.game;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test Position class.
 */
public class PositionTest {
    private Position pos;

    @Before
    public void setUp() throws Exception {
        pos = new Position(9, 9);
    }

    @Test
    public void getX() {
        assertEquals(9, pos.getX());
    }

    @Test
    public void getY() {
        assertEquals(9, pos.getY());
    }
}