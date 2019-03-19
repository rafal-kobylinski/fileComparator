package rk.fluxfiles.demo;

import java.util.List;

public interface Spec {

    String getKey(String record);
    String getKey2(String record);
    String createComparisonReport(String[] records);
    void init();
}
