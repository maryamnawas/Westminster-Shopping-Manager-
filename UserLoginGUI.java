import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserLoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Map<String, String> userCredentials;

    // Constructor to initialize the UserLoginGUI
    public UserLoginGUI() {
        // Load existing user credentials from file
        userCredentials = loadUserCredentialsFromFile();

        setTitle("User Login Interface");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        // Add an empty border to the frame
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize GUI components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Adjusted button and text field sizes
        Dimension buttonSize = new Dimension(80, 35);
        Dimension textFieldSize = new Dimension(150, 25);

        loginButton.setPreferredSize(buttonSize);
        registerButton.setPreferredSize(buttonSize);
        usernameField.setPreferredSize(textFieldSize);
        passwordField.setPreferredSize(textFieldSize);

        // Add action listeners to buttons
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(UserLoginGUI.this, "Login successful!");

                    // Open ShoppingGUI after successful login
                    WestminsterShoppingManager shoppingManager = new WestminsterShoppingManager();
                    ShoppingGUI shoppingGUI = new ShoppingGUI(shoppingManager);
                    shoppingGUI.refreshTable("All");
                    dispose(); // Close the login interface
                } else {
                    JOptionPane.showMessageDialog(UserLoginGUI.this, "Invalid username or password");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(UserLoginGUI.this, "Registration successful!");
                } else {
                    JOptionPane.showMessageDialog(UserLoginGUI.this, "Username already exists");
                }
            }
        });

        // Add components to the frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerButton);

        // Explicitly check and create the userCredentials.txt file
        createCredentialsFile();

        pack();
        setVisible(true);
    }

    // Method to validate user login
    private boolean validateLogin(String username, String password) {
        return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
    }

    // Method to register a new user
    private boolean registerUser(String username, String password) {
        if (!userCredentials.containsKey(username)) {
            userCredentials.put(username, password);
            saveUserCredentialsToFile();
            return true;
        }
        return false;
    }

    // Method to load user credentials from a file
    private Map<String, String> loadUserCredentialsFromFile() {
        Map<String, String> credentials = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("userCredentials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    credentials.put(parts[0], parts[1]);
                } else {
                    System.err.println("Invalid line format in userCredentials.txt: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    // Method to save user credentials to a file
    private void saveUserCredentialsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("userCredentials.txt"))) {
            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create the userCredentials.txt file if it doesn't exist
    private void createCredentialsFile() {
        File file = new File("userCredentials.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Main method to launch the UserLoginGUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserLoginGUI userLoginGUI = new UserLoginGUI();
        });
    }
}
