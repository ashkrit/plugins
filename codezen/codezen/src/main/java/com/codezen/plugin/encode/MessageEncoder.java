package com.codezen.plugin.encode;

import com.intellij.openapi.diagnostic.Logger;

import java.util.Base64;

public class MessageEncoder {

    private static final Logger LOG = Logger.getInstance(MessageEncoder.class);

    public static String decode(String value) {
        try {
            if (value.startsWith("http")) {
                return value;
            }
            return new String(Base64.getDecoder().decode(value));
        } catch (IllegalArgumentException e) {
            LOG.error(e);
            return value;
        }
    }
}
