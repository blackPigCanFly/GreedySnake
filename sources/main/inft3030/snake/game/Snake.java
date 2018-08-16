package inft3030.snake.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Snake in game world.
 */
public class Snake {

    /**
     * snake's name
     */
    private String name;

    /**
     * snake occupied positions
     */
    private List<Position> positions;

    /**
     * snake current direction
     */
    private Direction direction;

    /**
     * is snake dead
     */
    private boolean dead;

    /**
     * current score
     */
    private AtomicInteger score;

    /**
     * game
     */
    private Game game;


    /**
     * constructor
     *
     * @param name
     * @param initPos
     * @param game
     */
    public Snake(String name, Position initPos, Game game) {
        this.name = name;
        this.game = game;
        this.positions = Collections.synchronizedList(new ArrayList<>());
        this.positions.add(initPos);
        this.direction = Direction.NONE;
        this.dead = false;
        this.score = new AtomicInteger(0);
    }

    /**
     * return snake's name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * is snake dead
     *
     * @return
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Get score value.
     *
     * @return
     */
    public int getScore() {
        return score.get();
    }

    /**
     * return snake's head
     *
     * @return
     */
    public Position getHead() {
        return positions.get(0);
    }

    /**
     * return snake occupied positions
     *
     * @return
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * Is snake collided with other objects
     *
     * @return
     */
    public boolean isCollided() {
        Position head = getHead();

        // is collided with other snakes
        for (Snake s : game.getSnakes()) {
            if (s != this && !s.isDead()) {
                List<Position> positions = s.getPositions();
                synchronized (positions) {
                    for (Position p : positions) {
                        if (head.equals(p)) {
                            System.out.printf("%s collided with %s at %s\n", this, s, p);
                            return true;
                        }
                    }
                }
            }
        }

        // is collided with self
        for (int i = 1; i < positions.size(); i++) {
            if (head.equals(positions.get(i))) {
                System.out.printf("%s collied with itself at %s\n",
                        name, positions.get(i));
                return true;
            }
        }

        return false;
    }


    /**
     * Move snake
     *
     * @param newDirection
     * @return
     */
    public boolean move(Direction newDirection) {
        if (!(this.direction == Direction.DOWN && newDirection == Direction.UP
                || this.direction == Direction.UP && newDirection == Direction.DOWN
                || this.direction == Direction.LEFT && newDirection == Direction.RIGHT
                || this.direction == Direction.RIGHT && newDirection == Direction.LEFT
                || newDirection == Direction.NONE)) {
            this.direction = newDirection;
        }

        if (this.direction == Direction.NONE) {
            return false;
        }

        int newX = getHead().getX();
        int newY = getHead().getY();
        switch (this.direction) {
            case UP:
                newY -= 1;
                break;
            case DOWN:
                newY += 1;
                break;
            case LEFT:
                newX -= 1;
                break;
            case RIGHT:
                newX += 1;
                break;
            default:
                break;
        }

        newX = (newX + game.getSize()) % game.getSize();
        newY = (newY + game.getSize()) % game.getSize();

        boolean ate = false;
        synchronized (positions) {
            positions.add(0, new Position(newX, newY));

            if (game.isFood(getHead())) {
                game.removeFood(getHead());
                score.incrementAndGet();
                ate = true;
            } else {
                positions.remove(positions.size() - 1);
            }

            dead = isCollided();
        }

        return ate;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, direction);
    }
}
