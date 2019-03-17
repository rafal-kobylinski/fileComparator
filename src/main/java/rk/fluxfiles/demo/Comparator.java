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
public class Comparator {
    //TODO refactor

    @Autowired
    private TypeProxy spec;

    private Map<String, String> streamOne = new HashMap<>();
    private Map<String, String> streamTwo = new HashMap<>();
    private ArrayList<String[]> notFullyMatched = new ArrayList<>();

    public void compare(String one, String two) {
        log.info("comparing:\n" + one + "\n" + two);
        String streamOneKey = null;
        String streamTwoKey = null;

        if (!one.equals(""))
        {
            streamOneKey = spec.getKey(one);
        }
        if (!two.equals("")) {
            streamTwoKey = spec.getKey(two);
        }

        if (!one.equals("") && !two.equals(""))
        {
            if (compareKeys(streamOneKey, streamTwoKey))
            {
                log.info("records equal on key1");
                String streamOneKey2 = spec.getKey2(one);
                String streamTwoKey2 = spec.getKey2(two);
                if (compareKeys(streamOneKey2, streamTwoKey2))
                {
                    log.info("records equal on key2");
                    return;
                } else {
                    log.info("records different on key2");
                    notFullyMatched.add(new String[]{one, two});
                    return;
                }
            } else {
                log.info("records different, comparing individually");
            }
        }

        if (!one.equals("")) {
            processKey(one, streamOneKey, streamOne, streamTwo);
        }
        if (!two.equals("")) {
            processKey(two, streamTwoKey, streamTwo, streamOne);
        }
    }

    private void processKey(String record, String key1, Map<String, String> recordstream, Map<String , String> otherStream)
    {
        log.info("processing record " + record);

        String otherRecord;

        if (checkKey1VsOtheStream(key1, otherStream))
        {
            log.info("match on key1 with other stream");
            String streamOneKey2 = spec.getKey2(record);
            otherRecord = otherStream.get(key1);
            String otherRecordKey1 = spec.getKey(otherRecord);
            String otherRecordKey2 = spec.getKey2(otherStream.get(otherRecordKey1));

            if (compareKeys(streamOneKey2, otherRecordKey2))
            {
                log.info("match on key2");
                otherStream.remove(key1);
            } else {
                log.info("not matching on key2");
                otherStream.remove(key1);
                notFullyMatched.add(new String[]{record, otherRecord});
            }
        }
        else {
            log.info("no match on key1 with other stream");
            recordstream.put(key1, record);
        }
    }

    private Boolean checkKey1VsOtheStream(String key, Map<String, String> otherStream)
    {
        return otherStream.containsKey(key);
    }

    private Boolean compareKeys(String key, String otherKey)
    {
        return key.equals(otherKey);
    }
}
