package rk.fluxfiles.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
@Slf4j
@Data
public class TypeConfig {

    private Properties properties = new Properties();
    private String type;
    private String file;

    @Autowired
    private TypeProxy typeProxy;
    @Autowired
    private Dict dict;

    public void initConfig(String type, String directory, String file)
    {
        this.type = type;
        this.file = file;

        try {
            properties.load(new FileInputStream(directory + file));
        } catch (IOException e) {
            log.error("Failed to read properties file " + directory + file);
        }

        String dictConfigFile = properties.getProperty("dictionary");
        dict.init(dictConfigFile);

        this.type = properties.getProperty("type");
        log.info("Initializing config for type " + this.type + ", using file " + directory + file);
        log.info("key1: " + getKeys1());
        log.info("key2: " + getKeys2());

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

    public String getKeys1 ()
    {
        return properties.getProperty("key1");
    }

    public String getKeys2 ()
    {
        return properties.getProperty("key2");
    }

    private void setProxy(String type)
    {
        typeProxy.setType(type);
    }

    public String getSubtypePosition() {
        return properties.getProperty("type_position");
    }
}
