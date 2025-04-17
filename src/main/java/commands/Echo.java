package commands;

import java.util.ArrayList;

public class Echo implements CommandExecutor{
    @Override
    public String execute(ArrayList command) {
        return "$"+command.get(1).toString().length()+"\r\n"+command.get(1)+"\r\n";
    }
}