import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class LandingPage extends JFrame {
    private JButton playButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    
    public LandingPage() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }
    
    private void initializeComponents() {
        // Create title label
        titleLabel = new JLabel("FOOTBALL SHOOTING GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Create subtitle label
        subtitleLabel = new JLabel("⚽ Score Goals and Become a Champion! ⚽");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Create play button
        playButton = createStyledButton("🎮 PLAY GAME", new Color(46, 204, 113));
        
        // Create exit button
        exitButton = createStyledButton("❌ EXIT", new Color(231, 76, 60));
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 60));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = button.getBackground();
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create gradient background
                Color color1 = new Color(30, 60, 114);
                Color color2 = new Color(42, 82, 152);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add football field pattern
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < getWidth(); i += 50) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 50) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Add title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 20, 20, 20);
        mainPanel.add(titleLabel, gbc);
        
        // Add subtitle
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 40, 20);
        mainPanel.add(subtitleLabel, gbc);
        
        // Add play button
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(playButton, gbc);
        
        // Add exit button
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 50, 20);
        mainPanel.add(exitButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playGame();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
    }
    
    private void setupWindow() {
        setTitle("Football Shooting Game");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set icon (you can replace this with your own icon)
        try {
            // Create a simple football icon
            ImageIcon icon = createFootballIcon();
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Could not load icon");
        }
    }
    
    private ImageIcon createFootballIcon() {
        // Create a simple football icon programmatically
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw football
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillOval(4, 4, size-8, size-8);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(4, 4, size-8, size-8);
        
        // Add pattern
        g2d.drawLine(size/2, 8, size/2, size-8);
        g2d.drawArc(size/2-6, size/2-3, 12, 6, 0, 180);
        g2d.drawArc(size/2-6, size/2-3, 12, 6, 180, 180);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private void playGame() {
        // Hide the landing page
        this.setVisible(false);
        
        // TODO: Replace this with your actual game initialization
        JOptionPane.showMessageDialog(null, 
            "Starting Football Shooting Game!\n\nReplace this with your actual game code.", 
            "Game Starting", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Example: Launch your main game class
        // YourFootballGame game = new YourFootballGame();
        // game.start();
        
        // For demo purposes, we'll show the landing page again
        this.setVisible(true);
    }
    
    private void exitGame() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Exit Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show the landing page
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LandingPage().setVisible(true);
            }
        });
    }
}