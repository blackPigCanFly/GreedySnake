package inft3030.snake.client;

import inft3030.snake.game.Direction;
import inft3030.snake.server.Server;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test Player class.
 */
public class PlayerTest {
    private Player player;

    @Before
    public void setUp() throws Exception {
        // mock server
        Server server = mock(Server.class);
        when(server.moveSnake(any(String.class), any(Direction.class)))
                .thenReturn(false);
        when(server.getScore(any(String.class))).thenReturn(10);

        player = new Player(server, "name", true);
    }

    @Test
    public void getName() {
        assertEquals("name", player.getName());
    }

    @Test
    public void isAutomated() {
        assertEquals(true, player.isAutomated());
    }

    @Test
    public void getScore() {
        assertEquals(0, player.getScore());
    }

    @Test
    public void play() throws Exception {
        // playing
        player.run();
        Thread.sleep(Player.DELAY * 2);

        assertEquals(10, player.getScore());
    }

}