package com.sasslint.config;

import com.sasslint.SassLintProjectComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;

/**
 * @author idok
 */
public final class SassLintConfigFileUtil {
    private SassLintConfigFileUtil() {
    }

    public static boolean isSassFile(PsiFile file) {
        return file.getName().endsWith(".scss") || file.getName().endsWith(".sass") || isExt(file);
    }

    private static boolean isExt(PsiFile file) {
        SassLintProjectComponent component = file.getProject().getComponent(SassLintProjectComponent.class);
        if (StringUtils.isEmpty(component.extensions)) {
            return false;
        }
        String[] exts = component.extensions.split(",");
        for (String ext : exts) {
            if (file.getName().endsWith('.' + ext)) {
                return true;
            }
        }
        return false;
    }

//    public static boolean isSassLintConfigFile(PsiElement position) {
//        return isSassLintConfigFile(position.getContainingFile().getOriginalFile().getVirtualFile());
//    }

    public static boolean isSassLintConfigFile(VirtualFile file) {
        return file != null && file.getName().equals(SassLintConfigFileType.SASS_LINT_CONFIG);
    }
}
