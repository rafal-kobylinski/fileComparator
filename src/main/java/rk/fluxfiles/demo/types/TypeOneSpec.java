package rk.fluxfiles.demo.types;

import org.springframework.stereotype.Service;
import rk.fluxfiles.demo.Spec;

@Service
public class TypeOneSpec implements Spec {


    @Override
    public String getKey(String record) {
        return null;
    }
}
