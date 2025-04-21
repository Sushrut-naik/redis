package commands;

import utils.CommandExecutor;

import java.util.ArrayList;

public class InvalidCommand implements CommandExecutor {
    @Override
    public String execute(ArrayList command) {
        return "-ERR Unknown command\r\n";
    }
}
