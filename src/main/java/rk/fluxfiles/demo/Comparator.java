package rk.fluxfiles.demo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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

    private int counter1 = 0;
    private int counter2 = 0;
    private Multimap<String, String> streamOne = ArrayListMultimap.create();
    private Multimap<String, String> streamTwo = ArrayListMultimap.create();
    private ArrayList<String[]> notFullyMatched = new ArrayList<>();

    public void compare(String one, String two) {
        log.debug("comparing:\n" + one + "\n" + two);
        String streamOneKey = null;
        String streamTwoKey = null;

        if (!one.equals(""))
        {
            counter1 += 1;
            streamOneKey = spec.getKey(one);
        }
        if (!two.equals("")) {
            counter2 += 1;
            streamTwoKey = spec.getKey(two);
        }

        if (!one.equals("") && !two.equals(""))
        {
            if (compareKeys(streamOneKey, streamTwoKey))
            {
                log.debug("records equal on key1");
                String streamOneKey2 = spec.getKey2(one);
                String streamTwoKey2 = spec.getKey2(two);
                if (compareKeys(streamOneKey2, streamTwoKey2))
                {
                    log.debug("records equal on key2");
                    return;
                } else {
                    log.debug("records different on key2");
                    notFullyMatched.add(new String[]{one, two});
                    return;
                }
            } else {
                log.debug("records different, comparing individually");
            }
        }

        if (!one.equals("")) {
            processKey(one, streamOneKey, streamOne, streamTwo);
        }
        if (!two.equals("")) {
            processKey(two, streamTwoKey, streamTwo, streamOne);
        }
    }

    private void processKey(String record, String key1, Multimap<String,String> recordstream, Multimap<String,String> otherStream)
    {
        log.debug("processing record " + record);

        if (checkKey1VsOtheStream(key1, otherStream))
        {
            log.debug("match on key1 with other stream");
            String streamOneKey2 = spec.getKey2(record);

            String otherRecord = otherStream.get(key1).iterator().next();
            String otherRecordKey1 = spec.getKey(otherRecord);
            String otherRecordKey2 = spec.getKey2(otherStream.get(otherRecordKey1).iterator().next());

            otherStream.remove(key1, otherRecord);

            if (compareKeys(streamOneKey2, otherRecordKey2))
            {
                log.debug("match on key2");
            } else {
                log.debug("not matching on key2");
                notFullyMatched.add(new String[]{record, otherRecord});
            }
        }
        else {
            log.debug("no match on key1 with other stream");
            recordstream.put(key1, record);
        }
    }

    private Boolean checkKey1VsOtheStream(String key, Multimap<String,String> otherStream)
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
}
