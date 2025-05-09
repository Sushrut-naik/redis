package utils;

import commands.*;

public class CommandExecutorFactory {
    public static CommandExecutor getCommandExecutor(String command) {
        switch (command.toLowerCase()) {
            case "ping":
                return new Ping();
            case "echo":
                return new Echo();
            case "get":
                return new Get();
            case "set":
                return new Set();
            case "config":
                return new ConfigGet();
            case "keys":
                return new Keys();
            case "info":
                return new Info();
            default:
                return new InvalidCommand();
        }
    }
}
