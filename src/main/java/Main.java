import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

public class Main {
    private static final int PORT = 6379;
    private Selector selector;
    private ServerSocketChannel serverChannel;

    public static void main(String[] args) throws IOException {
        Main server = new Main();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Main() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port " + PORT);
    }

    public void start() throws IOException {
        while (true) {
            selector.select();  // Wait for an event
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    acceptConnection();
                } else if (key.isReadable()) {
                    handleClientRequest(key);
                }
            }
        }
    }

    private void acceptConnection() throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, new CommandParser(client));
        System.out.println("Client connected: " + client.getRemoteAddress());
    }

    private void handleClientRequest(SelectionKey key) throws IOException {
        CommandParser parser = (CommandParser) key.attachment();
        parser.processCommand();
    }
}
