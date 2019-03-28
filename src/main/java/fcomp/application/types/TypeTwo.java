package fcomp.application.types;

import fcomp.application.utils.Dict;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.utils.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@NoArgsConstructor
@Component
@Data
@Slf4j
public class TypeTwo implements Spec {

    private List<Pair> keys1;
    private List<Pair> keys2;

    @Autowired
    private TypeConfig typeConfig;
    @Autowired
    private Dict dict;

    public void init()
    {
        keys1 = setKey(Arrays.asList(typeConfig.getKeys1().split(",")));
        keys2 = setKey(Arrays.asList(typeConfig.getKeys2().split(",")));
    }


    public Boolean checkIfInKeys(String index, String record)
    {
        if (keys2 == null) return true;

        String[] splitted = index.split("-");
        Pair range = new Pair(Integer.valueOf(splitted[0]), Integer.valueOf(splitted[1]));
        for (Pair key : keys2)
        {
            if (key.getValue1() == range.getValue1() && key.getValue2() == range.getValue2()) return true;
        }

        return false;
    }

    public String getFieldValue(String index, String record)
    {
        String[] splitted = index.split("-");
        return record.substring(Integer.valueOf(splitted[0]), Integer.valueOf(splitted[1]) + 1);
    }

    public Map<String, String> getRecordDictionary(String record1) {
        return dict.getDictionary().get("00");
    }

    public String getKey(String record)
    {
        return generateRecordKey(keys1, record);
    }


    public String getKey2(String record)
    {
        return generateRecordKey(keys2, record);
    }


    private String generateRecordKey(List<Pair> keys, String record)
    {
        if (keys == null)
        {
            return record.trim();
        }

        return keys
                .stream()
                .map(v -> record.trim().substring(v.getValue1(), v.getValue2() + 1))
                .collect(Collectors.joining());
    }

    private List<Pair> setKey(List<String> keys)
    {
        if (keys.get(0).equals("all") )
        {
            return null;
        }

        return keys
                .stream()
                .map(key -> key.split("-"))
                .map(v ->
                        new Pair(
                                Integer.valueOf(v[0]),
                                Integer.valueOf(v[1])
                        )
                )
                .collect(Collectors.toList());
    }
}
