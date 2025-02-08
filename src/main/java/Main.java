import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args){
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");
        parseArgs(args);
        System.out.println("Redis Directory: " + GlobalVars.dir);
        System.out.println("DB Filename: " + GlobalVars.dbFilename);
        //  Uncomment this block to pass the first stage
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while(true){
                // Wait for connection from client.
                clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                // Input and Output streams for communication
                CommandParser parser = new CommandParser(clientSocket);
                Thread thread = new Thread(parser);

                thread.start();
            }
        }
        catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--dir")) {
                GlobalVars.dir = args[i + 1];
            } else if (args[i].equals("--dbfilename")) {
                GlobalVars.dbFilename = args[i + 1];
            }
        }
    }
}
