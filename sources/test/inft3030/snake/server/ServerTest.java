package inft3030.snake.server;

import inft3030.snake.client.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Test Server class.
 */
public class ServerTest {

    private Server server;
    private List<Player> players;

    /**
     * Create 100 snakes and 100 players.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // 100*100 board size
        server = new Server(100);
        players = new ArrayList<>();

        // 100 snakes
        for (char c = 'a'; c <= 'j'; c++) {
            for (int i = 0; i <= 9; i++) {
                String name = String.format("%c%d", c, i);
                server.addSnake(name);
                players.add(new Player(server, name, true));
            }
        }
    }

    @Test
    public void login() {
        assertTrue(server.login("a1", "a1"));
        assertTrue(server.login("z9", "z9"));
        assertFalse(server.login("none", "none"));
    }

    @Test
    public void getScore() {
        assertEquals(0, server.getScore("a1"));
        assertEquals(0, server.getScore("j9"));
    }

    /**
     * Simulate 100 players playing on server.
     *
     * @throws InterruptedException
     */
    @Test
    public void moveSnakes() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(players.size());
        for (Player player : players) {
            service.execute(player);
        }

        service.awaitTermination(120, TimeUnit.SECONDS);
        for (Player player : players) {
            System.out.println(player);
        }
    }

}