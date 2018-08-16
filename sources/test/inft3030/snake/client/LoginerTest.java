package inft3030.snake.client;

import inft3030.snake.server.Server;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test Loginer class.
 */
public class LoginerTest {

    @Test
    public void login() throws Exception {
        String username = "username";
        String password = "password";
        Buffer buffer = new Buffer(1);

        // mock server
        Server server = mock(Server.class);
        when(server.login(username, password)).thenReturn(true);

        // login
        Loginer loginer = new Loginer(server, buffer, username, password, true);
        loginer.run();

        // test
        Player player = buffer.take();
        assertEquals(player.getName(), username);
    }
}