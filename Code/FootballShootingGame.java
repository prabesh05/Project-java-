package Code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;


public class FootballShootingGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Football Shooting Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private Ball ball;
    private Goal goal;
    private Goalkeeper goalkeeper;
    private double angle = 45.0;
    private int power = 25;
    private boolean shotFired = false;
    private boolean ballReady = true;
    private int score = 0;
    private int attempts = 0;
    private final int GROUND_LEVEL = 520;

    private JTextField angleField;
    private JTextField powerField;
    private JButton shootButton;
    private JButton resetButton;

    public GamePanel() {
        this.setPreferredSize(new Dimension(900, 650));
        this.setBackground(new Color(34, 139, 34)); // Forest Green
        this.setLayout(null);

        ball = new Ball(100, GROUND_LEVEL - 20);
        goal = new Goal(700, 280, 140, 180); // Larger goal
        goalkeeper = new Goalkeeper(goal.x - 50, goal.y + goal.height - 60, 30, 60);

        // Create UI components
        setupUI();

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    private void setupUI() {
        JLabel angleLabel = new JLabel("Angle (1-90°):");
        JLabel powerLabel = new JLabel("Power (1-50):");
        
        angleLabel.setBounds(10, 10, 120, 20);
        powerLabel.setBounds(10, 40, 120, 20);
        
        angleField = new JTextField("45.0");
        powerField = new JTextField("25");
        shootButton = new JButton("Shoot");
        resetButton = new JButton("Reset");

        angleField.setBounds(140, 10, 60, 25);
        powerField.setBounds(140, 40, 60, 25);
        shootButton.setBounds(210, 10, 80, 25);
        resetButton.setBounds(210, 40, 80, 25);

        this.add(angleLabel);
        this.add(powerLabel);
        this.add(angleField);
        this.add(powerField);
        this.add(shootButton);
        this.add(resetButton);

        shootButton.addActionListener(e -> {
            if (ballReady) {
                try {
                    double inputAngle = Double.parseDouble(angleField.getText());
                    int inputPower = Integer.parseInt(powerField.getText());
                    
                    // Strict validation with user feedback
                    if (inputAngle <= 0 || inputAngle > 90) {
                        JOptionPane.showMessageDialog(this, "Angle must be greater than 0 and less than 90 degrees!");
                        return;
                    }
                    if (inputPower < 1 || inputPower > 50) {
                        JOptionPane.showMessageDialog(this, "Power must be between 1 and 50!");
                        return;
                    }
                    
                    this.angle = inputAngle;
                    this.power = inputPower;
                    shootBall();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
                }
            }
        });

        resetButton.addActionListener(e -> resetGame());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw ground
        g2d.setColor(new Color(101, 67, 33)); // Brown
        g2d.fillRect(0, GROUND_LEVEL, getWidth(), getHeight() - GROUND_LEVEL);

        // Draw goal posts (more realistic - no left post, extended right and top)
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(8)); // Thicker posts
        
        // Right post
        g2d.drawLine(goal.x + goal.width, goal.y, goal.x + goal.width, GROUND_LEVEL);
        // Top bar
        g2d.drawLine(goal.x, goal.y, goal.x + goal.width, goal.y);
        // Goal line on ground
        g2d.drawLine(goal.x, GROUND_LEVEL, goal.x + goal.width, GROUND_LEVEL);

        // Draw goal net
        g2d.setColor(new Color(200, 200, 200, 100));
        g2d.setStroke(new BasicStroke(1));
        // Vertical net lines
        for (int i = goal.x; i <= goal.x + goal.width; i += 20) {
            g2d.drawLine(i, goal.y, i, GROUND_LEVEL);
        }
        // Horizontal net lines
        for (int i = goal.y; i <= GROUND_LEVEL; i += 20) {
            g2d.drawLine(goal.x, i, goal.x + goal.width, i);
        }

        // Draw goal area
        g2d.setColor(new Color(255, 255, 255, 30)); // Semi-transparent white
        g2d.fillRect(goal.x, goal.y, goal.width, GROUND_LEVEL - goal.y);

        // Draw goalkeeper (simple rectangle)
        g2d.setColor(new Color(255, 165, 0)); // Orange goalkeeper
        g2d.fillRect(goalkeeper.x, goalkeeper.y, goalkeeper.width, goalkeeper.height);
        
        // Simple border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(goalkeeper.x, goalkeeper.y, goalkeeper.width, goalkeeper.height);

        // Draw ball
        g2d.setColor(Color.WHITE);
        g2d.fill(new Ellipse2D.Double(ball.x - ball.radius/2, ball.y - ball.radius/2, ball.radius, ball.radius));
        
        // Ball details (soccer ball pattern)
        g2d.setColor(Color.BLACK);
        g2d.draw(new Ellipse2D.Double(ball.x - ball.radius/2, ball.y - ball.radius/2, ball.radius, ball.radius));

        // Draw game info
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Score: " + score, 10, 100);
        g2d.drawString("Attempts: " + attempts, 10, 120);
        if (attempts > 0) {
            g2d.drawString("Accuracy: " + String.format("%.1f%%", (score * 100.0) / attempts), 10, 140);
        }
        
        if (!ballReady && !shotFired) {
            g2d.drawString("Press Shoot to try again!", 300, 50);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Update goalkeeper movement
        goalkeeper.update();
        
        if (shotFired) {
            ball.update();

            // Check collision with goalkeeper
            if (goalkeeper.collidesWith(ball)) {
                // Ball hits goalkeeper - no goal, reset
                shotFired = false;
                ballReady = true;
                ball = new Ball(100, GROUND_LEVEL - 20);
                JOptionPane.showMessageDialog(this, "Goalkeeper saved it! Try again!");
                return;
            }

            // Check ground collision with improved bounce handling
            if (ball.y >= GROUND_LEVEL) {
                ball.y = GROUND_LEVEL;
                ball.vy *= -0.5; // Reduced bounce energy
                ball.vx *= 0.7; // Increased friction
                
                // Stop bouncing if velocity is too low OR after many bounces
                if (Math.abs(ball.vy) < 0.8 || Math.abs(ball.vx) < 0.3) {
                    ball.vy = 0;
                    ball.vx = 0;
                    shotFired = false;
                    ballReady = true;
                    ball = new Ball(100, GROUND_LEVEL - 20);
                }
            }

            // Check goal - improved detection for all angles including low shots
            if (ball.x >= goal.x && ball.x <= goal.x + goal.width) {
                // Check if ball is within goal height (including ground level shots)
                if (ball.y >= goal.y && ball.y <= GROUND_LEVEL) {
                    score++;
                    resetBallAfterGoal();
                    JOptionPane.showMessageDialog(this, "GOAL! Great shot!");
                    return; // Exit early to prevent other collision checks
                }
            }

            // Check if ball is out of bounds (missed the goal) or stopped moving
            if (ball.x > getWidth() + 50 || ball.x < -50 || 
                (ball.y >= GROUND_LEVEL && Math.abs(ball.vx) < 0.3 && Math.abs(ball.vy) < 0.3)) {
                shotFired = false;
                ballReady = true;
                ball = new Ball(100, GROUND_LEVEL - 20); // Reset ball to starting position
            }
        }

        repaint();
    }

    public void shootBall() {
        if (ballReady) {
            ball.shoot(angle, power);
            shotFired = true;
            ballReady = false;
            attempts++;
        }
    }

    public void resetBallAfterGoal() {
        ball = new Ball(100, GROUND_LEVEL - 20);
        shotFired = false;
        ballReady = true;
    }

    public void resetGame() {
        ball = new Ball(100, GROUND_LEVEL - 20);
        shotFired = false;
        ballReady = true;
        score = 0;
        attempts = 0;
    }
}

