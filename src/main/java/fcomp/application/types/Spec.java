package fcomp.application.types;

import java.util.List;
import java.util.Map;

public interface Spec {

    String getKey(String record);
    String getKey2(String record);
    //String createComparisonReport(String record1, String record2);
    void init();

    Boolean checkIfInKeys(String index, String record);
    String getFieldValue(String index, String record);
    Map<String, String> getRecordDictionary(String record1);
}
