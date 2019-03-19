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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@NoArgsConstructor
@Component
@Data
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

    private String generateKey(String[] keys, String record)
    {
        if (keys[0].equals("all") )
        {
            return record;
        }
        String[] splitted = record.split(delimeter, -1);
        StringBuilder output = new StringBuilder();
        for (String index: keys)
        {
            output.append(splitted[Integer.valueOf(index)]);
        }

        return output.toString();
    }

    public String createComparisonReport(String[] records)
    {
        StringBuilder output = new StringBuilder();
        Boolean isDictAvailable;
        String record1 = records[0];
        String[] splitted1 = record1.split(delimeter, -1);
        String record2 = records[1];
        String[] splitted2 = record2.split(delimeter, -1);
        ArrayList<String> arrayOfKeys2 = new ArrayList<>(Arrays.asList(keys2));


        int howManyDiffs = 0;
        StringBuilder diffs = new StringBuilder();

        Map dict = typeConfig.getDictionary();
        Set<String> keys = dict.keySet();

        for (int i=0; i < keys.size(); i++)
        {
                    Boolean different = false;
                    String isKeyed = "";
                    String value1 = splitted1[Integer.valueOf(i)];
                    String value2 = splitted2[Integer.valueOf(i)];
                    if (arrayOfKeys2.contains(String.valueOf(i)))
                    {
                        if (!value1.equals(value2))
                        {
                            different = true;
                            howManyDiffs += 1;
                            diffs.append(i + " ");
                        }
                        isKeyed = "*";
                    }
                    output.append(String.format("|%30s|%10s|%20s|%20s|%2s\n", dict.get(String.valueOf(i)), i, value1, value2, different ? "<<" : isKeyed));
        }

        output.insert(0, (record1 + "\n"
                + record2 + "\n"
                + "Differences: " + howManyDiffs + ", on position" + (howManyDiffs==1?": ":"s: ") + diffs.toString() + "\n"));

        return output.toString();
    }
}
