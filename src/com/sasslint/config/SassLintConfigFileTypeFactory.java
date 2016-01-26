package com.sasslint.config;

import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class SassLintConfigFileTypeFactory extends FileTypeFactory {
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(SassLintConfigFileType.INSTANCE, new ExactFileNameMatcher(SassLintConfigFileType.SASS_LINT_CONFIG));
    }
}