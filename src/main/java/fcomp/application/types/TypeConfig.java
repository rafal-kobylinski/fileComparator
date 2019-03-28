package fcomp.application.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.utils.Cfg;
import fcomp.application.utils.Dict;
import fcomp.application.errors.PropertyNotFoundException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@NoArgsConstructor
@Component
@Slf4j
@Data
public class TypeConfig {

    private Properties properties = new Properties();

    @Autowired
    private TypeProxy typeProxy;
    @Autowired
    private Dict dict;
    @Autowired
    private Cfg cfg;

    public void initConfig(String directory, String file)
    {
        try {
            properties.load(new FileInputStream(directory + file));
        } catch (IOException e) {
            log.error("Failed to read properties file " + directory + file);
        }

        String dictConfigFile = properties.getProperty("dictionary");
        if (dictConfigFile == null){
                throw new PropertyNotFoundException("'dictionary' property not found in " + directory + file);
        }
        if (!dictConfigFile.equals("null")) {
            dict.init(cfg.getDictionaryDir() + dictConfigFile);
        } else {
            dict.missingDict();
        }

        String type = properties.getProperty("type");
        log.debug("Initializing config for type " + type + ", using file " + directory + file);
        log.debug("key1: " + getKeys1());
        log.debug("key2: " + getKeys2());

        setProxy(type);
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
