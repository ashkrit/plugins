package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.git.CommandLineGiT;
import com.codezen.plugin.git.GitAPI;
import com.codezen.plugin.io.MoreIO;
import com.codezen.plugin.model.CodeAction;
import com.codezen.plugin.model.PluginConfig;
import com.codezen.plugin.model.Sink;
import com.codezen.plugin.sink.SinkConsumer;
import com.codezen.plugin.tag.CodeTagger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;

public class MarkStartedAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(MarkStartedAction.class);
    public static final String LINE_BREAK = "\n";
    public static final String ACTION_NAME = "codemark";
    private final Path pluginHome;


    public MarkStartedAction() {
        pluginHome = MoreIO.createPluginHome();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final CaretModel caretModel = editor.getCaretModel();
        final String selectedText = caretModel.getCurrentCaret().getSelectedText();
        LOG.info(String.format("Marking Code  %s", selectedText));
        int start = caretModel.getCurrentCaret().getSelectionStart();
        int end = caretModel.getCurrentCaret().getSelectionEnd();
        LOG.info(String.format("Block selection start %s and end %s", start, end));

        if (selectedText == null || selectedText.trim().isBlank()) {
            return;
        }


        runWriteCommandAction(project, () -> _updateDocument(editor, project, selectedText, start, end));

    }

    private void _updateDocument(Editor editor, Project project, String selectedText, int start, int end) {

        Document document = editor.getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);

        String padding = calculateSpacePadding(project, editor);
        CodeTagger markers = new CodeTagger(configFile());

        // Get the text you want to insert
        String startMarker = markers.begin(file.getName());
        String endMarker = padding + markers.end(file.getName());
        String selectedCodeWithPadding = padSelectedTextBlock(selectedText, padding);
        document.replaceString(start, end, prepareFullText(startMarker, selectedCodeWithPadding, endMarker));

        commitChanges(editor, end, startMarker, endMarker);

        CodeAction actionData = saveRequestLocally(file, project, selectedText);

        Sink sink = SessionContext.get().get(SessionContext.ENTRY_SINK);


        Map<String, Object> body = new HashMap<>();

        body.put("action", ACTION_NAME);
        body.put("data", actionData);

        new SinkConsumer(sink).send(body, LOG::info, LOG::error);
    }


    public String configFile() {
        return "/config/marker-copilot.json";
    }

    @NotNull
    private static String prepareFullText(String startMarker, String selectedCodeWithPadding, String endMarker) {
        return String.join(LINE_BREAK, startMarker, selectedCodeWithPadding, endMarker);
    }

    private static String padSelectedTextBlock(String selectedText, String padding) {
        return Arrays.stream(selectedText.split(LINE_BREAK))
                .map(line -> padding + line)
                .collect(Collectors.joining(LINE_BREAK));
    }

    private static void commitChanges(Editor editor, int end, String startMarker, String endMarker) {
        editor.getCaretModel().moveToOffset(end + startMarker.length() + endMarker.length());
    }

    private CodeAction saveRequestLocally(VirtualFile file, Project project, String selectedText) {
        assert file != null;
        String currentUser = SessionContext.get().get(SessionContext.CURRENT_USER);

        CodeAction action = new CodeAction(currentUser, "code_marker", project, file, selectedText);

        String osName = System.getProperty("os.name");
        action.params.put("os.type", osName);
        action.params.put("os.user.name", System.getProperty("user.name"));

        GitAPI git = new CommandLineGiT();

        action.params.putAll(git.context(project.getBasePath()));


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String absolutePath = pluginHome.toFile().getAbsolutePath();
        byte[] bytes = gson.toJson(action).getBytes();
        MoreIO.write(Paths.get(absolutePath, String.format("%s_%s.json", "code_mark", System.nanoTime())), bytes);
        return action;
    }


    private static String calculateSpacePadding(Project project, Editor editor) {

        assert project != null;
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
            String space = codeStyleManager.getLineIndent(psiFile, editor.getSelectionModel().getSelectionStart());
            if (space != null) {
                LOG.info("Indentation of selected code: '" + space + "' spaces and length is " + space.length());
                return space;
            }
        }
        return "";
    }
}
