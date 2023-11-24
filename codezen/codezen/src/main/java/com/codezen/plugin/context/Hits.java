package com.codezen.plugin.context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Hits {

    public static class MessageIDGen {
        private final Map<String, AtomicInteger> hits = new ConcurrentHashMap<>();

        public String next() {
            String d = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            AtomicInteger counter = hits.computeIfAbsent(d, k -> new AtomicInteger(0));
            int value = counter.incrementAndGet();
            return d + "-" + value;
        }
    }
}
