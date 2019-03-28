package fcomp.application.types;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
@Slf4j
public class TypeProxy
{

    private Spec spec;
    int howManyDiffs;
    StringBuilder diffs;

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

    public String createComparisonReport(String record1, String record2)
    {
        howManyDiffs = 0;
        diffs = new StringBuilder();

        Map<String, String> dict = spec.getRecordDictionary(record1);

        if (dict == null) return record1 + "\n" + record2 + "\n";

        Flux<List<String>> record1ReportFlux = Flux.fromIterable(dict.keySet())
                .map(i -> recordToReportInput(i, dict.get(i), record1));

        Flux<List<String>> record2ReportFlux = Flux.fromIterable(dict.keySet())
                .map(i -> recordToReportInput(i, dict.get(i), record2));

        String report = record1ReportFlux.zipWith(record2ReportFlux)
                .map(this::tupleToReportLine)
                .collect(Collectors.joining()).block();

        return record1 + "\n"
                + record2 + "\n"
                + "Differences: " + howManyDiffs + ", on position" + (howManyDiffs > 1 ? "s " : " ") + diffs + "\n"
                + report;
    }

    private String tupleToReportLine(Tuple2<List<String>, List<String>> tuple)
    {
        String name = tuple.getT1().get(0);
        String index = tuple.getT1().get(1);
        String value1 = tuple.getT1().get(2);
        String value2 = tuple.getT2().get(2);
        String info = tuple.getT1().get(3);
        if (info.equals("") && !value1.equals(value2))
        {
            howManyDiffs += 1;
            diffs.append(index + " ");
            info = "<<<---";
        }

        return String.format("|%50s|%10s|%20s|%20s|%2s\n", name, index, value1, value2, info);
    }


    private List<String> recordToReportInput(String index, String name, String record)
    {
        Boolean isKeyed = spec.checkIfInKeys(index, record);

        List<String> output = new ArrayList<>();
        output.add(name);
        output.add(index);
        output.add(spec.getFieldValue(index, record));
        output.add(isKeyed ? "" : "not a key");
        return output;
    }
}
