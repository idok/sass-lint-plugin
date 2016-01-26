package com.sasslint.cli;

import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class SassLint {
    public File file = new File();
    public String version;

    public static SassLint read(String xml) {
        XStream xstream = new XStream();
        xstream.alias("checkstyle", SassLint.class);
        xstream.useAttributeFor(SassLint.class, "version");
        xstream.alias("file", File.class);
        xstream.alias("error", Issue.class);
        xstream.addImplicitCollection(File.class, "errors");
        xstream.useAttributeFor(File.class, "name");
        xstream.useAttributeFor(Issue.class, "source");
        xstream.useAttributeFor(Issue.class, "line");
//        xstream.useAttributeFor(Issue.class, "column");
        xstream.useAttributeFor(Issue.class, "severity");
        xstream.useAttributeFor(Issue.class, "message");
        SassLint lint = (SassLint) xstream.fromXML(xml);
        if (lint.file == null) {
            lint.file = new File();
        }
        if (lint.file.errors == null) {
            lint.file.errors = new ArrayList<Issue>();
        }
        return lint;
    }

    public static class File {
        public String name;
        public List<Issue> errors = new ArrayList<Issue>();
    }

    public static class Issue {
        public String source;
        public int line;
//        public int column;
        public String severity;
        public String message;
    }
}

