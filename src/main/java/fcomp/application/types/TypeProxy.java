package fcomp.application.types;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TypeProxy implements Spec
{

    private Spec spec;
    @Autowired
    private TypeOne typeOne;
    @Autowired
    private TypeTwo typeTwo;
    @Autowired
    private TypeThree typeThree;

    public void setType(String type)
    {
        switch (type)
        {
            case "1":
                spec = typeOne;
                spec.init();
                return;
            case "2":
                spec = typeTwo;
                spec.init();
                return;
            case "3":
                spec = typeThree;
                spec.init();
                return;
        }
    }

    public String getKey(String record)
    {
        return spec.getKey(record);
    }

    public String getKey2(String record)
    {
        return spec.getKey2(record);
    }

    public void init()
    {
    }

    public Boolean checkIfInKeys(String index, String record)
    {
        return spec.checkIfInKeys(index, record);
    }

    public String getFieldValue(String index, String record)
    {
        return spec.getFieldValue(index, record);
    }

    public Map<String, String> getRecordDictionary(String record)
    {
        return spec.getRecordDictionary(record);
    }


}
