package rk.fluxfiles.demo;

import org.springframework.stereotype.Component;
import rk.fluxfiles.demo.types.TypeOne;

@Component
public class TypeProxy implements Spec {

    private Spec spec;

    public void setType(String type)
    {
        switch (type)
        {
            case "TYPE_ONE":
                spec = new TypeOne();
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
}
