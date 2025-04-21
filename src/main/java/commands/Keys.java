package commands;

import utils.CommandExecutor;
import utils.DataStore;
import utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class Keys implements CommandExecutor {
    @Override
    public String execute(ArrayList command) {
        Map<String, Pair> dataStore = DataStore.getDataStore();

        StringBuilder resp = new StringBuilder();
        resp.append("*").append(dataStore.size()).append("\r\n");

        for (String key : dataStore.keySet()) {
            resp.append("$").append(key.length()).append("\r\n");
            resp.append(key).append("\r\n");
        }

        return resp.toString();
    }
}
