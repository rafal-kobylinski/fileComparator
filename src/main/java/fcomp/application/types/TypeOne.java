package fcomp.application.types;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.configuration.dictionary.Dict;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TypeOne implements Spec {

    private String delimiter;
    private List<String> keys1;
    private List<String> keys2;
    private TypeProperties typeProperties;
    private Dict dict;

    @Autowired
    public TypeOne(TypeProperties typeProperties, Dict dict) {
        this.typeProperties = typeProperties;
        this.dict = dict;
    }

    public void init()
    {
        this.delimiter = typeProperties.getDelimiter();
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

        String[] splitted = record.split(delimiter, -1);
        return keys.stream()
                .map(key -> splitted[Integer.valueOf(key)])
                .collect(Collectors.joining());
    }

    public String getFieldValue(String index, String record)
    {
        return record.split(delimiter, -1)[Integer.valueOf(index)];
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
        return keys2.get(0).equals("all") || keys2.contains(index);
    }
}
