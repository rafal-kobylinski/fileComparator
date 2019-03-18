package rk.fluxfiles.demo.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import rk.fluxfiles.demo.Spec;
import rk.fluxfiles.demo.TypeConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


@Slf4j
@NoArgsConstructor
@Component
@ToString
public class TypeOne implements Spec {

    private String delimeter;
    private String[] keys1;
    private String[] keys2;

    @Autowired
    private TypeConfig typeConfig;

    public void init()
    {
        this.delimeter = typeConfig.getDelimeter();
        this.keys1 = typeConfig.getKeys1();
        this.keys2 = typeConfig.getKeys2();
    }


    public String getKey(String record)
    {
        return generateKey(keys1, record);
    }

    public String getKey2(String record)
    {
        return generateKey(keys2, record);
    }


    public String generateKey(String[] keys, String record)
    {
        if (keys[0].equals("all") )
        {
            return record;
        }

        String[] splitted = record.split(delimeter);
        StringBuilder output = new StringBuilder();
        for (String index: keys)
        {
            output.append(splitted[Integer.valueOf(index)]);
        }

        return output.toString();
    }


}
