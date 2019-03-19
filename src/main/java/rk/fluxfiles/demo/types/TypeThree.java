package rk.fluxfiles.demo.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.fluxfiles.demo.Spec;
import rk.fluxfiles.demo.TypeConfig;


@Slf4j
@NoArgsConstructor
@Data
@Component
public class TypeThree implements Spec {

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

        log.info("Type1 initialized: " + this.toString());
    }

    @Override
    public String getKey(String record)
    {
        if (keys1[0].equals("all") )
        {
            return record;
        }

        String[] splitted = record.split(delimeter);
        StringBuilder output = new StringBuilder();
        for (String index: keys1)
        {
            output.append(splitted[Integer.valueOf(index)]);
        }

        return output.toString();
    }

    public String getKey2(String record)
    {
        if (keys2[0].equals("all") )
        {
            return record;
        }

        String[] splitted = record.split(delimeter);
        StringBuilder output = new StringBuilder();
        for (String index: keys2)
        {
            output.append(splitted[Integer.valueOf(index)]);
        }

        return output.toString();
    }

}
