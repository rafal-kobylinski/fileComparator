package rk.fluxfiles.demo;

import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
public class Comp {

    private static Map<String, String> streamOne = new HashMap<>();
    private static Map<String, String> streamTwo = new HashMap<>();
    private static String[] keys;

    public static void compare(String one, String two)
    {
        streamOne.put(getRecordKey(one), one);
        streamTwo.put(getRecordKey(two), two);
    }

    public static String output()
    {
        return "\none: " + streamOne.entrySet()+ "\ntwo: " + streamTwo.entrySet();
    }

    private static String getRecordKey(String record)
    {
        String[] splitted = record.split(",");
        StringBuilder output = new StringBuilder();
        for (String index: keys)
        {
            output.append(splitted[Integer.valueOf(index)]);
        }

        return output.toString();
    }

    public static void loadProperties()
    {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "TYPE1.txt";

        Properties appProps = new Properties();

        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String delimeter = appProps.getProperty("delimeter");
        keys = appProps.getProperty("key1").split(delimeter);
    }

}
