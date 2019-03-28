package fcomp.application.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.configuration.dictionary.Dict;

import java.util.*;


@Slf4j
@NoArgsConstructor
@Component
@Data
public class TypeOne implements Spec {

    private String delimeter;
    private List<String> keys1;
    private List<String> keys2;


    @Autowired
    private TypeProperties typeProperties;
    @Autowired
    private Dict dict;

    public void init()
    {
        this.delimeter = typeProperties.getDelimeter();
        this.keys1 = Arrays.asList(typeProperties.getKeys1().split(","));
        this.keys2 = Arrays.asList(typeProperties.getKeys2().split(","));
    }


    public String getKey(String record)
    {
        return generateKey(keys1, record);
    }

    public String getKey2(String record)
    {
        return generateKey(keys2, record);
    }

    private String generateKey(List<String> keys, String record)
    {
        if (keys.get(0).equals("all") )
        {
            return record;
        }
        String[] splitted = record.split(delimeter, -1);
        StringBuilder output = new StringBuilder();
        for (String key: keys)
        {
            output.append(splitted[Integer.valueOf(key)]);
        }

        return output.toString();
    }



    public String getFieldValue(String index, String record)
    {
        return record.split(delimeter, -1)[Integer.valueOf(index)];
    }

    public Map<String, String> getRecordDictionary(String record1) {
        if (dict.getDictionary() == null) {
            return null;
        } else {
            return dict.getDictionary().get("00");
        }
    }

    public Boolean checkIfInKeys(String index, String record)
    {
        if (keys2.get(0).equals("all") || keys2.contains(index)) return true;
        return false;
    }
}
