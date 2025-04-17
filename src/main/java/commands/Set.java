package commands;

import utils.DataStore;
import utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class Set implements CommandExecutor{
    @Override
    public String execute(ArrayList command) {
        if(command.size() == 3){
            Map<String, Pair> dataStore = DataStore.getDataStore();
            Pair val = new Pair(command.get(2).toString(), Long.MAX_VALUE);
            dataStore.put(command.get(1).toString(), val);
        }
        else if(command.size() == 5){
            Map<String, Pair> dataStore = DataStore.getDataStore();
            Pair val = new Pair(command.get(2).toString(), System.currentTimeMillis()+Integer.parseInt(command.get(4).toString()));
            dataStore.put(command.get(1).toString(), val);
        }
        System.out.println("Returning after storing\n");
        return "+OK\r\n";
    }
}
