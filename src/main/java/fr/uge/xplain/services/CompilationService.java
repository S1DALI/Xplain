package fr.uge.xplain.services;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Service for compiling Java source code provided as a string.
 */
public final class CompilationService {

    /**
     * Compiles the provided Java source code and returns a list of diagnostic messages.
     *
     * @param sourceCode the Java source code to compile
     * @return a list of diagnostic messages generated during compilation
     * @throws NullPointerException if sourceCode is null
     */
    public static List<String> compile(String sourceCode) {
        Objects.requireNonNull(sourceCode);
        // Create a JavaFileObject representing sourceCode
        var className = extractClassName(sourceCode);
        var sourceFile = SimpleJavaFileObject.forSource(URI.create(className + ".java"), sourceCode);
        // Compile sourceCode and get diagnostics
        var compiler = ToolProvider.getSystemJavaCompiler();
        var fileManager = compiler.getStandardFileManager(null, null, null);
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        var targetDirectory = "target/userClasses";
        var task = compiler.getTask(null, fileManager, diagnostics, List.of("-d", targetDirectory), null, List.of(sourceFile));
        task.call();
        // Convert list of diagnostics to list of messages and return it
        return diagnostics.getDiagnostics().stream().map(CompilationService::diagnoticToString).toList();
    }

    private static String extractClassName(String sourceCode) {
        // this regex captures the name of a public class or record
        final var PUBLIC_CLASS_REGEX = "public\\s+(record|(final\\s+)?class|enum)\\s+(\\w+)";
        var matcher = Pattern.compile(PUBLIC_CLASS_REGEX).matcher(sourceCode);
        return matcher.find() ? matcher.group(3) : "defaultName";
    }

    private static String diagnoticToString(Diagnostic<? extends JavaFileObject> diagnostic) {
        return "[" + diagnostic.getKind() + "] line " + diagnostic.getLineNumber() + ": " + diagnostic.getMessage(Locale.ROOT);
    }

}
