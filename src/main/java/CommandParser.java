import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CommandParser implements Runnable {
    private Socket clientSocket;
    public CommandParser(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    @Override
    public void run() {
        try {
            InputStream inStream = clientSocket.getInputStream();
            OutputStream opStream = clientSocket.getOutputStream();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024]; // Temporary buffer
            int bytesRead;

            // Listen and respond to multiple requests
            while ((bytesRead = inStream.read(data)) != -1) {

                String command = new String(data, 0, bytesRead, "UTF-8");

                // Process the request
                System.out.println("Accumulated command: [" + command + "]");
                String[] commandSplit = command.split("\r\n");
                ArrayList<String> commandBreakup = new ArrayList<String>(commandSplit[0].charAt(1));

                for(int i=2; i<commandSplit.length; i++){
                    commandBreakup.add(commandSplit[i]);
                    if(i != commandSplit.length-1){
                        i++;
                    }
                }

                System.out.println("This is the personal repo\n");
                String commandName = commandBreakup.get(0);
                CommandExecutor cmdExe = CommandExecutorFactory.getCommandExecutor(commandName);

                opStream.write(cmdExe.execute(commandBreakup).getBytes());
                data = new byte[1024];
            }
        }
        catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
        finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
