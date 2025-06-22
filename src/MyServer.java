import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyServer extends Thread{
    public static void main(String[] args) {
        MyServer server = new MyServer();
        server.start();
        server.listen();
    }
    private int port;
    private static Object key;
    private static int currentCapacity;
    private final int maxCapacity = 4;
    private String name, address;
    private static List<String> bannedWordList = new ArrayList<>();
    private ServerSocket serverSocket;
    private Socket client;
    private static Map<ClientThread, Socket> allClients;
    private static List<String> commands = new ArrayList<>();

    static{
        key = currentCapacity;
        commands.add("@bs");
        commands.add("@lcp");
        commands.add("@mT");
        commands.add("@mE");
        commands.add("@e");
        commands.add("@add");
        currentCapacity = 0;
        allClients = new HashMap<>();
    }

    public MyServer(){
        try {
            File file = new File(System.getProperty("user.dir") + "/resources/ConfigurationFile.txt");
            Scanner scanner = new Scanner(file);
            bannedWordList = new ArrayList<>();
            name = scanner.next();
            port = Integer.parseInt(scanner.next());
            address = "127.0.0.1";

            checkBannedWords();

            //NETWORK ESTABLISHMENT
            System.out.println("Server started on port " + port);
            serverSocket = new ServerSocket(port);
            client = null;

        }catch (SocketException e){
            e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("File not found here");
        }
    }
    public void listen(){
        while (true) {
            try {
                client = serverSocket.accept();
                boolean canConnect = true;
                synchronized (key){
                    if(currentCapacity >= maxCapacity){
                        client.close();
                        canConnect = false;
                    }
                }
                if(canConnect) {
                    ClientThread temp;
                    synchronized (allClients) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        temp = new ClientThread(reader.readLine(), client);
                        allClients.put(temp, client);
                    }

                    temp.start();
                    MyServer.userEntered(temp);
                    synchronized (key) {
                        ++currentCapacity;
                    }
                }
            } catch (IOException e) {
                System.out.println("Accept Failed");
                System.exit(-1);
            }
        }
    }
    public static void userEntered(ClientThread newClient){
        synchronized (allClients){
            Set<ClientThread> list = allClients.keySet();
            Iterator<ClientThread> iterator = list.iterator();
            while(iterator.hasNext()){
                ClientThread temp = iterator.next();
                if(temp != newClient) temp.getMessenger().sendMessage(newClient + " has entered the server!");
            }
        }
    }
    public static void userLeft(ClientThread oldClient){
        synchronized (allClients){
            //REMOVING FROM MAP
            Set<ClientThread> list = allClients.keySet();
            Iterator<ClientThread> iterator = list.iterator();

            while(iterator.hasNext()){
                ClientThread temp = iterator.next();
                if(temp == oldClient) allClients.remove(temp);
            }

            //ANNOUNCING DEPARTURE
            list = allClients.keySet();
            iterator = list.iterator();

            while(iterator.hasNext()){
                ClientThread temp = iterator.next();
                temp.getMessenger().sendMessage(oldClient.toString() + " Has Left the Server");
            }
        }
        synchronized (key) {
            currentCapacity = allClients.size();
        }
    }
    public static String getIntro(){return "Welcome to the server MyWorld!" +
            "\nHere is a list of commands:" +
            "\n@bs -> Banned Phrases\n@lcp -> List of Connected People\n@mT -> Message Specific People" +
            "\n@mE -> Message All Except\n@e -> Exit Server" + "\nWhen using @mE and @mT" +
            "\nBe sure to put\nnicknames in [] separated by |\nFor example:\n@mT [nickname1|nickname2] message";}
    public static void checkBannedWords(){
        synchronized (bannedWordList) {
            bannedWordList.clear();
            try {
                File file = new File(System.getProperty("user.dir") + "/resources/ConfigurationFile.txt");
                Scanner scanner = new Scanner(file);
                scanner.next();
                scanner.next();
                while (scanner.hasNext()) {
                    bannedWordList.add(scanner.next());
                }
            } catch (FileNotFoundException e) {
                System.out.println("FILE NOT FOUND WHEN CHECKING BANNED PHRASES");
            }
        }
    }
    public static boolean isCommand(String message){
        return MyServer.commands.contains(message) || message.contains("@mT") || message.contains("@mE")
                || message.contains("@add") || message.contains("@rm");
    }
    public static String getCommand(String message){
        if(message.contains("@bs")) return "@bs";
        else if(message.contains("@e")) return "@e";
        else if(message.contains("@lcp")) return "@lcp";
        else if(message.contains("@mT")) return "@mT";
        else if(message.contains("@add")) return "@add";
        else if(message.contains("@rm")) return "@rm";
        else return "@mE";
    }
    public static int getCurrentCapacity(){return currentCapacity;}
    public static List<String> getBannedWordList(){return bannedWordList;}

    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String line;

        while (true) {
            line = scanner.nextLine();
            if (isCommand(line)) {

                String command = getCommand(line);
                switch (command) {
                    case ("@bs"):
                        System.out.println(bannedWordList);
                        break;
                    case "@add":
                        if(line.length() > 5) {
                            String word = " " + line.subSequence(5, line.length());
                            try {
                                FileWriter myFWriter = new FileWriter("src\\ConfigurationFile.txt", true);
                                BufferedWriter myBWriter = new BufferedWriter(myFWriter);;
                                myBWriter.write(word);
                                myBWriter.newLine();
                                myBWriter.close();
                                checkBannedWords();
                            } catch (IOException e) {
                                System.out.println("ERROR WHEN APPENDING");
                            }
                        }
                        break;
                    case "@rm":
                        if(line.length() > 4){
                            String word = (String) line.subSequence(4, line.length());
                            try{
                                //INPUT
                                File file = new File("src\\ConfigurationFile.txt");
                                Scanner myScanner = new Scanner(file);
                                String replacement = "";
                                String temp;
                                while(myScanner.hasNext()){
                                    temp = myScanner.next();
                                    if(!temp.equals(word)) replacement += temp + " ";
                                }
                                myScanner.close();

                                //OUTPUT
                                FileWriter fw = new FileWriter(file);
                                BufferedWriter writer = new BufferedWriter(fw);
                                writer.write(replacement);
                                writer.close();
                            }catch (IOException e){
                                System.out.println("ERROR REMOVING PHRASE");
                            }
                            checkBannedWords();
                        }
                }
            } ;
        }
    }
    public static class ClientThread extends Thread{
        private String myUsername;
        private ClientListener myListener;
        private ClientMessenger myMessenger;
        private Socket mySocket;
        public ClientThread(String username, Socket socket){
            myUsername = username;
            mySocket = socket;
            myMessenger = new ClientMessenger(mySocket);
            myListener = new ClientListener(mySocket, myMessenger, this);
        }
        public String toString(){return myUsername;}
        @Override
        public void run(){
            myMessenger.start();

            try{
                Thread.sleep(300);
            }catch (InterruptedException e){
                System.out.println("Error here");
            }

            myMessenger.sendMessage(MyServer.getIntro());

            try{
                Thread.sleep(300);
            }catch (InterruptedException e){
                System.out.println("Error here");
            }

            myListener.start();
        }
        public ClientMessenger getMessenger(){return myMessenger;}
    }
    public static class ClientListener extends Thread{
        private Socket socket;
        private BufferedReader listener;
        private ClientMessenger messenger;
        private ClientThread clientThread;
        public ClientListener(Socket socket, ClientMessenger mes, ClientThread client){
            this.clientThread = client;
            this.messenger = mes;
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
                while (socket.isConnected()) {
                    //CHECKING WHETHER THERE ARE BANNED PHRASES
                    boolean safe = true;
                    String line = listener.readLine();
                    Iterator<String> bannedIterator = bannedWordList.iterator();
                    while (bannedIterator.hasNext()) {
                        String word = bannedIterator.next();
                        if (line.contains(word)) safe = false;
                    }
                    //CHECKING IF THERE ARE COMMANDS
                    if (MyServer.isCommand(line)) {
                        String command = MyServer.getCommand(line);
                        switch (command){
                            case "@bs":
                                messenger.sendMessage(MyServer.getBannedWordList().toString());
                                break;
                            case "@lcp":
                                if(getCurrentCapacity() == 1) messenger.sendMessage("Only You're Connected");
                                else{
                                    Set<ClientThread> list = allClients.keySet();
                                    Iterator<ClientThread> iterator = list.iterator();
                                    String lcpMessage = "Number of Connected People: " + MyServer.getCurrentCapacity() +
                                            "\nList of Connected People: \n";
                                    while(iterator.hasNext()){
                                        lcpMessage += "\n" + iterator.next().toString();
                                    }
                                    messenger.sendMessage(lcpMessage);
                                }
                                break;
                            case "@mE":
                                Pattern pattern = Pattern.compile("@\\w+\\s\\[(.*?)]");
                                Matcher matcher = pattern.matcher(line);

                                if(matcher.find()){
                                    String nicknamesText = matcher.group(1);
                                    String[] nicknames = nicknamesText.split("\\|");

                                    Set<ClientThread> list = allClients.keySet();
                                    Iterator<ClientThread> iterator = list.iterator();

                                    while(iterator.hasNext()){
                                        ClientThread user = iterator.next();
                                        boolean canSend = true;
                                        for(int i = 0, stopper = 0; i < nicknames.length && stopper == 0; ++i){
                                            if(user.toString().equals(nicknames[i])){
                                                ++stopper;
                                                canSend = false;
                                            }
                                        }
                                        if(canSend && user != clientThread) user.getMessenger().sendMessage(line, clientThread.toString());
                                    }
                                }
                                break;
                            case "@mT":
                                Pattern patternDirect = Pattern.compile("@\\w+\\s\\[(.*?)]");
                                Matcher matcherDirect = patternDirect.matcher(line);

                                if(matcherDirect.find()){
                                    String nicknamesText = matcherDirect.group(1);
                                    String[] nicknames = nicknamesText.split("\\|");

                                    Set<ClientThread> list = allClients.keySet();
                                    Iterator<ClientThread> iterator = list.iterator();

                                    while(iterator.hasNext()){
                                        ClientThread user = iterator.next();
                                        for(int i = 0, stopper = 0; i < nicknames.length && stopper == 0; ++i){
                                            if(user.toString().equals(nicknames[i])){
                                                user.getMessenger().sendMessage(line, this.clientThread.toString());
                                                ++stopper;
                                            }
                                        }
                                    }
                                }
                                break;
                            case "@e":
                                socket.close();
                                MyServer.userLeft(this.clientThread);
                                break;
                        }
                    }
                    //SEND DEFAULT TO EVERYONE
                    else if (safe) {
                        Set<ClientThread> list = allClients.keySet();
                        Iterator<ClientThread> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            ClientThread temp = iterator.next();
                            if (temp != clientThread) temp.getMessenger().sendMessage(line, clientThread.toString());
                        }
                    }
                }
            }catch (IOException e){
                System.out.println("ERROR READING CLIENT MESSAGE");
            }
        }
    }
    public static class ClientMessenger extends Thread{
        private final String breaker = "th1s 1s th@ @nd";
        private Socket socket;
        private PrintWriter writer;
        private String message;
        public ClientMessenger(Socket socket){
            this.socket = socket;
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
            }catch (IOException e){
                System.out.println("ERROR CREATING CLIENT WRITER");
            }
        }
        @Override
        public void run(){
            try{
                while (socket.isConnected()) {
                    synchronized (this) {
                        wait();
                        writer.println(message);
                        writer.println(breaker);
                    }
                }
            }catch (InterruptedException e){
                System.out.println("ERROR SENDING MESSAGE");
            }
        }
        public void sendMessage(String message){
            this.message = "Server\n" + message;
            synchronized (this){
                notify();
            }
        }
        public void sendMessage(String message, String sender){
            this.message = "@" + sender +"\n" +message;
            synchronized (this){
                notify();
            }
        }
    }
}