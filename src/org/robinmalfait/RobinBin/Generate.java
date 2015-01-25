package org.robinmalfait.RobinBin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Generate extends AnAction {

    private String selectedTextIfAny;

    public void actionPerformed(AnActionEvent e) {
        String contents = getContents(e);
        RobinBin robinBin = new RobinBin();

        robinBin.save(contents);
    }

    private String getContents(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        String contents;

        if (editor == null) {
            e.getPresentation().setEnabled(false);
            return "";
        }

        contents = this.getEditorContents(editor);

        return contents;
    }

    private List<String> splitContentsInLines(String contents) {
        return Arrays.asList(contents.split("\n"));
    }

    public String getEditorContents(Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        Document document = editor.getDocument();

        if (selectionModel.hasSelection()) {
            return getSelectedText(selectionModel, document);
        }

        return document.getText();
    }

    private String getSelectedText(SelectionModel selectionModel, Document document) {
        VisualPosition start = selectionModel.getSelectionStartPosition();
        VisualPosition end = selectionModel.getSelectionEndPosition();

        String lines[] = document.getText().split("\n");
        List<String> contentLines = new ArrayList<String>();

        for (int i = start.getLine(); i <= end.getLine(); i++) {
            contentLines.add(lines[i]);
        }

        int diff = contentLines.get(0).length() - contentLines.get(0).trim().length();

        if (diff > 0) {
            for (int i = 0; i < contentLines.size(); i++) {
                contentLines.set(i, this.leftTrim(contentLines.get(i), diff));
            }
        }

        return this.arrayToStringConversion(contentLines);
    }

    private String leftTrim(String content, int amount) {
        if (amount > content.length()) return content;

        return content.substring(amount, content.length());
    }

    private String arrayToStringConversion(List<String> contentLines) {
        return StringUtils.join(contentLines, "\n");
    }
}