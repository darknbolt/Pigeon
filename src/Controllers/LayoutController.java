package Controllers;

import Views.ChatPanel;
import Views.RegisterPanel;

import javax.swing.*;
import java.awt.*;

public class LayoutController {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public enum View{
        REGISTER,
        CHAT
    }

    public LayoutController() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new RegisterPanel(this), View.REGISTER.name());
        mainPanel.add(new ChatPanel(this), View.CHAT.name());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void show(View viewToShow){ cardLayout.show(mainPanel, viewToShow.name()); }
}
