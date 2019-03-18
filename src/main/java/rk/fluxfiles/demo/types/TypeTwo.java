package rk.fluxfiles.demo.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.fluxfiles.demo.Spec;
import rk.fluxfiles.demo.TypeConfig;
import rk.fluxfiles.demo.utils.Pair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



@ToString
@NoArgsConstructor
@Component
@Slf4j
public class TypeTwo implements Spec {

    private List<Pair> keys1;
    private List<Pair> keys2;

    @Autowired
    private TypeConfig typeConfig;

    public void init()
    {
        keys1 = setKey(typeConfig.getKeys1());
        keys2 = setKey(typeConfig.getKeys2());
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
                .map(v -> record.trim().substring(v.getValue1(), v.getValue2()))
                .collect(Collectors.joining());
    }

    private List<Pair> setKey(String[] keys)
    {
        if (keys[0].equals("all") )
        {
            return null;
        }

        return Stream.of(keys)
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
