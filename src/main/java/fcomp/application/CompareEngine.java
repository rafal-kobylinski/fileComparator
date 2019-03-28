package fcomp.application;

import fcomp.application.configuration.Cfg;
import fcomp.application.types.TypeProxy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.errors.BuffersOverflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@Slf4j
public class CompareEngine {

    @Autowired
    private TypeProxy spec;
    @Autowired
    private Cfg cfg;

    private int counter1 = 0;
    private int counter2 = 0;
    private Map<String, List<String>> streamOne = new HashMap<>();
    private Map<String, List<String>> streamTwo = new HashMap<>();
    private ArrayList<String[]> notFullyMatched = new ArrayList<>();

    public void compare(String record1, String record2) {
        log.trace("comparing:\n" + record1 + "\n" + record2);

        if (streamOne.size() + streamTwo.size() > cfg.getMaxBuffersSize())
        {
            throw new BuffersOverflow("Not compared threshold reached. Buffer1: " + streamOne.size() + ", buffer2: " + streamTwo.size() + ". Fix your data");
        }

        String key1 = null;
        String key2 = null;

        if (!record1.equals(""))
        {
            counter1 += 1;
            key1 = spec.getKey(record1);
        }
        if (!record2.equals("")) {
            counter2 += 1;
            key2 = spec.getKey(record2);
        }

        if (!record1.equals("") && !record2.equals(""))
        {
            if (compareKeys(key1, key2))
            {
                log.trace("records equal on key1");
                String streamOneKey2 = spec.getKey2(record1);
                String streamTwoKey2 = spec.getKey2(record2);
                if (compareKeys(streamOneKey2, streamTwoKey2))
                {
                    log.trace("records equal on key2");
                    return;
                } else {
                    log.trace("records different on key2\n" + key1 + "\n" + key2);
                    notFullyMatched.add(new String[]{record1, record2});
                    return;
                }
            } else {
                log.trace("records different, comparing individually");
            }
        }

        if (!record1.equals("")) {
            compareWithOtherStream(record1, key1, streamOne, streamTwo);
        }
        if (!record2.equals("")) {
            compareWithOtherStream(record2, key2, streamTwo, streamOne);
        }
    }

    private void compareWithOtherStream(String record, String key1, Map<String,List<String>> recordstream, Map<String,List<String>> otherStream)
    {
        log.trace("processing record " + record);

        if (checkKey1VsOtheStream(key1, otherStream))
        {
            log.trace("match on key1 with other stream");
            String streamOneKey2 = spec.getKey2(record);

            String otherRecord = otherStream.get(key1).get(0);
            String otherRecordKey1 = spec.getKey(otherRecord);
            String otherRecordKey2 = spec.getKey2(otherStream.get(otherRecordKey1).get(0));

            removeValue(key1, otherRecord, otherStream);

            if (compareKeys(streamOneKey2, otherRecordKey2))
            {
                log.trace("match on key2");
            } else {
                log.trace("not matching on key2\n" + streamOneKey2 + "\n" + otherRecordKey2);
                notFullyMatched.add(new String[]{record, otherRecord});
            }
        }
        else {
            log.trace("no match on key1 with other stream");
            addValue(key1, record, recordstream);

        }
    }

    private Boolean checkKey1VsOtheStream(String key, Map<String,List<String>> otherStream)
    {
        return otherStream.containsKey(key);
    }

    private Boolean compareKeys(String key, String otherKey)
    {
        return key.equals(otherKey);
    }

    public void cleanUp()
    {
        streamOne.clear();
        streamTwo.clear();
        counter1 = 0;
        counter2 = 0;
        notFullyMatched.clear();
    }

    private void addValue(String key, String value, Map<String, List<String>> map)
    {
        map.computeIfAbsent(key, (k) -> new ArrayList<>()).add(value);
    }

    private void removeValue(String key, String value, Map<String, List<String>> map)
    {
        map.computeIfPresent(key, (k, l) -> l.remove(value) && l.isEmpty() ? null : l);
    }
}
