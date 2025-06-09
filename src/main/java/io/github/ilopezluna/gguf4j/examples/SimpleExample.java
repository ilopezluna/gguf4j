package io.github.ilopezluna.gguf4j.examples;

import io.github.ilopezluna.gguf4j.core.*;
import io.github.ilopezluna.gguf4j.scalar.GGMLType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple example demonstrating GGUF4J library usage.
 */
public class SimpleExample {
    
    public static void main(String[] args) {
        try {
            System.out.println("GGUF4J Library Example");
            System.out.println("======================");
            System.out.println();
            
            // Create a sample GGUF file in memory for demonstration
            byte[] sampleGGUFData = createSampleGGUFFile();
            
            // Parse the GGUF file
            GGUFFile ggufFile = GGUFParser.parse(new ByteArrayInputStream(sampleGGUFData));
            
            // Display basic information
            System.out.println("📄 File Information:");
            System.out.println("  Version: " + ggufFile.getVersion());
            System.out.println("  Architecture: " + ggufFile.getArchitecture().orElse("Unknown"));
            System.out.println("  Name: " + ggufFile.getName().orElse("Unknown"));
            System.out.println("  Tensors: " + ggufFile.getTensorCount());
            System.out.println("  Parameters: " + formatNumber(ggufFile.getTotalParameters()));
            System.out.println("  Model Size: " + formatBytes(ggufFile.getTotalTensorSize()));
            System.out.println("  Avg BPW: " + String.format("%.2f", ggufFile.getAverageBitsPerWeight()));
            System.out.println();
            
            // Display architecture-specific information
            System.out.println("🏗️  Architecture Details:");
            ggufFile.getContextLength().ifPresent(ctx -> 
                System.out.println("  Context Length: " + formatNumber(ctx)));
            ggufFile.getEmbeddingLength().ifPresent(emb -> 
                System.out.println("  Embedding Length: " + emb));
            ggufFile.getBlockCount().ifPresent(blocks -> 
                System.out.println("  Layers: " + blocks));
            ggufFile.getAttentionHeadCount().ifPresent(heads -> 
                System.out.println("  Attention Heads: " + heads));
            System.out.println();
            
            // Display tokenizer information
            System.out.println("🔤 Tokenizer Information:");
            ggufFile.getTokenizerModel().ifPresent(model -> 
                System.out.println("  Model: " + model));
            ggufFile.getBosTokenId().ifPresent(bos -> 
                System.out.println("  BOS Token ID: " + bos));
            ggufFile.getEosTokenId().ifPresent(eos -> 
                System.out.println("  EOS Token ID: " + eos));
            System.out.println();
            
            // Analyze tensors
            System.out.println("🧮 Tensor Analysis:");
            Map<GGMLType, Long> tensorsByType = ggufFile.tensors().stream()
                .collect(Collectors.groupingBy(
                    GGUFTensorInfo::type,
                    Collectors.counting()
                ));
            
            tensorsByType.forEach((type, count) -> 
                System.out.println("  " + type + ": " + count + " tensors"));
            System.out.println();
            
            // Show some tensor details
            System.out.println("📊 Sample Tensors:");
            ggufFile.tensors().stream()
                .limit(5)
                .forEach(tensor -> {
                    System.out.printf("  %-20s %10s %15s %10s%n",
                        tensor.name(),
                        tensor.type(),
                        tensor.getDimensionsString(),
                        formatBytes(tensor.getSizeInBytes())
                    );
                });
            
            if (ggufFile.tensors().size() > 5) {
                System.out.println("  ... and " + (ggufFile.tensors().size() - 5) + " more tensors");
            }
            System.out.println();
            
            // Demonstrate metadata access
            System.out.println("🔍 Metadata Keys (first 10):");
            ggufFile.metadata().keySet().stream()
                .sorted()
                .limit(10)
                .forEach(key -> {
                    var value = ggufFile.metadata().get(key).orElse(null);
                    if (value != null) {
                        String valueStr = value.getValue().toString();
                        if (valueStr.length() > 50) {
                            valueStr = valueStr.substring(0, 47) + "...";
                        }
                        System.out.printf("  %-30s: %s%n", key, valueStr);
                    }
                });
            
            System.out.println();
            System.out.println("✅ Example completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a sample GGUF file in memory for demonstration purposes.
     */
    private static byte[] createSampleGGUFFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Write header
        writeInt32(baos, GGUFConstants.GGUF_MAGIC);
        writeInt32(baos, GGUFConstants.GGUF_VERSION_3);
        writeUInt64(baos, 3); // tensor count
        writeUInt64(baos, 6); // metadata count
        
        // Write metadata
        writeMetadata(baos, GGUFConstants.GENERAL_ARCHITECTURE, "llama");
        writeMetadata(baos, GGUFConstants.GENERAL_NAME, "example-model-7b");
        writeMetadata(baos, "llama.context_length", 4096L);
        writeMetadata(baos, "llama.embedding_length", 4096L);
        writeMetadata(baos, "llama.block_count", 32L);
        writeMetadata(baos, "llama.attention.head_count", 32L);
        
        // Write tensor information
        writeTensorInfo(baos, "token_embd.weight", new long[]{32000, 4096}, GGMLType.F16);
        writeTensorInfo(baos, "blk.0.attn_q.weight", new long[]{4096, 4096}, GGMLType.Q4_0);
        writeTensorInfo(baos, "output.weight", new long[]{4096, 32000}, GGMLType.F16);
        
        // Align to 32 bytes
        while (baos.size() % 32 != 0) {
            baos.write(0);
        }
        
        return baos.toByteArray();
    }
    
    private static void writeMetadata(ByteArrayOutputStream baos, String key, String value) throws IOException {
        writeString(baos, key);
        writeInt32(baos, 8); // STRING type
        writeString(baos, value);
    }
    
    private static void writeMetadata(ByteArrayOutputStream baos, String key, long value) throws IOException {
        writeString(baos, key);
        writeInt32(baos, 10); // UINT64 type
        writeUInt64(baos, value);
    }
    
    private static void writeTensorInfo(ByteArrayOutputStream baos, String name, long[] dimensions, GGMLType type) throws IOException {
        writeString(baos, name);
        writeUInt32(baos, dimensions.length);
        for (long dim : dimensions) {
            writeUInt64(baos, dim);
        }
        writeInt32(baos, type.getValue());
        writeUInt64(baos, 0); // offset
    }
    
    private static void writeInt32(ByteArrayOutputStream baos, int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        baos.writeBytes(buffer.array());
    }
    
    private static void writeUInt32(ByteArrayOutputStream baos, long value) {
        writeInt32(baos, (int) value);
    }
    
    private static void writeUInt64(ByteArrayOutputStream baos, long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(value);
        baos.writeBytes(buffer.array());
    }
    
    private static void writeString(ByteArrayOutputStream baos, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeUInt64(baos, bytes.length);
        baos.writeBytes(bytes);
    }
    
    private static String formatNumber(long number) {
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
    
    private static String formatBytes(long bytes) {
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
}
