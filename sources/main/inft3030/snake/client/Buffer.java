package inft3030.snake.client;

/**
 * Buffer.
 */
public class Buffer {
    /**
     * Data array.
     */
    private Player[] data;

    /**
     * In index.
     */
    private int inIndex;

    /**
     * Out index.
     */
    private int outIndex;

    /**
     * Number of players in buffer.
     */
    private int count;

    /**
     * Constructor.
     *
     * @param size
     */
    public Buffer(int size) {
        this.data = new Player[size];
        this.inIndex = 0;
        this.outIndex = 0;
        this.count = 0;
    }

    /**
     * Take player from buffer.
     *
     * @return
     */
    public synchronized Player take() {
        while (count == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        Player player = data[outIndex];
        outIndex = (outIndex + 1) % data.length;
        count--;
        notifyAll();

        return player;
    }

    /**
     * Append new player into buffer.
     *
     * @param player
     */
    public synchronized void append(Player player) {
        while (count == data.length) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        data[inIndex] = player;
        inIndex = (inIndex + 1) % data.length;
        count++;
        notifyAll();
    }
}
