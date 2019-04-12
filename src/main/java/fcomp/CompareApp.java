package fcomp;

import fcomp.application.Comparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class CompareApp {

    public static void main(String[] args)
    {
        SpringApplication.run(CompareApp.class, args);
    }

    @Bean
    public CommandLineRunner run(Comparator comparator)
    {
        return (args ->
        {
            List<String> types = Arrays.asList(args);
            if (types.size() != 0)
            {
                comparator.startProcessing(types);
            } else {
                comparator.startProcessing();
            }
        });
    }
}
