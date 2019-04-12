package fcomp.application.types;

import fcomp.application.configuration.dictionary.Dict;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.utils.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@NoArgsConstructor
@Data
@Component
public class TypeThree implements Spec {

    private String delimiter;
    private Map<String, List<Pair>> keys1;
    private Map<String, List<Pair>> keys2;

    @Autowired
    private TypeProperties typeProperties;
    @Autowired
    private Dict dict;

    private Pair subtype_position;

    public void init()
    {
        this.delimiter = typeProperties.getDelimiter();

        String key1Props = typeProperties.getKeys1();
        this.keys1 = parseKeyProps(key1Props);

        String key2Props = typeProperties.getKeys2();
        this.keys2 = parseKeyProps(key2Props);

        String[] subtype = typeProperties.getSubtypePosition().split("-");
        this.subtype_position = new Pair(Integer.valueOf(subtype[0]), Integer.valueOf(subtype[1]));
    }

    public Boolean checkIfInKeys(String index, String record) {

        if (keys2 == null ) return true;

        String[] splitted = index.split("-");
        Pair range = new Pair(Integer.valueOf(splitted[0]), Integer.valueOf(splitted[1]));
        for (Pair key : keys2.get(getSubType(record)))
        {
            if ((key == null) || (key.getValue1() == range.getValue1() && key.getValue2() == range.getValue2())) return true;
        }

        return false;
    }

    public String getFieldValue(String index, String record) {
        String[] splitted = index.split("-");
        return record.substring(Integer.valueOf(splitted[0]), Integer.valueOf(splitted[1]) + 1);
    }

    public Map<String, String> getRecordDictionary(String record1) {
        return dict.getDictionary().get(getSubType(record1));
    }

    public String getKey(String record)
    {
        if (keys1 == null) {
            return record;
        } else {
            return generateRecordKey(keys1.get(getSubType(record)), record);
        }
    }

    public String getKey2(String record)
    {
        if (keys2 == null) {
            return record;
        } else {
            return generateRecordKey(keys2.get(getSubType(record)), record);
        }
    }

    private String getSubType(String record)
    {
        return record.substring(subtype_position.getValue1(), subtype_position.getValue2() + 1);
    }

    private Map<String, List<Pair>> parseKeyProps(String key)
    {
        if (key.equals("all")) return null;

        Pattern pattern = Pattern.compile("([0-9]{2}):\\[(.+?)\\],*");
        Matcher matcher = pattern.matcher(key);

        Map<String, List<Pair>> output = new LinkedHashMap<>();

        while (matcher.find()) {
            String type = matcher.group(1);

            String keysString = matcher.group(2);
            if (keysString.equals("all"))
            {
                output.put(type, null);
                continue;
            }

            List<Pair> keys = Arrays
                    .asList(keysString.split(","))
                    .stream()
                    .map(v -> v.split("-"))
                    .map(k -> new Pair(
                            Integer.valueOf(k[0]),
                            Integer.valueOf(k[1]))
                    ).collect(Collectors.toList());
            output.put(type, keys);
        }

        return output;
    }

    private String generateRecordKey(List<Pair> keys, String record)
    {
        if (keys == null) return record;

        return keys
                .stream()
                .map(v -> record.substring(v.getValue1(), v.getValue2() + 1))
                .collect(Collectors.joining());
    }


}
