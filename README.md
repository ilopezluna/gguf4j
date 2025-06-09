# GGUF4J - Java GGUF File Parser

A Java port of the [GGUF parser from gpustack/gguf-parser-go](https://github.com/gpustack/gguf-parser-go), providing comprehensive parsing and analysis capabilities for GGUF (GPT-Generated Unified Format) files.

## Features

- ✅ **Complete GGUF Support**: Parse GGUF files versions 1, 2, and 3
- ✅ **All GGML Data Types**: Support for all quantization types (F32, F16, Q4_0, Q4_1, Q5_0, Q5_1, Q8_0, Q2_K, Q3_K, Q4_K, Q5_K, Q6_K, IQ variants, etc.)
- ✅ **Memory Efficient**: Stream-based parsing without loading entire files into memory
- ✅ **Comprehensive Metadata**: Extract and access all metadata with type-safe APIs
- ✅ **Architecture Detection**: Automatic detection of model architectures (LLaMA, Qwen, GPT, etc.)
- ✅ **Tensor Analysis**: Detailed tensor information including dimensions, types, and sizes
- ✅ **Command Line Interface**: Ready-to-use CLI for file analysis
- ✅ **Modern Java**: Built with Java 21 features (records, pattern matching, sealed interfaces)

## Quick Start

### As a Library

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.ilopezluna</groupId>
    <artifactId>gguf4j</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Parse a GGUF file:

```java
import io.github.ilopezluna.gguf4j.core.GGUFFile;
import io.github.ilopezluna.gguf4j.core.GGUFParser;
import java.nio.file.Paths;

// Parse a GGUF file
GGUFFile ggufFile = GGUFParser.parse(Paths.get("model.gguf"));

// Get basic information
System.out.println("Architecture: " + ggufFile.getArchitecture().orElse("Unknown"));
System.out.println("Parameters: " + ggufFile.getTotalParameters());
System.out.println("Model size: " + ggufFile.getTotalTensorSize() + " bytes");
System.out.println("Avg BPW: " + ggufFile.getAverageBitsPerWeight());

// Access metadata
ggufFile.getContextLength().ifPresent(ctx -> 
    System.out.println("Context length: " + ctx));
ggufFile.getBlockCount().ifPresent(blocks -> 
    System.out.println("Layers: " + blocks));

// Iterate through tensors
for (var tensor : ggufFile.tensors()) {
    System.out.println(tensor.name() + ": " + tensor.type() + 
                      " " + Arrays.toString(tensor.dimensions()));
}
```

### Command Line Usage

```bash
# Build the project
mvn clean package

# Show file summary
java -jar target/gguf4j-1.0-SNAPSHOT.jar model.gguf

# Show detailed metadata
java -jar target/gguf4j-1.0-SNAPSHOT.jar --metadata model.gguf

# Show tensor information
java -jar target/gguf4j-1.0-SNAPSHOT.jar --tensors model.gguf

# Fast metadata-only parsing (for large files)
java -jar target/gguf4j-1.0-SNAPSHOT.jar --metadata-only model.gguf

# Show help
java -jar target/gguf4j-1.0-SNAPSHOT.jar --help
```

## API Overview

### Core Classes

- **`GGUFFile`**: Main container for parsed GGUF file data
- **`GGUFParser`**: Static methods for parsing GGUF files
- **`GGUFMetadata`**: Type-safe access to metadata key-value pairs
- **`GGUFTensorInfo`**: Information about individual tensors
- **`GGMLType`**: Enumeration of all GGML data types

### Key Methods

```java
// Parse from file path
GGUFFile file = GGUFParser.parse(Paths.get("model.gguf"));

// Parse from InputStream
GGUFFile file = GGUFParser.parse(inputStream);

// Quick metadata-only parsing
GGUFFile file = GGUFParser.parseHeaderAndMetadata(inputStream);

// Validate file format
boolean isValid = GGUFParser.isValidGGUFFile(inputStream);

// Get version without full parsing
int version = GGUFParser.getVersion(inputStream);
```

### Metadata Access

```java
// Architecture-specific information
Optional<String> arch = file.getArchitecture();
Optional<Long> contextLen = file.getContextLength();
Optional<Long> embeddingLen = file.getEmbeddingLength();
Optional<Long> layers = file.getBlockCount();
Optional<Long> headCount = file.getAttentionHeadCount();

// Tokenizer information
Optional<String> tokenizerModel = file.getTokenizerModel();
Optional<Long> bosToken = file.getBosTokenId();
Optional<Long> eosToken = file.getEosTokenId();

// Raw metadata access
Optional<GGUFMetadataValue> value = file.metadata().get("custom.key");
```

### Tensor Operations

```java
// Find tensors by pattern
List<GGUFTensorInfo> weights = file.findTensors(".*\\.weight");

// Get tensors by layer
List<GGUFTensorInfo> layer0 = file.getTensorsByLayer(0);

// Filter by type
List<GGUFTensorInfo> weights = file.getWeightTensors();
List<GGUFTensorInfo> biases = file.getBiasTensors();

// Tensor information
GGUFTensorInfo tensor = file.findTensor("token_embd.weight").orElse(null);
if (tensor != null) {
    System.out.println("Name: " + tensor.name());
    System.out.println("Type: " + tensor.type());
    System.out.println("Dimensions: " + Arrays.toString(tensor.dimensions()));
    System.out.println("Elements: " + tensor.getElementCount());
    System.out.println("Size: " + tensor.getSizeInBytes() + " bytes");
}
```

## Supported Architectures

The library automatically detects and provides specialized support for:

- **LLaMA** (llama, llama2, llama3)
- **Qwen** (qwen, qwen2, qwen2vl)
- **GPT** (gpt2, gptj, gptneox)
- **Falcon** (falcon)
- **Baichuan** (baichuan)
- **Gemma** (gemma)
- **Phi** (phi2, phi3)
- **Mistral** and many others

## Supported GGML Types

All GGML quantization types are supported:

- **Float types**: F32, F16, BF16, F64
- **Integer types**: I8, I16, I32, I64
- **Legacy quantized**: Q4_0, Q4_1, Q5_0, Q5_1, Q8_0, Q8_1
- **K-quantized**: Q2_K, Q3_K, Q4_K, Q5_K, Q6_K, Q8_K
- **IQ variants**: IQ1_S, IQ1_M, IQ2_XXS, IQ2_XS, IQ2_S, IQ3_XXS, IQ3_S, IQ4_NL, IQ4_XS
- **Specialized**: Q4_0_4_4, Q4_0_4_8, Q4_0_8_8, TQ1_0, TQ2_0

## Building

Requirements:
- Java 21 or later
- Maven 3.6 or later

```bash
# Clone the repository
git clone https://github.com/ilopezluna/gguf4j.git
cd gguf4j

# Build the project
mvn clean package

# Run tests
mvn test

# Create executable JAR
mvn clean package
java -jar target/gguf4j-1.0-SNAPSHOT.jar --help
```

## Examples

### Basic File Analysis

```java
import io.github.ilopezluna.gguf4j.core.*;
import java.nio.file.Paths;

public class Example {
    public static void main(String[] args) throws Exception {
        GGUFFile file = GGUFParser.parse(Paths.get("model.gguf"));
        
        System.out.println("=== Model Information ===");
        System.out.println("Architecture: " + file.getArchitecture().orElse("Unknown"));
        System.out.println("Name: " + file.getName().orElse("Unknown"));
        System.out.println("Parameters: " + formatNumber(file.getTotalParameters()));
        System.out.println("Size: " + formatBytes(file.getTotalTensorSize()));
        System.out.println("Quantization: " + getQuantizationName(file.getFileType()));
        
        file.getContextLength().ifPresent(ctx -> 
            System.out.println("Context Length: " + ctx));
        file.getBlockCount().ifPresent(layers -> 
            System.out.println("Layers: " + layers));
    }
    
    private static String formatNumber(long num) {
        if (num >= 1_000_000_000) return String.format("%.1fB", num / 1e9);
        if (num >= 1_000_000) return String.format("%.1fM", num / 1e6);
        if (num >= 1_000) return String.format("%.1fK", num / 1e3);
        return String.valueOf(num);
    }
    
    private static String formatBytes(long bytes) {
        if (bytes >= 1L << 30) return String.format("%.1f GB", bytes / (double)(1L << 30));
        if (bytes >= 1L << 20) return String.format("%.1f MB", bytes / (double)(1L << 20));
        if (bytes >= 1L << 10) return String.format("%.1f KB", bytes / (double)(1L << 10));
        return bytes + " bytes";
    }
}
```

### Tensor Analysis

```java
// Analyze model structure
GGUFFile file = GGUFParser.parse(Paths.get("model.gguf"));

// Group tensors by type
Map<GGMLType, List<GGUFTensorInfo>> tensorsByType = file.tensors().stream()
    .collect(Collectors.groupingBy(GGUFTensorInfo::type));

System.out.println("=== Tensor Distribution ===");
tensorsByType.forEach((type, tensors) -> {
    long totalSize = tensors.stream().mapToLong(GGUFTensorInfo::getSizeInBytes).sum();
    System.out.printf("%-10s: %3d tensors, %s\n", 
        type, tensors.size(), formatBytes(totalSize));
});

// Find embedding and output layers
file.findTensor("token_embd.weight").ifPresent(emb -> 
    System.out.println("Embedding: " + Arrays.toString(emb.dimensions())));
file.findTensor("output.weight").ifPresent(out -> 
    System.out.println("Output: " + Arrays.toString(out.dimensions())));

// Analyze attention layers
long layers = file.getBlockCount().orElse(0L);
for (int i = 0; i < layers; i++) {
    List<GGUFTensorInfo> layerTensors = file.getTensorsByLayer(i);
    if (!layerTensors.isEmpty()) {
        long layerSize = layerTensors.stream()
            .mapToLong(GGUFTensorInfo::getSizeInBytes).sum();
        System.out.printf("Layer %2d: %2d tensors, %s\n", 
            i, layerTensors.size(), formatBytes(layerSize));
    }
}
```

## Performance

The library is designed for efficiency:

- **Streaming parsing**: No need to load entire files into memory
- **Lazy evaluation**: Tensor data is not read unless specifically requested
- **Fast metadata access**: Quick parsing of headers and metadata only
- **Memory efficient**: Minimal object allocation during parsing

For large files, use `parseHeaderAndMetadata()` for fastest access to model information without parsing tensor details.

## Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Original Go implementation: [gpustack/gguf-parser-go](https://github.com/gpustack/gguf-parser-go)
- GGUF specification: [ggml GGUF documentation](https://github.com/ggerganov/ggml/blob/master/docs/gguf.md)
- GGML project: [ggerganov/ggml](https://github.com/ggerganov/ggml)
