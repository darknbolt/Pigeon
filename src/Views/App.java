package Views;

import Controllers.LayoutController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame {
    private LayoutController layoutController;

    public App(){
        this.setTitle("Pigeon");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setResizable(true);

        this.layoutController = new LayoutController();
        this.setContentPane(layoutController.getMainPanel());
        this.setVisible(true);
    }
}