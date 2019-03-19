package rk.fluxfiles.demo;

import java.util.List;

public interface Spec {

    String getKey(String record);
    String getKey2(String record);
    List<String[]> getRecordToKey2(String record);
    void init();
}
