package com.codezen.plugin;

import com.codezen.plugin.io.MoreIO;
import com.codezen.plugin.model.CodeMarker;
import com.codezen.plugin.model.CodeMarker.CodeBlock;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markers {

    private final Pattern pattern = Pattern.compile("\\.(\\w+)$");

    private final Map<String, CodeBlock> commentMarker;

    public Markers() {


        Optional<String> data = MoreIO.readFromClassPath("/config/marker.json");

        commentMarker = data
                .map(d -> new Gson().fromJson(d, CodeMarker.class))
                .map(m -> m.fileTypes)
                .orElseGet(Markers::defaultRules);


    }

    @NotNull
    private static Map<String, CodeBlock> defaultRules() {
        Map<String, CodeBlock> commentMarker = new HashMap<>();
        commentMarker.put("java", new CodeBlock("// Code Start", "// Code end"));
        commentMarker.put("bat", new CodeBlock("@rem Code Start", "@rem Code end"));
        commentMarker.put("xml", new CodeBlock("<!-- Code Start -->", "<!-- Code end -->"));
        commentMarker.put("sh", new CodeBlock("## Code Start ", "## Code end "));
        commentMarker.put("py", new CodeBlock("## Code Start ", "## Code end "));
        return commentMarker;
    }


    public String begin(String file) {
        String extension = fileType(file);

        CodeBlock value = commentMarker.getOrDefault(extension, new CodeBlock("?? Code Start", "?? Code End"));
        return value.start;
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

        CodeBlock value = commentMarker.getOrDefault(extension, new CodeBlock("?? Code Start", "?? Code End"));
        return value.end;
    }

}
