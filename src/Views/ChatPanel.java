package Views;

import Controllers.LayoutController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ChatPanel extends JPanel {
    private ChatScrollPane pane;
    private JTable messages;

    public ChatPanel(LayoutController controller) {
        this.setLayout(new BorderLayout());

        //Model
        DefaultTableModel model = new DefaultTableModel(
                new Object[][] {{"", ""}},
                new String[] {"", ""}
        ){
            @Override
            public boolean isCellEditable(int row, int column) { return false;}
        };
        messages = new JTable(model);

        //Pane
        pane = new ChatScrollPane(messages);
        pane.setBackground(Color.WHITE);

        this.add(pane, BorderLayout.CENTER);
        this.setVisible(true);
    }
}
