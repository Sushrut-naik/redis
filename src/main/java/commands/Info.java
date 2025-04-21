package commands;

import utils.CommandExecutor;
import utils.GlobalVars;
import utils.Role;

import java.util.ArrayList;

public class Info implements CommandExecutor {
    @Override
    public String execute(ArrayList command) {
        if(command.size() == 2 && command.get(1).equals("replication")){
            String retString;
            if(GlobalVars.ROLE == Role.slave){
                retString = "role:slave\n";
            }
            else{
                retString = "role:master\n";
                retString += "master_replid:8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb\nmaster_repl_offset:0\n";
            }
            return "$"+retString.length()+"\r\n"+retString+"\r\n";
        }
        return "$-1\r\n";
    }
}