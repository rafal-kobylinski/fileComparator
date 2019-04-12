package fcomp.application.raports;

import fcomp.application.CompareEngine;
import fcomp.application.configuration.Cfg;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class NotFullyCompleted {

    CompareEngine compareEngine;
    Cfg cfg;
    RecordsComparison recordsComparison;

    public void writeOutput(String type)
    {
        log.info("Stream1 had " + compareEngine.getCounter1() + " records");
        log.info("Stream2 had " + compareEngine.getCounter2() + " records");
        log.info("not matched from stream1: " + compareEngine.getStreamOne().size());
        log.info("not matched from stream2: " + compareEngine.getStreamTwo().size());
        log.info("not fully matched: " + compareEngine.getNotFullyMatched().size());

        List<String> contentNotFully = createComparison();

        try {
            if (compareEngine.getStreamOne().size() !=0)
                Files.write(
                        Paths.get(cfg.getOutputDir() + type + ".notMatched1"),
                        compareEngine.getStreamOne().values().stream().flatMap(List::stream).collect(Collectors.toList()));

            if (compareEngine.getStreamTwo().size() !=0)
                Files.write(
                        Paths.get(cfg.getOutputDir() + type + ".notMatched2"),
                        compareEngine.getStreamTwo().values().stream().flatMap(List::stream).collect(Collectors.toList()));

            if (compareEngine.getNotFullyMatched().size() !=0)
                Files.write(Paths.get(cfg.getOutputDir() + type + ".notFullyMatched"), contentNotFully);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> createComparison()
    {
        return compareEngine
                .getNotFullyMatched()
                .stream()
                .map(this::formatDiff)
                .limit(cfg.getIn_report_examples())
                .collect(Collectors.toList());
    }

    private String formatDiff(String[] lines)
    {
        return recordsComparison.compareRecords(lines[0], lines[1]);
    }
}
