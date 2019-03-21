package rk.fluxfiles.demo.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.fluxfiles.demo.Dict;
import rk.fluxfiles.demo.Spec;
import rk.fluxfiles.demo.TypeConfig;
import rk.fluxfiles.demo.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@NoArgsConstructor
@Data
@Component
public class TypeThree implements Spec {

    private String delimeter;
    private Map<String, List<String>> keys1;
    private Map<String, List<String>> keys2;

    @Autowired
    private TypeConfig typeConfig;
    @Autowired
    private Dict dict;
    private Pair subtype_position;

    public void init()
    {
        this.delimeter = typeConfig.getDelimeter();

        String key1Props = typeConfig.getKeys1();
        this.keys1 = parseKeyProps(key1Props);

        String key2Props = typeConfig.getKeys2();
        this.keys2 = parseKeyProps(key2Props);

        String[] subtype = typeConfig.getSubtypePosition().split("-");
        this.subtype_position = new Pair(Integer.valueOf(subtype[0]), Integer.valueOf(subtype[1]));
    }

    public Boolean checkIfInKeys(String index) {
        return null;
    }

    public String getField(String index, String record) {
        return null;
    }

    public Map<String, String> getFieldsMapping(String record1) {
        return dict.getDictionary().get(1);
    }


    public String getKey(String record)
    {
        return null;
    }

    public String getKey2(String record)
    {
        return null;
    }

    private Map<String, List<String>> parseKeyProps(String key)
    {
        Pattern pattern = Pattern.compile("([0-9]{2}):\\[(.+?)\\],*");
        Matcher matcher = pattern.matcher(key);

        Map<String, Map<String, String>> output = new HashMap<>();

        while (matcher.find()) {
            String type = matcher.group(1);
        }

        return null;
    }


}
