package com.codezen.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markers {

    private final Pattern pattern = Pattern.compile("\\.(\\w+)$");

    private final Map<String, String[]> commentMarker = new HashMap<>();

    public Markers() {
        commentMarker.put("java", new String[]{"// Code Start", "// Code end"});
        commentMarker.put("bat", new String[]{"@rem Code Start", "@rem Code end"});
        commentMarker.put("xml", new String[]{"<!-- Code Start -->", "<!-- Code end -->"});
        commentMarker.put("sh", new String[]{"## Code Start ", "## Code end "});
        commentMarker.put("py", new String[]{"## Code Start ", "## Code end "});
    }


    public String begin(String file) {
        String extension = fileType(file);

        String[] value = commentMarker.getOrDefault(extension, new String[]{"?? Code Start", "?? Code End"});
        return value[0];
    }

    private String fileType(String file) {
        String name = new File(file).getName();
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "default";
        }
    }

    public String end(String file) {
        String extension = fileType(file);

        String[] value = commentMarker.getOrDefault(extension, new String[]{"?? Code Start", "?? Code End"});
        return value[1];
    }
}
