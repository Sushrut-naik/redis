package utils;

import java.util.HashMap;
import java.util.Map;
public class DataStore {
    private static final Map<String, Pair> dataStore = new HashMap<>();

    public static Map<String, Pair> getDataStore() {
        return dataStore;
    }
}