package fcomp.application.configuration.dictionary;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@Slf4j
public class Dict
{
    private Map<String, Map<String, String>> dictionary;

    public void init(String path)
    {
        log.debug("initializing dictionary...");
        dictionary = DictParser.parse(path);
        log.debug("dict: " + dictionary.entrySet());
    }

    public void missingDict()
    {
        dictionary = null;
    }
}
