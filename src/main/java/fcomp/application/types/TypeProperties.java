package fcomp.application.types;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.configuration.Cfg;
import fcomp.application.configuration.dictionary.Dict;
import fcomp.application.errors.PropertyNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class TypeProperties {

    private Properties properties = new Properties();

    private TypeProxy typeProxy;
    private Dict dict;
    private Cfg cfg;

    @Autowired
    public TypeProperties(TypeProxy typeProxy, Dict dict, Cfg cfg) {
        this.typeProxy = typeProxy;
        this.dict = dict;
        this.cfg = cfg;
    }

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

    public String getDelimiter()
    {
        return  properties.getProperty("delimiter");
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

    public Boolean checkIfConfigExists(String type)
    {
        File path = new File(cfg.getTypesConfigDir() + type + ".txt");
        Boolean exist = path.exists();
        if (!exist)
        {
            log.info("Config file for type: " + type + " does not exists, skipping");
        }
        return exist;
    }
}
