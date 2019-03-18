package rk.fluxfiles.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
public class DemoApplication {
    private String INPUT_DIR = "D:/projekty/compareTool/fromCM1/fcomp/";
    private String STREAM_ONE_DIR = "in1/";
    private String STREAM_TWO_DIR = "in2/";
    private String CONFIG_DIR = "config/";

    @Autowired
    Comparator comparator;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner demo(TypeConfig config) {
        return (args) ->
                getTypesFromDir(INPUT_DIR + "/" + STREAM_ONE_DIR)
                .stream()
                .filter(this::checkIfConfigExists)
                .forEach(type -> {
                    log.info("Processing stream " + type);
                    config.initConfig(type, CONFIG_DIR + type + ".txt");
                    execute(type);
                });
    }*/

    private void execute(String type)
    {

        Flux<String> streamOne = streamFromFiles(getFiles(type, INPUT_DIR + "/" + STREAM_ONE_DIR));
        Flux<String> streamTwo = streamFromFiles(getFiles(type, INPUT_DIR + "/" + STREAM_TWO_DIR));

        streamOne.zipWith(streamTwo)
                .takeWhile(v -> (!v.getT1().equals("") || !v.getT2().equals("")))
                .doOnNext(v -> comparator.compare(v.getT1(), v.getT2()))
                .subscribe();

        log.info("Stream1 had " + comparator.getCounter1() + " records");
        log.info("Stream2 had " + comparator.getCounter2() + " records");
        log.info("stream one: " + comparator.getStreamOne().entrySet().size());
        log.info("stream two: " + comparator.getStreamTwo().entrySet().size());
        log.info("not matched: " + comparator.getNotFullyMatched().size());

        //comparator.getStreamOne().entrySet().forEach(System.out::println);
    }

    private Boolean checkIfConfigExists(String type)
    {
        File path = new File(CONFIG_DIR + type + ".txt");
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

    private List<String> getTypesFromDir(String dir)
    {
        File path = new File(dir);
        return Stream.of(path.listFiles())
                .filter(File::isFile)
                .map(file -> file.getName())
                .map(name -> name.substring(name.lastIndexOf(".") + 1))
                .distinct()
                .collect(Collectors.toList());
    }

    private File[] getFiles(String type, String dir)
    {
        File path = new File(dir);
        return path.listFiles((dir1, name) -> name.endsWith(type));
    }


    private Flux<String> streamFromFiles(File[] files)
    {
        Flux<String> joinedStream = Flux.empty();

        for (File  file : files) {
            Flux<String> stream = fromPath(file.toPath());
            joinedStream = joinedStream.concatWith(stream);
        }

        //TODO fix below workaround
        Stream<String> infiniteStream = Stream.iterate("", i -> i);
        Flux<String> dummy = Flux.fromStream(infiniteStream).map(v -> String.valueOf(v));
        joinedStream = joinedStream.concatWith(dummy);

        return joinedStream;
    }
}
