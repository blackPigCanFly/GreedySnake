package inft3030.snake.client;

import inft3030.snake.server.Server;

import javax.swing.*;

/**
 * Login runnable thread.
 */
public class Loginer implements Runnable {
    /**
     * Game server.
     */
    private Server server;

    /**
     * Buffer to cache login players.
     */
    private Buffer buffer;

    /**
     * Username.
     */
    private String username;

    /**
     * Password.
     */
    private String password;

    private boolean automated;

    /**
     * Constructor.
     *
     * @param server
     * @param buffer
     * @param username
     * @param password
     */
    public Loginer(Server server, Buffer buffer,
                   String username, String password,
                   boolean automated) {
        this.server = server;
        this.buffer = buffer;
        this.username = username;
        this.password = password;
        this.automated = automated;
    }

    /**
     * Run, to login.
     */
    @Override
    public void run() {
        if (server.login(username, password)) {
            buffer.append(new Player(server, username, automated));
        } else {
            JOptionPane.showMessageDialog(null, "user or password is wrong");
        }
    }
}
