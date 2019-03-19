package rk.fluxfiles.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
@Component
@Slf4j
@Data
public class TypeConfig {

    private Properties properties = new Properties();
    private String type;
    private String file;
    private Map<String, String> dictionary = new TreeMap<>();

    @Autowired
    private TypeProxy typeProxy;

    public void initConfig(String type, String directory, String file)
    {
        this.type = type;
        this.file = file;

        try {
            properties.load(new FileInputStream(directory + file));
        } catch (IOException e) {
            log.error("Failed to read properties file " + directory + file);
        }

        try {
            loadDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.type = properties.getProperty("type");
        log.info("Initializing config for type " + this.type + ", using file " + directory + file);
        log.info("key1: " + Stream.of(getKeys1()).collect(Collectors.joining(",")));
        log.info("key2: " + Stream.of(getKeys2()).collect(Collectors.joining(",")));

        setProxy(this.type);


    }

    public String getType()
    {
        return type;
    }

    public String getDelimeter()
    {
        return  properties.getProperty("delimeter");
    }

    public String[] getKeys1 ()
    {
        return properties.getProperty("key1").split(",");
    }

    public String[] getKeys2 ()
    {
        return properties.getProperty("key2").split(",");
    }

    private void setProxy(String type)
    {
        typeProxy.setType(type);
    }

    public void loadDictionary() throws IOException {
        File path = new File("src/main/resources/dictionary/" + file);
        Files
                .lines(path.toPath())
                .map(line -> line.split("="))
                .forEach(v -> dictionary.put(v[0], v[1]));
    }

}
