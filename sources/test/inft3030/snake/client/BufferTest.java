package inft3030.snake.client;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Test Buffer class.
 */
public class BufferTest {
    private Player player;
    private Buffer buffer;

    /**
     * Initialise.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        player = mock(Player.class);
        buffer = new Buffer(1);
    }

    @Test
    public void appendAndTake() throws Exception {
        new Thread(() -> buffer.append(player)).start();
        assertEquals(player, buffer.take());
    }

}