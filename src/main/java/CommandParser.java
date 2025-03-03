import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CommandParser {
    private final SocketChannel client;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private final StringBuilder commandBuffer = new StringBuilder();

    public CommandParser(SocketChannel client) {
        this.client = client;
    }

    public void processCommand() throws IOException {
        buffer.clear();
        int bytesRead = client.read(buffer);

        if (bytesRead == -1) { // Client disconnected
            client.close();
            return;
        }

        buffer.flip();
        commandBuffer.append(StandardCharsets.UTF_8.decode(buffer));

        // Check if a full command is received
        if (commandBuffer.toString().endsWith("\r\n")) {
            String command = commandBuffer.toString().trim();
            commandBuffer.setLength(0); // Reset buffer

            System.out.println("Received command: [" + command + "]");
            ArrayList<String> commandParts = parseRESP(command);

            if (!commandParts.isEmpty()) {
                String commandName = commandParts.get(0);
                CommandExecutor cmdExe = CommandExecutorFactory.getCommandExecutor(commandName);
                String response = cmdExe.execute(commandParts);

                sendResponse(response);
            }
        }
    }

    private ArrayList<String> parseRESP(String command) {
        String[] commandSplit = command.split("\r\n");
        ArrayList<String> commandBreakup = new ArrayList<>();

        for (int i = 2; i < commandSplit.length; i += 2) { // Skip the array length and every other metadata
            commandBreakup.add(commandSplit[i]);
        }
        return commandBreakup;
    }

    private void sendResponse(String response) throws IOException {
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
        client.write(responseBuffer);
    }
}
