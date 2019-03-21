package rk.fluxfiles.demo;

import java.util.List;
import java.util.Map;

public interface Spec {

    String getKey(String record);
    String getKey2(String record);
    //String createComparisonReport(String record1, String record2);
    void init();

    Boolean checkIfInKeys(String index);
    String getField(String index, String record);
    Map<String, String> getFieldsMapping(String record1);
}
