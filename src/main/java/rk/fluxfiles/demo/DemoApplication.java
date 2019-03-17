package rk.fluxfiles.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    private String EXTENSION = "type1";
    private String STREAM_ONE_DIR = "input/in1/";
    private String STREAM_TWO_DIR = "input/in2/";
    private String CONFIG_DIR = "config/";
    private String TYPE = "TYPE1";

    @Bean
    public CommandLineRunner demo(Comparator comparator, TypeConfig config) {
        return (args) -> {

            config.initConfig(TYPE, CONFIG_DIR + TYPE + ".txt");

            Flux<String> streamOne = streamFromFiles(getFiles(STREAM_ONE_DIR));
            Flux<String> streamTwo = streamFromFiles(getFiles(STREAM_TWO_DIR));

            streamOne.zipWith(streamTwo)
                    .takeWhile(v -> (!v.getT1().equals("") || !v.getT2().equals("")))
                    .doOnNext(v -> comparator.compare(v.getT1(), v.getT2()))
                    .subscribe();

            log.info("stream one: " + comparator.getStreamOne().entrySet().size());
            log.info("stream two: " + comparator.getStreamTwo().entrySet().size());
            log.info("not matched: " + comparator.getNotFullyMatched().size());
        };
    }


    private static Flux<String> fromPath(Path path) {
        return Flux.using(() -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close
        );
    }

    private File[] getFiles(String dir)
    {
        File path = new File(dir);
        return path.listFiles((dir1, name) -> name.toLowerCase().endsWith(EXTENSION));
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
