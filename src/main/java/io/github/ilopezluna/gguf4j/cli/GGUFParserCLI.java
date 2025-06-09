package io.github.ilopezluna.gguf4j.cli;

import io.github.ilopezluna.gguf4j.core.GGUFFile;
import io.github.ilopezluna.gguf4j.core.GGUFParser;
import io.github.ilopezluna.gguf4j.core.GGUFTensorInfo;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Command-line interface for the GGUF parser.
 */
@Command(
    name = "gguf4j",
    description = "Parse and analyze GGUF files",
    version = "1.0.0",
    mixinStandardHelpOptions = true
)
public class GGUFParserCLI implements Callable<Integer> {

    @Parameters(
        index = "0",
        description = "Path to the GGUF file to parse"
    )
    private Path filePath;

    @Option(
        names = {"-s", "--summary"},
        description = "Show only a summary of the file"
    )
    private boolean summary = false;

    @Option(
        names = {"-m", "--metadata"},
        description = "Show metadata information"
    )
    private boolean showMetadata = false;

    @Option(
        names = {"-t", "--tensors"},
        description = "Show tensor information"
    )
    private boolean showTensors = false;

    @Option(
        names = {"-v", "--verbose"},
        description = "Show detailed information"
    )
    private boolean verbose = false;

    @Option(
        names = {"--metadata-only"},
        description = "Parse only header and metadata (faster for large files)"
    )
    private boolean metadataOnly = false;

    @Override
    public Integer call() throws Exception {
        try {
            System.out.println("Parsing GGUF file: " + filePath);
            System.out.println();

            GGUFFile ggufFile;
            if (metadataOnly) {
                ggufFile = GGUFParser.parseHeaderAndMetadata(
                    java.nio.file.Files.newInputStream(filePath)
                );
            } else {
                ggufFile = GGUFParser.parse(filePath);
            }

            if (summary || (!showMetadata && !showTensors)) {
                printSummary(ggufFile);
            }

            if (showMetadata) {
                printMetadata(ggufFile);
            }

            if (showTensors && !metadataOnly) {
                printTensors(ggufFile);
            }

            return 0;
        } catch (IOException e) {
            System.err.println("Error parsing file: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    private void printSummary(GGUFFile ggufFile) {
        System.out.println("=== GGUF File Summary ===");
        System.out.println("Version: " + ggufFile.getVersion());
        System.out.println("Architecture: " + ggufFile.getArchitecture().orElse("Unknown"));
        System.out.println("Name: " + ggufFile.getName().orElse("Unknown"));
        
        if (!metadataOnly) {
            System.out.println("Tensors: " + ggufFile.getTensorCount());
            System.out.println("Parameters: " + formatNumber(ggufFile.getTotalParameters()));
            System.out.println("Model Size: " + formatBytes(ggufFile.getTotalTensorSize()));
            System.out.println("Avg BPW: " + String.format("%.2f", ggufFile.getAverageBitsPerWeight()));
        }
        
        ggufFile.getContextLength().ifPresent(ctx -> 
            System.out.println("Context Length: " + formatNumber(ctx)));
        ggufFile.getBlockCount().ifPresent(blocks -> 
            System.out.println("Layers: " + blocks));
        ggufFile.getEmbeddingLength().ifPresent(emb -> 
            System.out.println("Embedding Length: " + emb));
        ggufFile.getAttentionHeadCount().ifPresent(heads -> 
            System.out.println("Attention Heads: " + heads));
        
        System.out.println();
    }

    private void printMetadata(GGUFFile ggufFile) {
        System.out.println("=== Metadata ===");
        System.out.println("Total entries: " + ggufFile.getMetadataCount());
        System.out.println();
        
        ggufFile.metadata().keySet().stream()
                .sorted()
                .forEach(key -> {
                    var value = ggufFile.metadata().get(key).orElse(null);
                    if (value != null) {
                        System.out.printf("%-40s: %s%n", key, formatMetadataValue(value));
                    }
                });
        System.out.println();
    }

    private void printTensors(GGUFFile ggufFile) {
        System.out.println("=== Tensors ===");
        System.out.println("Total tensors: " + ggufFile.getTensorCount());
        System.out.println();
        
        if (verbose) {
            // Show all tensors
            for (GGUFTensorInfo tensor : ggufFile.tensors()) {
                System.out.printf("%-50s %15s %10s %15s%n",
                    tensor.name(),
                    tensor.getDimensionsString(),
                    tensor.type(),
                    formatBytes(tensor.getSizeInBytes())
                );
            }
        } else {
            // Show summary by type
            var tensorsByType = ggufFile.tensors().stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                        t -> t.type(),
                        java.util.stream.Collectors.counting()
                    ));
            
            System.out.println("Tensors by type:");
            tensorsByType.entrySet().stream()
                    .sorted(java.util.Map.Entry.<io.github.ilopezluna.gguf4j.scalar.GGMLType, Long>comparingByValue().reversed())
                    .forEach(entry -> 
                        System.out.printf("  %-10s: %d tensors%n", entry.getKey(), entry.getValue())
                    );
        }
        System.out.println();
    }

    private String formatMetadataValue(io.github.ilopezluna.gguf4j.core.GGUFMetadataValue value) {
        if (value.isArray()) {
            var array = value.asArray();
            if (array.size() > 10) {
                return String.format("[%s array with %d elements]", array.elementType(), array.size());
            } else {
                return array.values().toString();
            }
        }
        return value.getValue().toString();
    }

    private String formatNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("%.2fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("%.2fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.2fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }

    private String formatBytes(long bytes) {
        if (bytes >= 1024L * 1024 * 1024) {
            return String.format("%.2f GiB", bytes / (1024.0 * 1024 * 1024));
        } else if (bytes >= 1024L * 1024) {
            return String.format("%.2f MiB", bytes / (1024.0 * 1024));
        } else if (bytes >= 1024) {
            return String.format("%.2f KiB", bytes / 1024.0);
        } else {
            return bytes + " bytes";
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GGUFParserCLI()).execute(args);
        System.exit(exitCode);
    }
}
