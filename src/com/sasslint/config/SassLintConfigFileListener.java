package com.sasslint.config;

import com.sasslint.SassLintProjectComponent;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class SassLintConfigFileListener {
    private final Project project;
    private final AtomicBoolean LISTENING = new AtomicBoolean(false);

    public SassLintConfigFileListener(@NotNull Project project) {
        this.project = project;
    }

    private void startListener() {
        if (LISTENING.compareAndSet(false, true))
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            VirtualFileManager.getInstance().addVirtualFileListener(new SassLintConfigFileVfsListener(), SassLintConfigFileListener.this.project);
                            EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
                            multicaster.addDocumentListener(new SassLintConfigFileDocumentListener(), SassLintConfigFileListener.this.project);
                        }
                    });
                }
            });
    }

    public static void start(@NotNull Project project) {
        SassLintConfigFileListener listener = ServiceManager.getService(project, SassLintConfigFileListener.class);
        listener.startListener();
    }

    private void fileChanged(@NotNull VirtualFile file) {
        if (SassLintConfigFileUtil.isSassLintConfigFile(file) && !project.isDisposed()) {
            restartAnalyzer();
        }
    }

    private void restartAnalyzer() {
        SassLintProjectComponent component = project.getComponent(SassLintProjectComponent.class);
        if (component.isEnabled()) {
            DaemonCodeAnalyzer.getInstance(project).restart();
        }
    }

    /**
     * VFS Listener
     */
    private class SassLintConfigFileVfsListener extends VirtualFileAdapter {
        private SassLintConfigFileVfsListener() {
        }

        public void fileCreated(@NotNull VirtualFileEvent event) {
            SassLintConfigFileListener.this.fileChanged(event.getFile());
        }

        public void fileDeleted(@NotNull VirtualFileEvent event) {
            SassLintConfigFileListener.this.fileChanged(event.getFile());
        }

        public void fileMoved(@NotNull VirtualFileMoveEvent event) {
            SassLintConfigFileListener.this.fileChanged(event.getFile());
        }

        public void fileCopied(@NotNull VirtualFileCopyEvent event) {
            SassLintConfigFileListener.this.fileChanged(event.getFile());
//            SassLintConfigFileListener.this.fileChanged(event.getOriginalFile());
        }
    }

    /**
     * Document Listener
     */
    private class SassLintConfigFileDocumentListener extends DocumentAdapter {
        private SassLintConfigFileDocumentListener() {
        }

        public void documentChanged(DocumentEvent event) {
            VirtualFile file = FileDocumentManager.getInstance().getFile(event.getDocument());
            if (file != null) {
                SassLintConfigFileListener.this.fileChanged(file);
            }
        }
    }
}

