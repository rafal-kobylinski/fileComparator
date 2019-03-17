package rk.fluxfiles.demo;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
@Component
@Slf4j
public class TypeConfig {

    private Properties properties = new Properties();
    private String type;

    @Autowired
    private TypeProxy typeProxy;

    public void initConfig(String type, String path)
    {

        this.type = type;

        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            log.error("Failed to read properties file " + path);
        }

        this.type = properties.getProperty("type");
        log.info("Initializing config for type " + this.type + ", using file " + path);
        log.info("key1: " + Stream.of(getKeys1()).collect(Collectors.joining()));
        log.info("key2: " + Stream.of(getKeys2()).collect(Collectors.joining()));

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


}
