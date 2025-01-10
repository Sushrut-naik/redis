import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

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
}
