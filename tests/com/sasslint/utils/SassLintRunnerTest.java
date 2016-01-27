package com.sasslint.utils;

import com.sasslint.TestUtils;
import com.sasslint.cli.SassLintRunner;
import com.sasslint.cli.LintResult;
import com.intellij.execution.ExecutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SassLintRunnerTest {

    public static final String NODE_INTERPRETER = "/Users/idok/.nvm/versions/node/v4.2.4/bin/node";
    public static final String SASS_LINT_BIN = "/Users/idok/.nvm/versions/node/v4.2.4/bin/sass-lint";
    public static final String TEST_DATA = TestUtils.getTestDataPath();

    private static SassLintRunner.SassLintSettings createSettings(String targetFile) {
        return SassLintRunner.buildSettings(TEST_DATA, targetFile, NODE_INTERPRETER, SASS_LINT_BIN, "", "", null);
    }

    private static SassLintRunner.SassLintSettings createSettings() {
        return createSettings("");
    }

    @Test
    public void testSimpleLint() {
        SassLintRunner.SassLintSettings settings = createSettings(TEST_DATA + "/inspections/one.scss");
        LintResult out = SassLintRunner.lint(settings);
        System.out.println(out.errorOutput);
        assertEquals("Exit code should be 1", 1, out.sassLint.file.errors.size());
    }

    @Test
    public void testLintWithConfig() {
        SassLintRunner.SassLintSettings settings = createSettings(TEST_DATA + "/unit/one.scss");
        settings.config = TEST_DATA + "/unit/.sass-lint.yml";
        LintResult out = SassLintRunner.lint(settings);
        assertEquals("Should have 3 lint error", 3, out.sassLint.file.errors.size());
    }

    @Test
    public void testVersion() {
        SassLintRunner.SassLintSettings settings = createSettings();
        try {
            String out = SassLintRunner.runVersion(settings);
            assertEquals("version should be", "1.4.0", out);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
