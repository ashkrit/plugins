package com.codezen.plugin.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class CodeAction {
    public String actionName;
    public String projectName;
    public String basePath;
    public String codeFile;
    public String codeBlock;

    public CodeAction(String action, Project project, VirtualFile file, String codeBlock) {
        this.actionName = action;
        this.projectName = project.getName();
        this.basePath = project.getBasePath();
        this.codeBlock = codeBlock;
        this.codeFile = file.getPath();
    }
}
