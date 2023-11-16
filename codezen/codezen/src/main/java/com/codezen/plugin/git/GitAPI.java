package com.codezen.plugin.git;

import java.util.Map;
import java.util.function.Consumer;

public interface GitAPI {

    Map<String, Object> context(String cmd);
}
