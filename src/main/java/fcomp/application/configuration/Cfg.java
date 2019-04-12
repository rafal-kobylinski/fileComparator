package fcomp.application.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
@Slf4j
@Data
public class Cfg {

    private String dictionaryDir;
    private String typesConfigDir;
    private String outputDir;
    private String in1Dir;
    private String in2Dir;
    private int in_report_examples;
    private Properties properties = new Properties();
    private long maxBuffersSize;

    public Cfg()
    {
        try {
            properties.load(new FileInputStream("comparator.properties"));
        } catch (IOException e) {
            log.error("Failed to read comparator.properties file ");
        }

        dictionaryDir = properties.getProperty("dictionary_dir");
        typesConfigDir = properties.getProperty("types_config");
        outputDir = properties.getProperty("output_dir");
        in1Dir = properties.getProperty("in1_dir");
        in2Dir = properties.getProperty("in2_dir");
        in_report_examples = Integer.valueOf(properties.getProperty("in_report_examples"));
        maxBuffersSize = Long.valueOf(properties.getProperty("max_in_buffers"));
    }
}
