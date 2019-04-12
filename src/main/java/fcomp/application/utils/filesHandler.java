package fcomp.application.utils;

import org.apache.commons.io.FileUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class filesHandler {

    public static List<String> getExtensionsFromDir(String dir)
    {
        File path = new File(dir);
        return Stream.of(path.listFiles())
                .filter(File::isFile)
                .map(file -> file.getName())
                .map(name -> name.substring(name.lastIndexOf(".") + 1))
                .distinct()
                .collect(Collectors.toList());
    }

    public static Flux<String> reactLoadFiles(String type, String dir)
    {
        File[] files = getFileNames(type, dir);
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

    private static Flux<String> fromPath(Path path) {
        return Flux.using(() -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close
        );
    }

    private static File[] getFileNames(String extension, String dir)
    {
        File path = new File(dir);
        return path.listFiles((dir1, name) -> name.endsWith("." + extension));
    }

    public static void cleanDir(String dir)
    {
        try {
            FileUtils.cleanDirectory(new File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
