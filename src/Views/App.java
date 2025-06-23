package Views;

import Controllers.LayoutController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame {
    private LayoutController layoutController;

    public App(){
        this.setTitle("Pigeon");
        this.setSize(600, 600);
        this.setResizable(true);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.layoutController = new LayoutController();
        this.setContentPane(layoutController.getMainPanel());
        this.layoutController.show(LayoutController.View.REGISTER);

        this.setVisible(true);
    }
}