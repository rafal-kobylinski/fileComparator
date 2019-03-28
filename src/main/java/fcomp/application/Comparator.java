package fcomp.application;

import fcomp.application.configuration.Cfg;
import fcomp.application.types.TypeProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class Comparator {


    @Autowired
    private CompareEngine compareEngine;
    @Autowired
    private TypeProperties config;
    @Autowired
    private Cfg cfg;

    public void startProcessing()
    {
        List<String> types = getExtensionsFromDir(cfg.getIn1Dir());
        startProcessing(types);
    }

    public void startProcessing(List<String> types)
    {
        try {
            FileUtils.cleanDirectory(new File(cfg.getOutputDir()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        types
                .stream()
                .filter(this::checkIfConfigExists)
                .forEach(type -> {
                    log.info("-----------------------------------------------------------");
                    log.info("Processing stream " + type);
                    config.initConfig(cfg.getTypesConfigDir(), type + ".txt");
                    execute(type);
                });
    }

    private void execute(String type)
    {
        Flux<String> streamOne = reactLoadFiles(getFileNames(type, cfg.getIn1Dir()));
        Flux<String> streamTwo = reactLoadFiles(getFileNames(type, cfg.getIn2Dir()));

        streamOne.zipWith(streamTwo)
                .takeWhile(v -> (!v.getT1().equals("") || !v.getT2().equals("")))
                .doOnNext(v -> compareEngine.compare(v.getT1(), v.getT2()))
                .doOnError(e -> log.error(
                        "\nError!\n"
                        + e.getLocalizedMessage() + "\n"
                        + Arrays.asList(e.getStackTrace())
                                .stream()
                                .map(v -> v.toString())
                                .filter(v -> v.startsWith("fcomp"))
                                .limit(5)
                                .map(v -> "\t" + v)
                                .collect(Collectors.joining("\n"))
                        + "\nbreaking " + type + " execution"))
                .doOnComplete(() -> writeOutput(type))
                .doFinally(v -> compareEngine.cleanUp())
                .subscribe();
    }

    private void writeOutput(String type)
    {
        log.info("Stream1 had " + compareEngine.getCounter1() + " records");
        log.info("Stream2 had " + compareEngine.getCounter2() + " records");
        log.info("not matched from stream1: " + compareEngine.getStreamOne().size());
        log.info("not matched from stream2: " + compareEngine.getStreamTwo().size());
        log.info("not fully matched: " + compareEngine.getNotFullyMatched().size());

        List<String> contentNotFully = compareEngine
                .getNotFullyMatched()
                .stream()
                .map(this::formatDiff)
                .limit(cfg.getIn_report_examples())
                .collect(Collectors.toList());

        try {
            if (compareEngine.getStreamOne().size() !=0)
                Files.write(
                        Paths.get(cfg.getOutputDir() + type + ".notMatched1"),
                        compareEngine.getStreamOne().values().stream().flatMap(List::stream).collect(Collectors.toList()));

            if (compareEngine.getStreamTwo().size() !=0)
                Files.write(
                        Paths.get(cfg.getOutputDir() + type + ".notMatched2"),
                        compareEngine.getStreamTwo().values().stream().flatMap(List::stream).collect(Collectors.toList()));

            if (compareEngine.getNotFullyMatched().size() !=0) Files.write(Paths.get(cfg.getOutputDir() + type + ".notFullyMatched"), contentNotFully);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean checkIfConfigExists(String type)
    {
        File path = new File(cfg.getTypesConfigDir() + type + ".txt");
        Boolean exist = path.exists();
        if (!exist)
        {
            log.info("Config file for type: " + type + " does not exists, skipping");
        }
        return exist;
    }

    private Flux<String> fromPath(Path path) {
        return Flux.using(() -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close
        );
    }

    private List<String> getExtensionsFromDir(String dir)
    {
        File path = new File(dir);
        return Stream.of(path.listFiles())
                .filter(File::isFile)
                .map(file -> file.getName())
                .map(name -> name.substring(name.lastIndexOf(".") + 1))
                .distinct()
                .collect(Collectors.toList());
    }

    private File[] getFileNames(String extension, String dir)
    {
        File path = new File(dir);
        return path.listFiles((dir1, name) -> name.endsWith("." + extension));
    }


    private Flux<String> reactLoadFiles(File[] files)
    {
        Flux<String> output = Flux.empty();

        for (File  file : files) {
            Flux<String> stream = fromPath(file.toPath());
            output = output.concatWith(stream);
        }

        //TODO fix below workaround
        Flux<String> infiniteEmptyStream = Flux.fromStream(Stream.iterate("", i -> i).map(v -> String.valueOf(v)));
        output = output.concatWith(infiniteEmptyStream);

        return output;
    }

    private String formatDiff(String[] lines)
    {
        return compareEngine.getSpec().createComparisonReport(lines[0], lines[1]);
    }
}
