package inft3030.snake.main;

import inft3030.snake.client.Buffer;
import inft3030.snake.client.Loginer;
import inft3030.snake.client.Player;
import inft3030.snake.game.Direction;
import inft3030.snake.server.Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * user interface
 */
public class MainWindow extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    private static final int BUFFER_SIZE = 4;
    private static final int FPS = 20;

    private static final int GAME_BOARD_SIZE = 80;

    /**
     * Canvas.
     */
    private JPanel canvas;

    /**
     * Player list.
     */
    private JList<Player> playerList;

    /**
     * Login button.
     */
    private JButton loginBtn;

    /**
     * Start button.
     */
    private JButton startBtn;

    /**
     * Test button.
     */
    private JButton testBtn;


    /**
     * Game server.
     */
    private Server server;


    /**
     * Buffer.
     */
    private Buffer buffer;

    /**
     * players.
     */
    private List<Player> players;

    private List<Player> realPlayers;

    /**
     * login thread pool.
     */
    private ExecutorService loginService;

    /**
     * play thread pool.
     */
    private ExecutorService playService;

    /**
     * Is playing.
     */
    private boolean playing;

    /**
     * Constructor.
     */
    public MainWindow() {
        server = new Server(GAME_BOARD_SIZE);
        buffer = new Buffer(BUFFER_SIZE);
        players = new ArrayList<>();
        realPlayers = new ArrayList<>();
        loginService = Executors.newFixedThreadPool(4);
        playing = false;

        canvas = new JPanel();
        playerList = new JList<>();
        loginBtn = new JButton("Login");
        startBtn = new JButton("Start");
        testBtn = new JButton("Test");

        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.BOTH;

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 0.8;
        playerList.setFont(new Font("monospaced", Font.PLAIN, 12));
        playerList.setBorder(BorderFactory.createEtchedBorder());
        pane.add(new JScrollPane(playerList), c);
        playerList.setPreferredSize(new Dimension(200, 0));

        Box box = Box.createHorizontalBox();
        box.add(Box.createGlue());
        startBtn.setAlignmentX(CENTER_ALIGNMENT);
        box.add(loginBtn);
        box.add(Box.createGlue());
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        box.add(startBtn);
        box.add(Box.createGlue());
        testBtn.setAlignmentX(CENTER_ALIGNMENT);
        box.add(testBtn);
        box.add(Box.createGlue());
        box.setBorder(BorderFactory.createEtchedBorder());
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.1;
        c.weighty = 0.2;
        pane.add(box, c);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.weightx = 0.9;
        c.weighty = 0.8;
        canvas.setBorder(BorderFactory.createEtchedBorder());
        pane.add(canvas, c);
        canvas.setIgnoreRepaint(true);

        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Greedy Snake");

        addEventListeners();

        startPlayerFetcher();

        // updating game state
        canvas.setDoubleBuffered(true);
        new Timer(1000 / FPS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerList.setListData(players.toArray(new Player[0]));
                repaintCanvas();
            }
        }).start();
    }

    /**
     * Repaint canvas.
     */
    private void repaintCanvas() {
        if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0) {
            return;
        }

        int size = Math.min(canvas.getWidth(), canvas.getHeight()) - 10;
        int xOffset = (canvas.getWidth() - size) / 2;
        int yOffset = (canvas.getHeight() - size) / 2;
        canvas.getGraphics().drawImage(server.getBufferedImage(size),
                xOffset, yOffset, null);
    }

    /**
     * Prompt user to input a no empty string.
     *
     * @param prompt
     * @return
     */
    private String inputString(String prompt) {
        String s;

        do {
            s = JOptionPane.showInputDialog(this, prompt);
        } while (s != null && s.trim().isEmpty());

        if (s != null) {
            s = s.trim();
        }

        return s;
    }

    /**
     * Prompt input login information.
     *
     * @return
     */
    private Loginer inputLoginer() {
        JTextField userTextField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JCheckBox realCheckBox = new JCheckBox("real", false);

        JComponent[] inputs = {
                new JLabel("user", SwingConstants.LEADING), userTextField,
                new JLabel("password", SwingConstants.LEFT), passwordField,
                realCheckBox
        };

        if (JOptionPane.showConfirmDialog(this, inputs, "login",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return null;
        }

        String username = userTextField.getText();
        String password = String.valueOf(passwordField.getPassword());
        boolean automated = !realCheckBox.isSelected();

        if (username.isEmpty() && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "user or password is empty");
            return null;
        }

        return new Loginer(server, buffer, username, password, automated);
    }

    /**
     * Add event listeners.
     */
    private void addEventListeners() {
        // login action listener
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playing) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "players is running, can't login");
                    return;
                }

                Loginer loginer = inputLoginer();
                if (loginer != null) {
                    loginService.execute(loginer);
                }
            }
        });

        // start action listener
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (players.isEmpty()) {
                    JOptionPane.showMessageDialog(MainWindow.this, "no player login");
                    return;
                }

                if (playing) {
                    JOptionPane.showMessageDialog(MainWindow.this, "already playing");
                    return;
                }

                playing = true;
                loginService.shutdownNow();
                playService = Executors.newFixedThreadPool(players.size());

                for (Player player : players) {
                    playService.execute(player);
                }
            }
        });

        // test action listener
        testBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playing) {
                    JOptionPane.showMessageDialog(MainWindow.this, "already playing");
                    return;
                }

                if (!players.isEmpty()) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "players is not empty");
                    return;
                }

                playing = true;

                // 4 real players
                loginService.execute(new Loginer(server, buffer, "k1", "k1", false));
                loginService.execute(new Loginer(server, buffer, "k2", "k2", false));
                loginService.execute(new Loginer(server, buffer, "k3", "k3", false));
                loginService.execute(new Loginer(server, buffer, "k4", "k4", false));

                // 100 auto players
                for (char c = 'a'; c < 'k'; c++) {
                    for (int i = 0; i < 10; i++) {
                        String username = String.format("%c%d", c, i);
                        String password = String.format("%c%d", c, i);
                        loginService.execute(new Loginer(server, buffer,
                                username, password, true));
                    }
                }

                try {
                    loginService.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    loginService.shutdownNow();
                }

                playService = Executors.newFixedThreadPool(players.size());
                for (Player player : players) {
                    playService.execute(player);
                }
            }
        });


        String[][] keyGroups = {
                // first real player
                // UP: up, DOWN: down, LEFT: left, RIGHT: right
                {"UP", "DOWN", "LEFT", "RIGHT"},
                // second real player
                // W: up, S: down, A: left, D: right
                {"W", "S", "A", "D"},
                // third real player
                // I: up, K: down, J: left, L: right
                {"I", "K", "J", "L"},
                // fourth real player
                // T: up, G: down, F: left, H: right
                {"T", "G", "F", "H"}
        };
        Direction[] directions = {
                Direction.UP, Direction.DOWN,
                Direction.LEFT, Direction.RIGHT};

        for (int i = 0; i < keyGroups.length; i++) {
            String[] group = keyGroups[i];

            for (int j = 0; j < group.length; j++) {
                String key = group[j];
                Direction direction = directions[j];

                final int index = i;
                canvas.getInputMap(JComponent.WHEN_FOCUSED)
                        .put(KeyStroke.getKeyStroke(key), key);
                canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                        .put(KeyStroke.getKeyStroke(key), key);
                canvas.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                        .put(KeyStroke.getKeyStroke(key), key);
                canvas.getActionMap().put(key, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (realPlayers.size() > index) {
                            Player player = realPlayers.get(index);
                            player.setDirection(direction);
                        }
                    }
                });
            }
        }
    }

    /**
     * Start a thread to take login player from buffer.
     */
    private void startPlayerFetcher() {
        loginService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Player player = buffer.take();
                    if (!players.contains(player)) {
                        System.out.printf("add new player (%s)\n", player);
                        players.add(player);
                        server.addSnake(player.getName());
                        if (!player.isAutomated()) {
                            realPlayers.add(player);
                        }
                    } else {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                String.format("player '%s' already exists",
                                        player.getName()));
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        MainWindow frame = new MainWindow();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