class Ball {
    double x, y;
    double vx, vy;
    double gravity = 0.5;
    int radius = 30;

    public Ball(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
    }

    public void shoot(double angleDeg, int power) {
        double angle = Math.toRadians(angleDeg);
        this.vx = power * Math.cos(angle);
        this.vy = -power * Math.sin(angle);
    }

    public void update() {
        x += vx;
        y += vy;
        vy += gravity;
    }
}

class Goal {
    int x, y, width, height;

    public Goal(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public boolean contains(Ball ball) {
        // Simple and reliable collision detection for all shot angles
        double ballCenterX = ball.x;
        double ballCenterY = ball.y;
        
        // Check if ball center is within goal boundaries
        return (ballCenterX >= x && ballCenterX <= x + width && 
                ballCenterY >= y && ballCenterY <= y + height);
    }
}

class Goalkeeper {
    int x, y, width, height;
    int speed = 4;
    int direction = 1; // 1 for up, -1 for down
    int minY, maxY;

    public Goalkeeper(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // Set movement bounds within the goal area
        this.minY = y - 80;
        this.maxY = y + 40;
    }

    public void update() {
        // Move goalkeeper up and down
        y += speed * direction;
        
        // Change direction when hitting bounds
        if (y <= minY || y >= maxY) {
            direction *= -1;
        }
    }

    public boolean collidesWith(Ball ball) {
        // Check if ball collides with goalkeeper
        Rectangle keeperRect = new Rectangle(x - 5, y - 5, width + 10, height + 10);
        Rectangle ballRect = new Rectangle((int)(ball.x - ball.radius/2), (int)(ball.y - ball.radius/2), ball.radius, ball.radius);
        
        return keeperRect.intersects(ballRect);
    }
}