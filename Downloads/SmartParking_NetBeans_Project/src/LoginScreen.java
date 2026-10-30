import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    
    // Fonts
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 26);
    private Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    public LoginScreen() {
        setTitle("Smart Parking - Login");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center of screen
        setResizable(false);
        
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createRegisterPanel(), "register");
        
        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Welcome Back!");
        title.setFont(titleFont);
        title.setForeground(new Color(30, 39, 52));
        JLabel sub = new JLabel("Login to manage your parking");
        sub.setFont(normalFont);
        sub.setForeground(Color.GRAY);
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(sub);
        headerPanel.add(Box.createVerticalStrut(40));
        panel.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);
        
        formPanel.add(new JLabel("Email Address"));
        JTextField emailField = new JTextField();
        emailField.setFont(normalFont);
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Password"));
        JPasswordField passField = new JPasswordField();
        passField.setFont(normalFont);
        formPanel.add(passField);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(35, 110, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(loginBtn);
        
        panel.add(formPanel, BorderLayout.CENTER);

        // Footer link
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        JLabel link = new JLabel("<html><u>Don't have an account? Register here</u></html>");
        link.setForeground(new Color(35, 110, 200));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "register");
            }
        });
        footerPanel.add(link);
        panel.add(footerPanel, BorderLayout.SOUTH);

        // Login Action
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passField.getPassword());
            
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + rs.getString("name"));
                    // Main App kholo
                    new SmartParkingApp().setVisible(true);
                    // Login screen band karo
                    this.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Email or Password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Create Account");
        title.setFont(titleFont);
        title.setForeground(new Color(30, 39, 52));
        JLabel sub = new JLabel("Sign up to start booking slots");
        sub.setFont(normalFont);
        sub.setForeground(Color.GRAY);
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(sub);
        headerPanel.add(Box.createVerticalStrut(20));
        panel.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(8, 1, 5, 2));
        formPanel.setBackground(Color.WHITE);
        
        formPanel.add(new JLabel("Full Name"));
        JTextField nameField = new JTextField();
        nameField.setFont(normalFont);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email Address"));
        JTextField emailField = new JTextField();
        emailField.setFont(normalFont);
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Password"));
        JPasswordField passField = new JPasswordField();
        passField.setFont(normalFont);
        formPanel.add(passField);

        JButton regBtn = new JButton("REGISTER");
        regBtn.setBackground(new Color(46, 160, 90)); // Green for register
        regBtn.setForeground(Color.WHITE);
        regBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        regBtn.setFocusPainted(false);
        regBtn.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(regBtn);
        
        panel.add(formPanel, BorderLayout.CENTER);

        // Footer link
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        JLabel link = new JLabel("<html><u>Already have an account? Login here</u></html>");
        link.setForeground(new Color(35, 110, 200));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "login");
            }
        });
        footerPanel.add(link);
        panel.add(footerPanel, BorderLayout.SOUTH);

        // Register Action
        regBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passField.getPassword());
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
                
                // Fields clear karke login par bhej do
                nameField.setText("");
                emailField.setText("");
                passField.setText("");
                cardLayout.show(mainPanel, "login");
                
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}