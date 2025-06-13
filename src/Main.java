import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pong");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new GamePanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final Timer timer;
    private final Paddle leftPaddle;
    private final Paddle rightPaddle;
    private final Ball ball;
    private int leftScore = 0;
    private int rightScore = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        leftPaddle = new Paddle(10, HEIGHT / 2 - 40, KeyEvent.VK_W, KeyEvent.VK_S);
        rightPaddle = new Paddle(WIDTH - 20, HEIGHT / 2 - 40, KeyEvent.VK_UP, KeyEvent.VK_DOWN);
        ball = new Ball(WIDTH / 2, HEIGHT / 2);
        timer = new Timer(1000 / 60, this); // 60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        // Draw center line
        for (int y = 0; y < HEIGHT; y += 15) {
            g.fillRect(WIDTH / 2 - 1, y, 2, 10);
        }
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString(String.valueOf(leftScore), WIDTH / 2 - 60, 50);
        g.drawString(String.valueOf(rightScore), WIDTH / 2 + 40, 50);
        leftPaddle.draw(g);
        rightPaddle.draw(g);
        ball.draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        leftPaddle.update(HEIGHT);
        rightPaddle.update(HEIGHT);
        ball.update(WIDTH, HEIGHT, leftPaddle, rightPaddle);
        if (ball.getX() < 0) {
            rightScore++;
            ball.reset(WIDTH / 2, HEIGHT / 2);
        } else if (ball.getX() > WIDTH) {
            leftScore++;
            ball.reset(WIDTH / 2, HEIGHT / 2);
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        leftPaddle.keyPressed(e.getKeyCode());
        rightPaddle.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        leftPaddle.keyReleased(e.getKeyCode());
        rightPaddle.keyReleased(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

class Paddle {
    private int x, y;
    private final int width = 10;
    private final int height = 80;
    private final int speed = 5;
    private final int upKey, downKey;
    private boolean movingUp = false, movingDown = false;

    public Paddle(int x, int y, int upKey, int downKey) {
        this.x = x;
        this.y = y;
        this.upKey = upKey;
        this.downKey = downKey;
    }

    public void update(int panelHeight) {
        if (movingUp && y > 0) {
            y -= speed;
        }
        if (movingDown && y + height < panelHeight) {
            y += speed;
        }
    }

    public void draw(Graphics g) {
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void keyPressed(int keyCode) {
        if (keyCode == upKey) {
            movingUp = true;
        } else if (keyCode == downKey) {
            movingDown = true;
        }
    }

    public void keyReleased(int keyCode) {
        if (keyCode == upKey) {
            movingUp = false;
        } else if (keyCode == downKey) {
            movingDown = false;
        }
    }
}

class Ball {
    private int x, y;
    private int size = 20;
    private int xVel = 4;
    private int yVel = 4;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(int panelWidth, int panelHeight, Paddle left, Paddle right) {
        x += xVel;
        y += yVel;

        if (y <= 0 || y + size >= panelHeight) {
            yVel = -yVel;
        }

        if (getBounds().intersects(left.getBounds()) || getBounds().intersects(right.getBounds())) {
            xVel = -xVel;
        }
    }

    public void draw(Graphics g) {
        g.fillOval(x, y, size, size);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public int getX() {
        return x;
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        xVel = -xVel;
    }
}

