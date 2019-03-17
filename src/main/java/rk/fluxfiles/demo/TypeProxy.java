package rk.fluxfiles.demo;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.fluxfiles.demo.types.TypeOne;
import rk.fluxfiles.demo.types.TypeThree;
import rk.fluxfiles.demo.types.TypeTwo;

@NoArgsConstructor
@Component
@Slf4j
public class TypeProxy
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
        spec.init();
    }
}
