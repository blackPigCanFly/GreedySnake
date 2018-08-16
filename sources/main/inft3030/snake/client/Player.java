package inft3030.snake.client;

import inft3030.snake.game.Direction;
import inft3030.snake.server.Server;

import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Player.
 */
public class Player implements Runnable {
    public final static int DELAY = 100;

    private Server server;
    private final String name;
    private AtomicInteger score;
    private AtomicBoolean alive;
    private AtomicBoolean automated;
    private Semaphore semaphore;
    private Direction direction;

    public Player(Server server, String name, boolean automated) {
        this.server = server;
        this.name = name;
        this.score = new AtomicInteger(0);
        this.alive = new AtomicBoolean(true);
        this.automated = new AtomicBoolean(automated);
        this.semaphore = new Semaphore(1);
        this.direction = Direction.NONE;
    }

    public String getName() {
        return name;
    }

    public void setDirection(Direction direction) {
        try {
            semaphore.acquire();
            this.direction = direction;
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isAutomated() {
        return automated.get();
    }

    public int getScore() {
        return score.get();
    }

    @Override
    public void run() {
        while (alive.get()) {
            try {
                semaphore.acquire();
                if (automated.get()) {
                    direction = Direction.random();
                }
                alive.set(server.moveSnake(name, direction));
                score.set(server.getScore(name));
                semaphore.release();

                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Player player = (Player) obj;
        return name.equals(player.name);
    }

    @Override
    public String toString() {
        String autoStr = automated.get() ? "automated" : "real     ";
        String aliveStr = alive.get() ? "running   " : "terminated";
        return String.format("'%s' %s %s %d",
                name, autoStr, aliveStr, score.get());
    }
}
