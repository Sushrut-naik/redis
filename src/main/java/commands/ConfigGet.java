package commands;

import utils.CommandExecutor;
import utils.GlobalVars;

import java.util.ArrayList;

public class ConfigGet implements CommandExecutor {
    @Override
    public String execute(ArrayList command) {
        if(command.get(2).equals("dir")){
            return "*2\r\n$3\r\ndir\r\n$"+ GlobalVars.DIR.length()+"\r\n"+ GlobalVars.DIR+"\r\n";
        }
        else if(command.get(2).equals("dbfilename")){
            return "*2\r\n$10\r\ndbfilename\r\n$"+ GlobalVars.DBFILENAME.length()+"\r\n"+ GlobalVars.DBFILENAME+"\r\n";
        }
        else
            return "$-1\r\n";
    }
}
