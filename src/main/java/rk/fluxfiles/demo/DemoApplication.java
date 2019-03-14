package rk.fluxfiles.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.BaseStream;

@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(Comp comparator, TypeConfig config, TypeProxy spec) {
        return (args) -> {
            final Runtime runtime = Runtime.getRuntime();

            spec.setType("TYPE_ONE");
            Path path1 = Paths.get("input/file1.txt.bak");
            Path path2 = Paths.get("input/file2.txt.bak");

            Flux<String> streamOne = fromPath(path1);
            Flux<String> streamTwo = fromPath(path2);

            streamOne
                    .zipWith(streamTwo)
                    .doOnNext(v -> comparator.compare(v.getT1(), v.getT2()))
                    .subscribe();
                    //.blockLast();

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

}
