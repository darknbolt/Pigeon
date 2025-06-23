package Views;

import Controllers.LayoutController;
import Services.UserService;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private JTextField username, ip, port;
    private JLabel usernameLabel, ipLabel, portLabel;
    private JButton enter;

    private final UserService service = new UserService();

    public RegisterPanel(LayoutController controller) {
        this.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        usernameLabel = new JLabel("Username:");
        ipLabel = new JLabel("IP:");
        portLabel = new JLabel("Port:");

        username = new JTextField(15);
        ip = new JTextField(15);
        port = new JTextField(15);
        enter = new JButton("Enter");
        enter.addActionListener(
                e -> {
                    String message;
                    try{
                        message = service.createClient(username.getText(), ip.getText(), Integer.parseInt(port.getText()));
                        System.out.println(message);
                        if(message.equals("Success")) controller.show(LayoutController.View.CHAT);
                        else username.setText(message);
                    }catch (NumberFormatException f){
                        port.setText("Invalid Port - Must be a valid port!");
                    }
                    //TODO: Error Message
                });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(username, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(ipLabel, gbc);
        gbc.gridx = 1;
        panel.add(ip, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(portLabel, gbc);
        gbc.gridx = 1;
        panel.add(port, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(enter, gbc);

        add(panel, BorderLayout.CENTER);
        this.setVisible(true);
    }
}
