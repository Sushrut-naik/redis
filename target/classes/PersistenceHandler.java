import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class PersistenceHandler {
    void parseRDBFile(){
        String filePath = GlobalVars.dir + File.separator + GlobalVars.dbFilename;
        int numElements = 0, numElementsWithExpiry = 0;
        Map<String, Pair> dataStore = DataStore.getDataStore();
        boolean parsingKeys = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentKey = null;
            Long expiry = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("FB")) {
                    numElements = decodeLengthEncoding(br.readLine().trim());
                    numElementsWithExpiry = decodeLengthEncoding(br.readLine().trim());
                    parsingKeys = true;
                    continue;
                }

                if (line.equals("FC") || line.equals("FD")) { // Expiry information
                    int byteSize = line.equals("FC") ? 8 : 4;
                    expiry = decodeExpiry(br, byteSize);
                    continue;
                }

                if (parsingKeys) {
                    if (currentKey == null) {
                        currentKey = decodeStringEncoding(line);
                    } else {
                        String value = decodeStringEncoding(line);
                        long expiryTimestamp = (expiry != null) ? expiry : -1L; // -1 indicates no expiry
                        dataStore.put(currentKey, new Pair(value, expiryTimestamp));
                        expiry = null;
                        currentKey = null;
                    }
                }

                if (line.equals("FF")) { // End of file section
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int decodeLengthEncoding(String hexString) {
        if (hexString.length() < 2) {
            throw new IllegalArgumentException("Invalid hex input: At least one byte is required.");
        }

        // Read the first byte as an integer
        int firstByte = Integer.parseInt(hexString.substring(0, 2), 16);
        int type = (firstByte & 0xC0) >> 6; // Extract the two most significant bits (MSBs)

        switch (type) {
            case 0b00: // 00xxxxxx -> The last 6 bits represent the length
                return firstByte & 0x3F;

            case 0b01: // 01xxxxxx -> Read one more byte, total 14-bit length
                if (hexString.length() < 4) throw new IllegalArgumentException("Invalid input: Expected 2 bytes.");
                int secondByte = Integer.parseInt(hexString.substring(2, 4), 16);
                return ((firstByte & 0x3F) << 8) | secondByte;

            case 0b10: // 10xxxxxx -> The next 4 bytes represent the length
                if (hexString.length() < 10) throw new IllegalArgumentException("Invalid input: Expected 5 bytes.");
                int length = Integer.parseInt(hexString.substring(2, 10), 16);
                return length;

            case 0b11: // 11xxxxxx -> Special encoding (not parsing in this case)
                int format = firstByte & 0x3F;
                throw new UnsupportedOperationException("Special encoding detected (format " + format + ")");

            default:
                throw new IllegalStateException("Unexpected encoding type");
    }

    private static String decodeStringEncoding(String hexString) {
            if (hexString.length() < 2) {
                throw new IllegalArgumentException("Invalid hex input: At least one byte is required.");
            }

            int firstByte = Integer.parseInt(hexString.substring(0, 2), 16);
            int type = (firstByte & 0xC0) >> 6; // Extract the two most significant bits

            if (type < 0b11) {
                // Regular string encoding (length is directly specified)
                int length = parseLength(hexString.substring(0, 2));
                if (hexString.length() < (2 + length * 2)) {
                    throw new IllegalArgumentException("Hex input is too short for the specified length.");
                }
                return hexToString(hexString.substring(2, 2 + length * 2));
            } else {
                // Special encoding
                int format = firstByte & 0x3F; // Remaining 6 bits
                switch (format) {
                    case 0x00: // 8-bit integer
                        return String.valueOf(Integer.parseInt(hexString.substring(2, 4), 16));

                    case 0x01: // 16-bit integer (little-endian)
                        if (hexString.length() < 6) throw new IllegalArgumentException("Expected 2 bytes.");
                        int int16 = Integer.parseInt(hexString.substring(4, 6) + hexString.substring(2, 4), 16);
                        return String.valueOf(int16);

                    case 0x02: // 32-bit integer (little-endian)
                        if (hexString.length() < 10) throw new IllegalArgumentException("Expected 4 bytes.");
                        int int32 = Integer.parseInt(
                                hexString.substring(8, 10) + hexString.substring(6, 8) + hexString.substring(4, 6) + hexString.substring(2, 4), 16);
                        return String.valueOf(int32);

                    case 0x03: // LZF compression (not implemented)
                        throw new UnsupportedOperationException("LZF compressed strings are not supported.");

                    default:
                        throw new IllegalArgumentException("Unknown special encoding format.");
                }
            }
    }

    private static long decodeExpiry(BufferedReader br, int byteSize) throws IOException {
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < byteSize; i++) {
            hexValue.insert(0, br.readLine().trim()); // Read bytes in little-endian order
        }
        return Long.parseUnsignedLong(hexValue.toString(), 16);
    }
}

