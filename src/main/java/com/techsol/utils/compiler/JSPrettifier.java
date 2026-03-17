package com.techsol.utils.compiler;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.StaticSourceFile;

public class JSPrettifier {
    public static String prettifyJS(String jsCode) {
        // System.out.println("Input: " + jsCode + "\n\n");
        CompilerOptions options = new CompilerOptions();
        options.setPrettyPrint(true);

        Compiler compiler = new Compiler();
        SourceFile inputs = SourceFile.fromCode("input.js", jsCode, StaticSourceFile.SourceKind.EXTERN);
        Result result = compiler.compile(SourceFile.fromCode("externs.js", ""), inputs, options);

        if (result.success) {
            return compiler.toSource();
        } else {
            for (JSError error : result.errors) {
                System.out.println(error.toString());
            }
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        // File file = new File("/Users/georgegithogori/Cyrus Work/Java
        // Projects/bit-symphony/src/main/resources/site/templates/index.js");

        // FileInputStream inputStream = new FileInputStream(file);
        // String code = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        // inputStream.close();
        // System.out.println(prettifyJS(code));
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");
        System.out.println(e.getFactory().getEngineName());
    }
}