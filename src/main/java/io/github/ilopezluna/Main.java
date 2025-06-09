package io.github.ilopezluna;

/**
 * Main entry point for the GGUF4J library.
 * 
 * This class demonstrates the usage of the GGUF parser library.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageExample();
        } else {
            // Try to delegate to CLI if available
            try {
                Class<?> cliClass = Class.forName("io.github.ilopezluna.gguf4j.cli.GGUFParserCLI");
                var mainMethod = cliClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) args);
            } catch (ClassNotFoundException e) {
                System.err.println("CLI functionality requires Maven to build with dependencies.");
                System.err.println("Please use Maven to build the full project:");
                System.err.println("  mvn clean package");
                System.err.println("  java -jar target/gguf4j-1.0-SNAPSHOT.jar " + String.join(" ", args));
                System.exit(1);
            } catch (Exception e) {
                System.err.println("Error running CLI: " + e.getMessage());
                if (args.length > 0 && (args[0].equals("-v") || args[0].equals("--verbose"))) {
                    e.printStackTrace();
                }
                System.exit(1);
            }
        }
    }

    private static void printUsageExample() {
        System.out.println("GGUF4J - Java GGUF File Parser");
        System.out.println("==============================");
        System.out.println();
        System.out.println("This is a Java port of the GGUF parser from https://github.com/gpustack/gguf-parser-go");
        System.out.println();
        System.out.println("Usage examples:");
        System.out.println("  java -jar gguf4j.jar <path-to-gguf-file>");
        System.out.println("  java -jar gguf4j.jar --summary <path-to-gguf-file>");
        System.out.println("  java -jar gguf4j.jar --metadata <path-to-gguf-file>");
        System.out.println("  java -jar gguf4j.jar --tensors <path-to-gguf-file>");
        System.out.println("  java -jar gguf4j.jar --metadata-only <path-to-gguf-file>");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -s, --summary      Show only a summary of the file");
        System.out.println("  -m, --metadata     Show metadata information");
        System.out.println("  -t, --tensors      Show tensor information");
        System.out.println("  -v, --verbose      Show detailed information");
        System.out.println("  --metadata-only    Parse only header and metadata (faster for large files)");
        System.out.println("  -h, --help         Show help message");
        System.out.println("  -V, --version      Show version information");
        System.out.println();
        System.out.println("Library usage example:");
        System.out.println();
        System.out.println("  import io.github.ilopezluna.gguf4j.core.GGUFFile;");
        System.out.println("  import io.github.ilopezluna.gguf4j.core.GGUFParser;");
        System.out.println("  import java.nio.file.Paths;");
        System.out.println();
        System.out.println("  // Parse a GGUF file");
        System.out.println("  GGUFFile ggufFile = GGUFParser.parse(Paths.get(\"model.gguf\"));");
        System.out.println();
        System.out.println("  // Get basic information");
        System.out.println("  System.out.println(\"Architecture: \" + ggufFile.getArchitecture().orElse(\"Unknown\"));");
        System.out.println("  System.out.println(\"Parameters: \" + ggufFile.getTotalParameters());");
        System.out.println("  System.out.println(\"Model size: \" + ggufFile.getTotalTensorSize() + \" bytes\");");
        System.out.println();
        System.out.println("  // Access metadata");
        System.out.println("  ggufFile.getContextLength().ifPresent(ctx -> ");
        System.out.println("      System.out.println(\"Context length: \" + ctx));");
        System.out.println();
        System.out.println("  // Iterate through tensors");
        System.out.println("  for (var tensor : ggufFile.tensors()) {");
        System.out.println("      System.out.println(tensor.name() + \": \" + tensor.type());");
        System.out.println("  }");
        System.out.println();
        System.out.println("Features:");
        System.out.println("  ✓ Parse GGUF file headers and metadata");
        System.out.println("  ✓ Extract tensor information");
        System.out.println("  ✓ Support for all GGUF versions (1, 2, 3)");
        System.out.println("  ✓ Support for all GGML data types");
        System.out.println("  ✓ Memory-efficient parsing");
        System.out.println("  ✓ Command-line interface");
        System.out.println("  ✓ Comprehensive metadata access");
        System.out.println("  ✓ Architecture-specific information extraction");
        System.out.println();
        System.out.println("For more information, visit: https://github.com/ilopezluna/gguf4j");
    }
}
