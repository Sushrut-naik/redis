package commands;

import utils.DataStore;
import utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class Get implements CommandExecutor{
    @Override
    public String execute(ArrayList command) {
        String key = command.get(1).toString();
        Map<String, Pair> dataStore = DataStore.getDataStore();
        Pair val = dataStore.get(key);
        if(val == null){
            return "$-1\r\n";
        }
        if(val.getTimestamp() > System.currentTimeMillis()){
            String retVal = val.getValue();
            return "$"+retVal.length()+"\r\n"+retVal+"\r\n";
        }
        else{
            dataStore.remove(key);
        }
        return "$-1\r\n";
    }
}
