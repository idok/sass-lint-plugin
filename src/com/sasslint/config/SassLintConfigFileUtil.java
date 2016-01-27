package com.sasslint.config;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * @author idok
 */
public final class SassLintConfigFileUtil {
    public static final String SASS_LINT_CONFIG = ".sass-lint.yml";

    private SassLintConfigFileUtil() {
    }

    public static boolean isSassFile(PsiFile file) {
        return file.getName().endsWith(".scss") || file.getName().endsWith(".sass");
    }

    public static boolean isSassLintConfigFile(VirtualFile file) {
        return file != null && file.getName().equals(SASS_LINT_CONFIG);
    }
}
