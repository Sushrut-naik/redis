public class CommandExecutorFactory {
    public static CommandExecutor getCommandExecutor(String command) {
        switch (command.toLowerCase()) {
            case "ping":
                return new Ping();
            case "echo":
                return new Echo();
            default:
                return new InvalidCommand();
        }
    }
}
