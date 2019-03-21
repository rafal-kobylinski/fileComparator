package rk.fluxfiles.demo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rk.fluxfiles.demo.utils.DictParser;

import java.util.Map;

@Component
@Data
@Slf4j
public class Dict
{
    private Map<String, Map<String, String>> dictionary;

    public void init(String path) {
        log.info("initializing dictionary...");
        dictionary = DictParser.parse(path);
        log.info("dict: " + dictionary.entrySet());
    }
}
