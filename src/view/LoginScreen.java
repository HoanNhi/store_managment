package view;

import javax.swing.*;
import utils.SpringUtilities;
import java.awt.*;
import utils.SpringUtilities;

public class LoginScreen extends JFrame {
    private JTextField txtUserName = new JTextField(10);
    private JPasswordField txtPassword = new JPasswordField(10);
    private JButton btnLogin = new JButton("Login");
    public JCheckBox showPasswordCheckBox = new JCheckBox();  // Public for controller access
    public JLabel eyeIconLabel = new JLabel(createEyeIcon()); // JLabel to hold the icon
    private char defaultPasswordChar;
    private final ImageIcon setPasswordVisibleIcon = new ImageIcon("eye-open.png"),
            setPasswordHiddenIcon = new ImageIcon("eye-closed.png");
    private boolean passwordVisible = false; // To manage eye icon state
    private boolean loginSuccessful = false;


    public JButton getBtnLogin() {
        return btnLogin;
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void clearField() {
        txtUserName.setText("");
        txtPassword.setText("");
    }

    public JTextField getTxtUserName() {
        return txtUserName;
    }

    public LoginScreen() {
        this.setTitle("Login"); // Set title
        this.setSize(500, 400); // Adjust size
        this.setLayout(new BorderLayout()); // Use BorderLayout

        JPanel loginPanel = new JPanel(new BorderLayout(0, 20)); // Add some vertical spacing
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100)); // Add padding

        JLabel titleLabel = new JLabel("LOGIN PAGE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleLabel.setForeground(Color.GREEN); // Set title color
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new SpringLayout());


        JLabel userIcon = new JLabel("\uD83D\uDC64");
        inputPanel.add(userIcon);
        inputPanel.add(txtUserName);

        JLabel lockIcon = new JLabel("\uD83D\uDD12");
        inputPanel.add(lockIcon);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(txtPassword, BorderLayout.CENTER);


        defaultPasswordChar = txtPassword.getEchoChar();

        inputPanel.add(passwordPanel);
        SpringUtilities.makeCompactGrid(inputPanel, 2, 2, 6, 6, 6, 6);


        loginPanel.add(inputPanel, BorderLayout.CENTER);
        loginPanel.add(btnLogin, BorderLayout.SOUTH);


        // Set placeholder text for password field
        txtUserName.setText("Enter username...");
        txtPassword.setText("");
        txtPassword.setForeground(Color.GRAY);

        this.add(loginPanel, BorderLayout.WEST); // Add loginPanel to the WEST

    }

    public void checkShowPasswordBox() { // Public for controller access
        if (showPasswordCheckBox.isSelected()) {
            txtPassword.setEchoChar((char) 0);
            showPasswordCheckBox.setIcon(new ImageIcon(setPasswordVisibleIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } else {
            txtPassword.setEchoChar(defaultPasswordChar);
            showPasswordCheckBox.setIcon(new ImageIcon(setPasswordHiddenIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        }
    }

    public void togglePasswordVisibility() { // Public for controller access
        showPasswordCheckBox.setSelected(!showPasswordCheckBox.isSelected());
        checkShowPasswordBox(); // Ensure UI updates
    }

    public void setLoginSuccessful(boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    private ImageIcon createEyeIcon() {
        String imagePath = passwordVisible ? "eye-open.png" : "eye-closed.png"; // Replace with your actual image paths
        try {
            // Load the image (adjust path as needed)
            Image img = new ImageIcon(imagePath).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {

            return null;  // Or a default icon
        }
    }


}