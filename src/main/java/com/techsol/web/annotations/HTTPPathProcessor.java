package com.techsol.web.annotations;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.techsol.web.annotations.HTTPPath")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class HTTPPathProcessor extends AbstractProcessor {
    private boolean fileCreated = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (fileCreated || roundEnv.processingOver()) {
            return false;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(HTTPPath.class);
        if (elements.isEmpty()) {
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder()
                .append("package com.techsol.web.routes;\n\n")
                .append("import java.util.Map;\n")
                .append("import java.util.HashMap;\n")
                .append("import java.util.function.BiConsumer;\n")
                .append("import java.io.IOException;\n")
                .append("import com.techsol.web.http.HTTPRequest;\n")
                .append("import com.techsol.web.http.HTTPResponse;\n")
                .append("import javax.xml.transform.TransformerException;\n\n")
                .append("public class RouteRegistry {\n")
                .append("    public static final Map<String, BiConsumer<HTTPRequest, HTTPResponse>> ROUTES = new HashMap<>();\n")
                .append("    static {\n");

        for (Element annotatedElement : elements) {
            if (annotatedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) annotatedElement;
                List<? extends TypeMirror> thrownTypes = method.getThrownTypes();
                String exceptionHandling = generateExceptionHandling(thrownTypes);

                HTTPPath httpPath = method.getAnnotation(HTTPPath.class);
                String path = httpPath.path();

                TypeElement classElement = (TypeElement) method.getEnclosingElement();
                String packageName = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName()
                        .toString();
                String className = classElement.getSimpleName().toString();
                String fullClassName = packageName + "." + className;

                String methodName = method.getSimpleName().toString();

                stringBuilder.append("        ROUTES.put(\"")
                        .append(path)
                        .append("\", (request, response) -> {\n");

                if (!thrownTypes.isEmpty()) {
                    stringBuilder.append("            try {\n")
                            .append("                new ")
                            .append(fullClassName)
                            .append("().")
                            .append(methodName)
                            .append("(request, response);\n")
                            .append("            }")
                            .append(exceptionHandling);
                } else {
                    stringBuilder.append("            new ")
                            .append(fullClassName)
                            .append("().")
                            .append(methodName)
                            .append("(request, response);\n");
                }

                stringBuilder.append("        });\n");
            }
        }

        stringBuilder.append("    }\n")
                .append("}\n");

        try {
            JavaFileObject builderFile = processingEnv.getFiler()
                    .createSourceFile("com.techsol.web.routes.RouteRegistry");
            try (Writer writer = builderFile.openWriter()) {
                writer.write(stringBuilder.toString());
                fileCreated = true;
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Error creating RouteRegistry: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private String generateExceptionHandling(List<? extends TypeMirror> thrownTypes) {
        if (thrownTypes.isEmpty()) {
            return "";
        }

        // Get the exception types
        String exceptions = thrownTypes.stream()
                .map(type -> type.toString())
                .collect(Collectors.joining(" | "));

        return String.format(" catch (%s e) {\n                e.printStackTrace();\n            }\n", exceptions);
    }
}