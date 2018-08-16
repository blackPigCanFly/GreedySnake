package inft3030.snake.game;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Greedy snake game.
 */
public class Game {

    private final static Random RANDOM = new Random();

    /**
     * initial snake position offset
     */
    private final static int OFFSET = 2;

    /**
     * game board size
     */
    private AtomicInteger size;

    /**
     * snakes
     */
    private List<Snake> snakes;

    /**
     * foods
     */
    private List<Position> foods;


    /**
     * constructor
     *
     * @param size
     */
    public Game(int size) {
        this.size = new AtomicInteger(size);
        this.snakes = Collections.synchronizedList(new ArrayList<>());
        this.foods = Collections.synchronizedList(new ArrayList<>());
    }

    public void setSnakes(List<Snake> snakes) {
        this.snakes = Collections.synchronizedList(snakes);
    }

    public void setFoods(List<Position> foods) {
        this.foods = Collections.synchronizedList(foods);
    }

    /**
     * return all of the snakes
     *
     * @return
     */
    public List<Snake> getSnakes() {
        return snakes;
    }

    /**
     * return all of the foods
     *
     * @return
     */
    public List<Position> getFoods() {
        return foods;
    }

    /**
     * is game over
     *
     * @return
     */
    public boolean isOver() {
        if (snakes.isEmpty()) {
            // not start
            return false;
        }

        for (Snake snake : snakes) {
            if (!snake.isDead()) {
                return false;
            }
        }
        return true;
    }

    public int getSize() {
        return size.get();
    }

    /**
     * Is a food at the position
     *
     * @param position
     * @return
     */
    public boolean isFood(Position position) {
        synchronized (foods) {
            for (Position food : foods) {
                if (food.equals(position)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Remove food
     *
     * @param food
     */
    public void removeFood(Position food) {
        synchronized (foods) {
            if (foods.contains(food)) {
                foods.remove(food);
            }
        }
    }

    /**
     * get snake by name
     *
     * @param name
     * @return
     */
    public Snake getSnake(String name) {
        synchronized (snakes) {
            for (Snake snake : snakes) {
                if (snake.getName().equals(name)) {
                    return snake;
                }
            }
        }

        return null;
    }

    /**
     * Add a new snake to game
     *
     * @param name snake name
     * @return
     */
    public void addSnake(String name) {
        if (getSnake(name) != null) {
            return;
        }

        int size = this.size.get();

        // Add snake
        snakes.add(new Snake(name,
                new Position(RANDOM.nextInt(size),
                        RANDOM.nextInt(size)),
                this));

        // Add food
        foods.add(new Position(RANDOM.nextInt(size), RANDOM.nextInt(size)));
    }

    /**
     * Move snake
     *
     * @param name
     * @param direction
     * @return
     */
    public boolean moveSnake(String name, Direction direction) {
        synchronized (foods) {
            Snake snake = getSnake(name);
            if (snake == null) {
                System.out.printf("snake %s not exists\n", name);
                return false;
            }

            if (snake.move(direction)) {
                Random random = new Random();
                foods.add(new Position(random.nextInt(size.get()),
                        random.nextInt(size.get())));
            }

            return !snake.isDead();
        }
    }
}
