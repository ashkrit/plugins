package com.codezen.plugin.model;

import java.util.Map;

public class CodeMarker {

    public Map<String, CodeBlock> fileTypes;

    public static class CodeBlock {
        public final String start;
        public final String end;

        public CodeBlock(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }
}
