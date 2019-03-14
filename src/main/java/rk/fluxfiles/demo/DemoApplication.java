package rk.fluxfiles.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

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
    public CommandLineRunner demo() {
        return (args) -> {

            Path path1 = Paths.get("/home/kobik/workz/flux/src/main/resources/file1.txt");
            Path path2 = Paths.get("/home/kobik/workz/flux/src/main/resources/file2.txt");

            Flux<String> books = fluxVersion(path1);
            Flux<String> books2 = fluxVersion(path2);
            Comp.loadProperties();

            books
                    .zipWith(books2)
                    .doOnNext(v -> Comp.compare(v.getT1(), v.getT2()))
                    .blockLast();

            //.toStream()
            //.forEach(System.out::println);

            //books.doOnNext(System.out::println)
            //       .blockLast();
            log.info(Comp.output());
        };
    }


    private static Flux<String> fluxVersion(Path path) {
        return fromPath(path);
    }

    private static Flux<String> fromPath(Path path) {
        return Flux.using(() -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close
        );
    }

}
