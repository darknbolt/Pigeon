package Views;

import javax.swing.*;

public class ChatScrollPane extends JScrollPane {
    public ChatScrollPane(JTable table) {
        super(table);
        this.setBounds(40, 40, 500, 440);
        this.setWheelScrollingEnabled(true);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }
}
