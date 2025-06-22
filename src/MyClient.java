import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;

public class MyClient extends JFrame{
    public static void main(String[] args) {
        MyClient client = new MyClient();
    }
    private enum Phases {IP, PORT, USERNAME, CONNECTED};
    private int myPort;
    private String myIP, myUsername;
    private Phases currentPhase;
    private JTextField textBox;
    private JTable tableOfMessages;
    private MyScrollPane pane;
    private JLabel setIP, enterPort, enterUsername;
    private ClientListener myListener;
    private ClientMessenger myMessenger;
    private Socket mySocket;
    public MyClient(){
        //PHASE && THREAD
        currentPhase = Phases.IP;
        myListener = null;
        myMessenger = null;
        mySocket = null;
        myIP = "";
        myUsername = "";
        myPort = 0;

        //JFRAME
        this.setSize(600, 600);
        this.setTitle("Pigeon");
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //MESSAGES
        DefaultTableModel model = new DefaultTableModel(
                new Object[][] {{"", ""}},
                new String[] {"", ""}
        ){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tableOfMessages = new JTable(model);

        //JSCROLLPANE
        pane = new MyScrollPane(tableOfMessages);
        pane.setBackground(Color.white);

        //JLABEL
        setIP = new JLabel("Provide IP");
        enterPort = new JLabel("Enter Port");
        enterUsername = new JLabel("Enter Username");

        setIP.setBounds(250, 240, 100, 100);
        enterPort.setBounds(250, 240, 100, 100);
        enterUsername.setBounds(250, 240, 100, 100);

        enterPort.setVisible(false);
        enterUsername.setVisible(false);

        //JTEXTFIELD
        textBox = new JTextField();
        textBox.setBounds(40, 500, 450, 50);
        textBox.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    phase(textBox.getText());
                    textBox.setText("");
                }
            }
        });

        this.add(setIP);
        this.add(enterPort);
        this.add(enterUsername);
        this.add(pane);
        this.add(textBox);
        this.setVisible(true);
    }
    private void phase(String text){
        switch (currentPhase){
            case IP:
                if(text.equalsIgnoreCase("Default")) myIP = "127.0.0.1";
                else myIP = text;
                setIP.setVisible(false);
                enterPort.setVisible(true);
                currentPhase = Phases.PORT;
                break;
            case PORT:
                if(text.equalsIgnoreCase("Default")) myPort = 550;
                else myPort = Integer.parseInt(text);
                enterPort.setVisible(false);
                enterUsername.setVisible(true);
                currentPhase = Phases.USERNAME;
                break;
            case USERNAME:
                myUsername = text;
                enterUsername.setVisible(false);
                currentPhase = Phases.CONNECTED;
                try {
                    mySocket = new Socket(myIP, myPort);
                    myListener = new ClientListener(mySocket, this);
                    myMessenger = new ClientMessenger(mySocket);

                    myListener.start();
                    myMessenger.start();
                    myMessenger.setMyClient(this);

                    try{
                        Thread.sleep(300);
                    }catch (InterruptedException e){
                        System.out.println("Error here");
                    }

                    if(myMessenger.isAlive()) myMessenger.sendMessage(myUsername);
                }catch (IOException e){
                    System.out.println("COULD NOT CONNECT TO SERVER");
                    reset();
                }

                break;
            case CONNECTED:
                myMessenger.sendMessage(text);

        }
    }
    public String getMyUsername() {
        return myUsername;
    }
    public JTable getTableOfMessages(){return tableOfMessages;}
    public void reset(){
        this.setIP.setVisible(true);
        this.enterPort.setVisible(false);
        this.enterUsername.setVisible(false);
        this.textBox.setText("");
        this.currentPhase = Phases.IP;
        DefaultTableModel model = (DefaultTableModel) this.getTableOfMessages().getModel();
        for(int i = model.getRowCount()-1; i > 0; --i){
            model.removeRow(i);
        }
    }
    public static class ClientListener extends Thread{
        private MyClient myClient;
        private Socket socket;
        private BufferedReader listener;
        public ClientListener(Socket socket, MyClient client){
            this.myClient = client;
            this.socket = socket;
            try{
                listener = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }catch (IOException e){
                System.out.println("ERROR WHEN CREATING CLIENT LISTENER");
            }
        }
        @Override
        public void run(){
            try {
                String line;
                while (socket.isConnected()) {
                    DefaultTableModel myModel = (DefaultTableModel) myClient.getTableOfMessages().getModel();
                    line = listener.readLine();
                    if(line.equals("th1s 1s th@ @nd")) myModel.addRow(new Object[]{"", ""});
                    else myModel.addRow(new Object[]{line, ""});
                }
            }catch (IOException e){
                System.out.println("ERROR WHEN LISTENING TO SERVER");
                myClient.reset();
            }
        }
    }
    public static class ClientMessenger extends Thread{
        private MyClient myClient;
        private Socket cmSocket;
        private PrintWriter writer;
        private String message;
        public ClientMessenger(Socket socket){
            this.cmSocket = socket;
            try {
                writer = new PrintWriter(cmSocket.getOutputStream(), true);
            }catch (IOException e){
                System.out.println("ERROR CREATING CLIENT WRITER");
            }
        }
        @Override
        public void run(){
            try{
                while(cmSocket.isConnected()){
                    synchronized (this){
                        wait();
                        writer.println(message);
                        if(!myClient.getMyUsername().equals(message)) {
                            DefaultTableModel model = (DefaultTableModel) myClient.getTableOfMessages().getModel();
                            model.addRow(new Object[]{"", "@" + myClient.getMyUsername()});
                            model.addRow(new Object[]{"", message});
                        }
                        if(message.equals("@e")) cmSocket.close();
                    }
                }
            }catch (IOException e){
                System.out.println("ERROR WHEN DISCONNECTING FROM SERVER");
            }catch (InterruptedException e){
                System.out.println("INTERRUPTION OCCURRED");
            }
        }
        public void setMyClient(MyClient myClient){
            this.myClient = myClient;
        }
        public void sendMessage(String message){
            this.message = message;
            synchronized (this){
                notify();
            }
        }
    }
    public static class MyScrollPane extends JScrollPane{
        public MyScrollPane(JTable table){
            super(table);
            this.setBounds(40, 40, 500, 440);
            this.setWheelScrollingEnabled(true);
            this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }
    }
}