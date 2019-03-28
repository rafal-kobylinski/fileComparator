package fcomp.application.configuration.dictionary;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class DictParser {

    private static final String[] TYPE_REGEX = {"DELIMITER", "VARIANT_FORMATS", "FIXED_FORMAT"};

    public static Map<String, Map<String, String>> parse(String path)
    {
        String content = loadFile(path);
        switch (getType(content))
        {
            case "DELIMITER":
                return type1(content);
            case "FIXED_FORMAT":
                return type2(content);
            case "VARIANT_FORMATS":
                return type3(content);
            default:
                return null;
        }
    }

    public static String loadFile(String file)
    {
        File path = new File(file);

        log.debug(path.getAbsolutePath());
        try {
            return Files.lines(path.toPath()).collect(Collectors.joining(" "));
        } catch (IOException e) {
            log.error("dictionary file " + file + " not found");
            e.printStackTrace();
        }

        return null;
    }

    private static String getType(String file)
    {
        return Arrays
                .stream(TYPE_REGEX)
                .parallel()
                .filter(file::contains)
                .findFirst()
                .orElse("");
    }

    private static Map<String, Map<String, String>> type1(String content)
    {
        Map<String, String> map = new LinkedHashMap();
        Pattern pattern2 = Pattern.compile("qw\\((.+)\\)");
        Matcher matcher2 = pattern2.matcher(content);
        matcher2.find();
        List<String> list = Arrays.asList(matcher2.group(1).trim().split("[ ,;|]"));
        for (int i=0; i<list.size(); i++)
        {
            map.put(String.valueOf(i), list.get(i));
        }
        Map<String, Map<String, String>> output = new LinkedHashMap<>();
        output.put("00", map);
        return output;
    }

    private static Map<String, Map<String, String>> type2(String content)
    {
        Map<String, String> map = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile("\\[.*'(.+)\\'.*\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(content);
        matcher.find();

        String keys = matcher
                .group(1)
                .trim()
                .replaceAll("\\s+",",")
                .replaceAll("a","");

        String names = matcher
                .group(2)
                .replaceAll(","," ")
                .trim()
                .replaceAll("\\s+",",");

        List<String> keyList = Arrays.asList(keys.split("[ ,;|]"));
        List<String> namesList = Arrays.asList(names.split("[ ,;|]"));

        int start = 0;
        for (int i=0; i<keyList.size(); i++)
        {
            map.put(start + "-" + (start - 1 + Integer.valueOf(keyList.get(i))),namesList.get(i));
            start += Integer.valueOf(keyList.get(i));
        }

        Map<String, Map<String,String>> output = new LinkedHashMap<>();
        output.put("00", map);

        return output;
    }

    private static Map<String, Map<String, String>> type3(String content)
    {
        Pattern pattern = Pattern.compile("'([0-9]{2})' =>.*?\\[.*?'(.*?)',.*?\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(content);

        Map<String, Map<String, String>> output = new LinkedHashMap<>();

        while (matcher.find()) {
            String type = matcher.group(1);
            String keys = matcher
                    .group(2)
                    .trim()
                    .replaceAll("\\s+", ",")
                    .replaceAll("a", "");
            String names = matcher
                    .group(3)
                    .trim()
                    .replaceAll("'", "")
                    .replaceAll(",\\s+", " ")
                    .trim()
                    .replaceAll("\\s", ",");

            List<String> keysList = Arrays.asList(keys.split(","));
            List<String> namesList = Arrays.asList(names.split(","));

            Map<String, String> map = new LinkedHashMap<>();

            int start = 0;
            for (int i=0; i<keysList.size(); i++)
            {
                map.put(start + "-" + (start - 1 + Integer.valueOf(keysList.get(i))),namesList.get(i));
                start += Integer.valueOf(keysList.get(i));
            }

            output.put(type, map);
        }

        return output;
    }
}
