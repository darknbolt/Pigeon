package Views;

import Controllers.LayoutController;
import Services.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private JTextField username;
    private JTextField ip;
    private JTextField port;
    private JButton enter;

    private final UserService service = new UserService();

    public RegisterPanel(LayoutController controller) {
        this.setLayout(new BorderLayout());

        //TODO: Register Panel
        //TODO: TextField for Username, IP, Port
        username = new JTextField("Enter Username");
        ip = new JTextField("Enter IP");
        port = new JTextField("Enter Port");

        enter = new JButton("Enter");
        enter.addActionListener(
                e -> {
                    String success = service.createClient(username.getText(), ip.getText(), Integer.parseInt(port.getText()));
                    if (success.equals("Success")) controller.show(LayoutController.View.CHAT);
                    //TODO: Error Message
                });
        //TODO: Button for Enter
        //TODO?: Special Error Panel

        this.setVisible(true);
    }
}
