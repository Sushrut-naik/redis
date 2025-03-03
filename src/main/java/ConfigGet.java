import java.util.ArrayList;

public class ConfigGet implements CommandExecutor{
    @Override
    public String execute(ArrayList command) {
        if(command.get(2).equals("dir")){
            return "*2\r\n$3\r\ndir\r\n$"+GlobalVars.dir.length()+"\r\n"+GlobalVars.dir+"\r\n";
        }
        else if(command.get(2).equals("dbfilename")){
            return "*2\r\n$10\r\ndbfilename\r\n$"+GlobalVars.dbFilename.length()+"\r\n"+GlobalVars.dbFilename+"\r\n";
        }
        else
            return "$-1\r\n";
    }
}
