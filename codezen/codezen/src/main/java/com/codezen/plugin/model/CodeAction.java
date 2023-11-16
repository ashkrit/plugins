package com.codezen.plugin.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashMap;
import java.util.Map;

public class CodeAction {
    private String codeBlock;
    public String user;
    public String actionName;
    public String projectName;
    public String basePath;
    public String codeFile;

    public Map<String, Object> params = new HashMap<>();

    public CodeAction(String user, String action, Project project, VirtualFile file, String codeBlock) {
        this.user = user;
        this.actionName = action;
        this.projectName = project.getName();
        this.basePath = project.getBasePath();
        this.codeBlock = codeBlock;
        this.codeFile = file.getPath();
    }
}
