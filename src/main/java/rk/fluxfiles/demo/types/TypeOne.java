package rk.fluxfiles.demo.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import rk.fluxfiles.demo.Dict;
import rk.fluxfiles.demo.Spec;
import rk.fluxfiles.demo.TypeConfig;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@NoArgsConstructor
@Component
@Data
public class TypeOne implements Spec {

    private String delimeter;
    private List<String> keys1;
    private List<String> keys2;


    @Autowired
    private TypeConfig typeConfig;
    @Autowired
    private Dict dict;

    public void init()
    {
        this.delimeter = typeConfig.getDelimeter();
        this.keys1 = Arrays.asList(typeConfig.getKeys1().split(","));
        this.keys2 = Arrays.asList(typeConfig.getKeys2().split(","));
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



    public String getField(String index, String record)
    {
        return record.split(delimeter, -1)[Integer.valueOf(index)];
    }

    public Map<String, String> getFieldsMapping(String record1) {
        return dict.getDictionary().get("00");
    }

    public Boolean checkIfInKeys(String index)
    {
        if (keys2.get(0).equals("all") || keys2.contains(index)) return true;
        return false;
    }
}
