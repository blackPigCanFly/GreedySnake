package inft3030.snake.server;

import inft3030.snake.game.Direction;
import inft3030.snake.game.Game;
import inft3030.snake.game.Position;
import inft3030.snake.game.Snake;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Game server.
 */
public class Server {
    /**
     * User database file.
     */
    private final static String USER_MAPDB_FILE = "mapDB";

    /**
     * Game.
     */
    private Game game;

    /**
     * Database map.
     */
    private ConcurrentMap<String, String> dbMap;

    /**
     * Constructor.
     *
     * @param gameBoardSize
     */
    public Server(int gameBoardSize) {
        game = new Game(gameBoardSize);

        // initialise users database
        DB db = DBMaker.fileDB(USER_MAPDB_FILE).make();
        dbMap = db.hashMap("users", Serializer.STRING, Serializer.STRING)
                .createOrOpen();
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i <= 9; i++) {
                String name = String.format("%c%d", c, i);
                dbMap.put(name, name);
            }
        }
        db.commit();
    }

    /**
     * Login.
     *
     * @param username
     * @param password
     * @return
     */
    public synchronized boolean login(String username, String password) {
        String actualPassword = dbMap.get(username);
        return actualPassword != null && actualPassword.equals(password);
    }

    /**
     * Add new snake.
     *
     * @param name
     */
    public synchronized void addSnake(String name) {
        game.addSnake(name);
    }

    /**
     * Move snake.
     *
     * @param name
     * @param direction
     */
    public boolean moveSnake(String name, Direction direction) {
        return game.moveSnake(name, direction);
    }

    /**
     * Return snake's score.
     *
     * @param snakeName
     * @return
     */
    public int getScore(String snakeName) {
        Snake snake = game.getSnake(snakeName);
        return snake.getScore();
    }

    /**
     * Return a image representation current game state.
     *
     * @param size
     * @return
     */
    public BufferedImage getBufferedImage(int size) {
        int grids = game.getSize();
        double cell = Double.valueOf(size) / grids;


        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.BOLD, (int) (cell)));

        // background
        g2.setColor(Color.BLACK);
        g2.fill(new Rectangle2D.Double(0, 0, size, size));

        // draw grids
        g2.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= game.getSize(); i++) {
            g2.draw(new Line2D.Double(i * cell, 0,
                    i * cell, grids * cell));
            g2.draw(new Line2D.Double(0, i * cell,
                    grids * cell, i * cell));
        }

        // draw snakes
        List<Snake> snakes = game.getSnakes();
        g2.setColor(new Color(0, 153, 79));
        synchronized (snakes) {
            for (Snake snake : snakes) {
                if (!snake.isDead()) {
                    List<Position> positions = snake.getPositions();
                    synchronized (positions) {
                        boolean first = true;
                        for (Position position : positions) {
                            int x = position.getX();
                            int y = position.getY();

                            g2.fill(new RoundRectangle2D.Double(x * cell,
                                    y * cell, cell, cell, cell / 2.0, cell / 2.0));

                            if (first) {
                                Color oldColor = g2.getColor();

                                g2.setColor(Color.RED);
                                FontMetrics fontMetrics = g2.getFontMetrics(g2.getFont());
                                g2.drawString(snake.getName(), (float) (x * cell),
                                        (float) (y * cell + fontMetrics.getAscent()));

                                g2.setColor(oldColor);
                                first = false;
                            }
                        }
                    }

                }
            }
        }
        // draw foods
        g2.setColor(Color.ORANGE);
        List<Position> foods = game.getFoods();
        synchronized (foods) {
            for (Position food : foods) {
                int x = food.getX();
                int y = food.getY();
                g2.fill(new Ellipse2D.Double(
                        x * cell, y * cell, cell, cell));
            }
        }

        return image;
    }

}
