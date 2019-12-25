package chess.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class DataReader {

    public static Map<String, String> readModel(String model) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(model));
            Map<String, String> map = new HashMap<>();
            String line = reader.readLine();
            while (line != null) {
                var splittedLine = line.split(":");
                map.put(splittedLine[0], splittedLine[1]);
                line = reader.readLine();
            }
            return map;
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
