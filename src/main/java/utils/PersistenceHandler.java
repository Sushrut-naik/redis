package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class PersistenceHandler {

    public void parseRDBFile() {
        String filePath = GlobalVars.DIR + File.separator + GlobalVars.DBFILENAME;
        int numElements = 0, numElementsWithExpiry = 0;
        Map<String, Pair> dataStore = DataStore.getDataStore();
        boolean parsingKeys = false;

        System.out.println("Starting parsing");

        try (FileInputStream fis = new FileInputStream(filePath)) {
            int b;
            String currentKey = null;
            Long expiry = null;

            while ((b = fis.read()) != -1) {
                byte marker = (byte) b;

                // Print for debugging
                System.out.printf("Marker: %02X\n", marker);

                switch (marker & 0xFF) {
                    case 0xFB: // Start of hash table
                        numElements = decodeLengthEncoding(fis);
                        numElementsWithExpiry = decodeLengthEncoding(fis);
                        parsingKeys = true;
                        System.out.println("Got the size of hash table: " + numElements + " elements, " + numElementsWithExpiry + " with expiry");
                        break;

                    case 0xFC: // Expiry (in milliseconds)
                        expiry = decodeExpiry(fis, 8);
                        System.out.println("Expiry (ms): " + expiry);
                        break;

                    case 0xFD: // Expiry (in seconds)
                        expiry = decodeExpiry(fis, 4);
                        System.out.println("Expiry (s): " + expiry);
                        break;

                    case 0xFF: // End of file
                        System.out.println("End of RDB parsing.");
                        return;

                    default:
                        if (parsingKeys) {
                            // Step 1: marker is actually the value type (e.g., 0x00 for string, etc.)
                            byte valueType = marker;
                            System.out.println("Value Type: " + String.format("%02X", valueType));

                            // Step 2: Read key
                            byte[] keyBytes = readStringBytes((byte) fis.read(), fis);
                            String key = decodeStringEncoding(keyBytes);
                            System.out.println("Key: " + key);

                            // Step 3: Read value
                            byte[] valueBytes = readStringBytes((byte) fis.read(), fis);
                            String value = decodeStringEncoding(valueBytes);
                            System.out.println("Value: " + value);

                            // Step 4: Put in data store
                            long expiryTimestamp = (expiry != null) ? expiry : Long.MAX_VALUE;
                            dataStore.put(key, new Pair(value, expiryTimestamp));
                            System.out.println("Put: (" + key + ", " + value + ", expiry=" + expiryTimestamp + ")");

                            // Reset expiry
                            expiry = null;
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int decodeLengthEncoding(FileInputStream fis) throws IOException {
        int firstByte = fis.read();
        int type = (firstByte & 0xC0) >> 6;

        switch (type) {
            case 0b00:
                return firstByte & 0x3F;

            case 0b01:
                int secondByte = fis.read();
                return ((firstByte & 0x3F) << 8) | secondByte;

            case 0b10:
                byte[] lengthBytes = new byte[4];
                fis.read(lengthBytes);
                return ((lengthBytes[0] & 0xFF) << 24) | ((lengthBytes[1] & 0xFF) << 16) |
                        ((lengthBytes[2] & 0xFF) << 8) | (lengthBytes[3] & 0xFF);

            case 0b11:
                throw new UnsupportedOperationException("Special encoding not supported");

            default:
                throw new IllegalStateException("Unexpected length encoding");
        }
    }

    private long decodeExpiry(FileInputStream fis, int byteSize) throws IOException {
        byte[] buffer = new byte[byteSize];
        if (fis.read(buffer) != byteSize) {
            throw new IOException("Failed to read expiry");
        }

        long expiry = 0;
        for (int i = byteSize - 1; i >= 0; i--) {
            expiry = (expiry << 8) | (buffer[i] & 0xFF); // little-endian
        }
        return expiry;
    }

    private byte[] readStringBytes(int firstByte, FileInputStream fis) throws IOException {
        int type = (firstByte & 0xC0) >> 6;

        switch (type) {
            case 0b00: {
                int length = firstByte & 0x3F;
                byte[] data = new byte[length];
                fis.read(data);
                return data;
            }

            case 0b01: {
                int secondByte = fis.read();
                int length = ((firstByte & 0x3F) << 8) | secondByte;
                byte[] data = new byte[length];
                fis.read(data);
                return data;
            }

            case 0b10: {
                byte[] lenBytes = new byte[4];
                fis.read(lenBytes);
                int length = ((lenBytes[0] & 0xFF) << 24) | ((lenBytes[1] & 0xFF) << 16) |
                        ((lenBytes[2] & 0xFF) << 8) | (lenBytes[3] & 0xFF);
                byte[] data = new byte[length];
                fis.read(data);
                return data;
            }

            case 0b11:
                throw new UnsupportedOperationException("Special string encoding not implemented");

            default:
                throw new IllegalArgumentException("Unknown string encoding");
        }
    }

    private String decodeStringEncoding(byte[] bytes) {
        return new String(bytes); // Assuming it's UTF-8 encoded. May need adjustments for Redis encoding.
    }
}
