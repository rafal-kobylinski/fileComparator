package rk.fluxfiles.demo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.fluxfiles.demo.types.TypeOne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@Slf4j
public class Comp {

    @Autowired
    private TypeProxy spec;

    private Map<String, String> streamOne = new HashMap<>();
    private Map<String, String> streamTwo = new HashMap<>();
    private ArrayList<String[]> notFullyMatched = new ArrayList<>();

    public void compare(String one, String two)
    {
        log.info("Starting comparing:\n" + one + "\n" + two);
        String streamOneKey = spec.getKey(one);
        String streamOneKey2 = spec.getKey2(one);

        String streamTwoKey = spec.getKey(two);
        String streamTwoKey2 = spec.getKey2(two);

        if (streamOneKey.equals(streamTwoKey))
        {
            log.debug("records are equal on key1");
            if (!streamOneKey2.equals(streamTwoKey2))
            {
                log.debug("records key2 not matching");
                String[] notMatched = {one, two};
                notFullyMatched.add(notMatched);
                return;
            }
        }

        log.debug("checking stream1 with buffer2");
        if (streamTwo.containsKey(streamOneKey))
        {
            log.debug("buffer2 contains key1");
            String key2ToBeChecked = spec.getKey2(streamTwo.get(streamOneKey));
            if (key2ToBeChecked.equals(streamOneKey2))
            {
                log.debug("key2 equals");
                streamTwo.remove(streamOneKey);
            }
            else {
                log.debug("keys are not the same");
                String[] notMatched = {one, streamTwo.get(streamOneKey)};
                notFullyMatched.add(notMatched);
                streamTwo.remove(streamOneKey);
            }
        } else {
            log.debug("nothing matches, adding record to buffer1");
            streamOne.put(streamOneKey, one);
        }

        log.debug("checking stream2 with buffer1");
        if (streamOne.containsKey(streamTwoKey))
        {
            log.debug("buffer1 contains key1");
            String key2ToBeChecked = spec.getKey2(streamOne.get(streamTwoKey));
            if (key2ToBeChecked.equals(streamTwoKey2))
            {
                log.debug("key2 equals");
                streamOne.remove(streamTwoKey);
            }
            else {
                log.debug("keys are not the same");
                String[] notMatched = {one, streamOne.get(streamTwoKey)};
                notFullyMatched.add(notMatched);
                streamOne.remove(streamTwoKey);
            }
        } else {
            log.debug("nothing matches, adding record to buffer2");
            streamTwo.put(streamTwoKey, two);
        }
    }


}
