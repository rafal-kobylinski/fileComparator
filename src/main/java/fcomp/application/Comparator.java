package fcomp.application;

import fcomp.application.configuration.Cfg;
import fcomp.application.raports.NotFullyCompleted;
import fcomp.application.types.TypeProperties;
import fcomp.application.utils.filesHandler;
import lombok.AllArgsConstructor;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@AllArgsConstructor
public class Comparator
{
    private CompareEngine compareEngine;
    private TypeProperties config;
    private Cfg cfg;
    private NotFullyCompleted notFullyCompleted;

    public void startProcessing()
    {
        List<String> types = filesHandler.getExtensionsFromDir(cfg.getIn1Dir());
        startProcessing(types);
    }

    public void startProcessing(List<String> types)
    {
        filesHandler.cleanDir(cfg.getOutputDir());

        types
                .stream()
                .filter(config::checkIfConfigExists)
                .forEach(type -> {
                    log.info("-----------------------------------------------------------");
                    log.info("Processing stream " + type);
                    config.initConfig(cfg.getTypesConfigDir(), type + ".txt");
                    long startTime = System.nanoTime();
                    execute(type);
                    long endTime = System.nanoTime();
                    log.debug("Execution time: " + TimeUnit.NANOSECONDS.toSeconds(endTime - startTime) + " sec");
                });
    }

    private void execute(String type)
    {
        Flux<String> streamOne = filesHandler.reactLoadFiles(type, cfg.getIn1Dir());
        Flux<String> streamTwo = filesHandler.reactLoadFiles(type, cfg.getIn2Dir());

        streamOne.zipWith(streamTwo)
                .takeWhile(v -> (!v.getT1().equals("") || !v.getT2().equals("")))
                .doOnNext(compareEngine::compare)
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
                .doOnComplete(() -> notFullyCompleted.writeOutput(type))
                .doFinally(v -> compareEngine.cleanUp())
                .subscribe();
    }
}
