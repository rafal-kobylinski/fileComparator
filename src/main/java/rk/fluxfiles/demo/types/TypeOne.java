package rk.fluxfiles.demo.types;

import org.springframework.stereotype.Service;
import rk.fluxfiles.demo.Spec;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TypeOne implements Spec {

    private static String[] keys;

    public TypeOne()
    {
        loadProperties();
    }

    @Override
    public String getKey(String record) {
        String[] splitted = record.split(",");
        StringBuilder output = new StringBuilder();
        for (String index: keys)
        {
            output.append(splitted[Integer.valueOf(index)]);
        }

        return output.toString();
    }

    public String getKey2(String record)
    {
        return record;
    }


    public void loadProperties()
    {
        String appConfigPath = "config/TYPE1.txt";

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
