package rk.fluxfiles.demo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testMultimap() {
        Multimap<String, String> map = ArrayListMultimap.create();
        map.put("key1", "value1");
        map.put("key1", "value1");
        System.out.println(map.get("key1"));
        map.remove("key1", "value1");
        System.out.println(map.get("key1"));
    }

}
