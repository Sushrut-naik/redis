package commands;

import java.util.ArrayList;

public class Ping implements CommandExecutor{
    @Override
    public String execute(ArrayList command) {
        if(command.size() == 1)
            return "+PONG\r\n";
        String retVal="+";

        for(int i=1;i<command.size();i++){
            retVal += command.get(i)+" ";
        }
        retVal += "\r\n";
        return retVal;
    }
}