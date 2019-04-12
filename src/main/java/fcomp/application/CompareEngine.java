package fcomp.application;

import fcomp.application.configuration.Cfg;
import fcomp.application.types.TypeProxy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fcomp.application.errors.BuffersOverflow;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@Slf4j
@AllArgsConstructor
public class CompareEngine {

    private TypeProxy spec;
    private Cfg cfg;

    private int counter1 = 0;
    private int counter2 = 0;
    private Map<String, List<String>> streamOne = new HashMap<>();
    private Map<String, List<String>> streamTwo = new HashMap<>();
    private ArrayList<String[]> notFullyMatched = new ArrayList<>();


    @Autowired
    public CompareEngine(TypeProxy spec, Cfg cfg)
    {
        this.spec = spec;
        this.cfg = cfg;
    }

    private Boolean compareRecordsByKey2(String record1, String record2)
    {
        String record1Key2 = spec.getKey2(record1);
        String record2Key2 = spec.getKey2(record2);
        return compareKeys(record1Key2, record2Key2);
    }

    public void compare(String record1, String record2) {

        if (streamOne.size() + streamTwo.size() > cfg.getMaxBuffersSize())
        {
            throw new BuffersOverflow("Not compared threshold reached. Buffer1: " + streamOne.size() + ", buffer2: " + streamTwo.size() + ". Fix your data");
        }

        String record1Key = null;
        String record2Key = null;

        if (!record1.equals(""))
        {
            counter1 += 1;
            record1Key = spec.getKey(record1);
        }
        if (!record2.equals("")) {
            counter2 += 1;
            record2Key = spec.getKey(record2);
        }

        if (!record1.equals("") && !record2.equals(""))
        {
            if (compareKeys(record1Key, record2Key))
            {
                if (!compareRecordsByKey2(record1, record2))
                {
                    addValue(record1Key, record1, streamOne);
                    addValue(record2Key, record2, streamTwo);
                }
                return;
            }
        }

        if (!record1.equals("")) {
            compareWithOtherStream(record1, record1Key, streamOne, streamTwo);
        }
        if (!record2.equals("")) {
            compareWithOtherStream(record2, record2Key, streamTwo, streamOne);
        }
    }

    private void compareWithOtherStream(String record, String key1, Map<String,List<String>> recordstream, Map<String,List<String>> otherStream)
    {
        if (otherStream.containsKey(key1))
        {
            String otherRecord = otherStream.get(key1).get(0);
            removeValue(key1, otherRecord, otherStream);

            if (!compareRecordsByKey2(record, otherRecord))
            {
                notFullyMatched.add(new String[]{record, otherRecord});
            }
        }
        else {
            addValue(key1, record, recordstream);
        }
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

    public void compare(Tuple2<String, String> records) {
        compare(records.getT1(), records.getT2());
    }
}
