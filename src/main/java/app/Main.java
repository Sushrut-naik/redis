package app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

import utils.CommandParser;
import utils.GlobalVars;
import utils.PersistenceHandler;
import utils.Role;

public class Main {
    private static final int PORT = 6379;
    private Selector selector;
    private ServerSocketChannel serverChannel;

    public static void main(String[] args) throws IOException {
        try{
            parseArgs(args);
        }catch (IOException e) {
            e.printStackTrace();
        }
        PersistenceHandler handler = new PersistenceHandler();
        handler.parseRDBFile();
        Main server = new Main();
        try {
            server.start();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Main() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(GlobalVars.PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port " + GlobalVars.PORT);
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

    private static void parseArgs(String[] args) throws IOException {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--dir")) {
                GlobalVars.DIR = args[i + 1];
            }
            else if (args[i].equals("--dbfilename")) {
                GlobalVars.DBFILENAME = args[i + 1];
            }
            else if(args[i].equals("--port")){
                GlobalVars.PORT = Integer.parseInt(args[i+1]);
            }
            else if(args[i].equals("--replicaof")){
                String replication_info = args[i+1];
                String[] info = replication_info.split(" ");
                if(info.length != 2){
                    throw new IOException("Invalid arguments to --replicaof flag");
                }
                GlobalVars.MASTER_HOST = info[0];
                GlobalVars.MASTER_PORT = Integer.parseInt(info[1]);
                GlobalVars.ROLE = Role.slave;
            }
        }
    }
}
