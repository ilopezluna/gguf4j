package io.github.ilopezluna.gguf4j.core;

import java.util.List;
import java.util.Optional;

/**
 * Represents a parsed GGUF file with all its components.
 */
public record GGUFFile(
    GGUFHeader header,
    GGUFMetadata metadata,
    List<GGUFTensorInfo> tensors,
    long tensorDataOffset
) {
    
    public GGUFFile {
        if (header == null) {
            throw new IllegalArgumentException("Header cannot be null");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Metadata cannot be null");
        }
        if (tensors == null) {
            throw new IllegalArgumentException("Tensors list cannot be null");
        }
        if (tensors.size() != header.tensorCount()) {
            throw new IllegalArgumentException(
                String.format("Tensor count mismatch: header says %d, but got %d tensors", 
                    header.tensorCount(), tensors.size()));
        }
    }

    /**
     * Get the GGUF version.
     */
    public int getVersion() {
        return header.version();
    }

    /**
     * Get the number of tensors.
     */
    public long getTensorCount() {
        return header.tensorCount();
    }

    /**
     * Get the number of metadata entries.
     */
    public long getMetadataCount() {
        return header.metadataKvCount();
    }

    /**
     * Get the model architecture.
     */
    public Optional<String> getArchitecture() {
        return metadata.getArchitecture();
    }

    /**
     * Get the model name.
     */
    public Optional<String> getName() {
        return metadata.getName();
    }

    /**
     * Get the file type (quantization).
     */
    public Optional<Long> getFileType() {
        return metadata.getFileType();
    }

    /**
     * Get the total size of all tensors in bytes.
     */
    public long getTotalTensorSize() {
        return tensors.stream()
                .mapToLong(GGUFTensorInfo::getSizeInBytes)
                .sum();
    }

    /**
     * Get the total number of parameters (sum of all tensor elements).
     */
    public long getTotalParameters() {
        return tensors.stream()
                .mapToLong(GGUFTensorInfo::getElementCount)
                .sum();
    }

    /**
     * Calculate the average bits per weight.
     */
    public double getAverageBitsPerWeight() {
        long totalElements = getTotalParameters();
        if (totalElements == 0) {
            return 0.0;
        }
        long totalBytes = getTotalTensorSize();
        return (totalBytes * 8.0) / totalElements;
    }

    /**
     * Get context length for the model's architecture.
     */
    public Optional<Long> getContextLength() {
        return getArchitecture()
                .flatMap(arch -> metadata.getContextLength(arch));
    }

    /**
     * Get embedding length for the model's architecture.
     */
    public Optional<Long> getEmbeddingLength() {
        return getArchitecture()
                .flatMap(arch -> metadata.getEmbeddingLength(arch));
    }

    /**
     * Get block count (number of layers) for the model's architecture.
     */
    public Optional<Long> getBlockCount() {
        return getArchitecture()
                .flatMap(arch -> metadata.getBlockCount(arch));
    }

    /**
     * Get feed forward length for the model's architecture.
     */
    public Optional<Long> getFeedForwardLength() {
        return getArchitecture()
                .flatMap(arch -> metadata.getFeedForwardLength(arch));
    }

    /**
     * Get attention head count for the model's architecture.
     */
    public Optional<Long> getAttentionHeadCount() {
        return getArchitecture()
                .flatMap(arch -> metadata.getAttentionHeadCount(arch));
    }

    /**
     * Get attention head count for key-value for the model's architecture.
     */
    public Optional<Long> getAttentionHeadCountKV() {
        return getArchitecture()
                .flatMap(arch -> metadata.getAttentionHeadCountKV(arch));
    }

    /**
     * Get tokenizer model.
     */
    public Optional<String> getTokenizerModel() {
        return metadata.getTokenizerModel();
    }

    /**
     * Get BOS token ID.
     */
    public Optional<Long> getBosTokenId() {
        return metadata.getBosTokenId();
    }

    /**
     * Get EOS token ID.
     */
    public Optional<Long> getEosTokenId() {
        return metadata.getEosTokenId();
    }

    /**
     * Get unknown token ID.
     */
    public Optional<Long> getUnkTokenId() {
        return metadata.getUnkTokenId();
    }

    /**
     * Get padding token ID.
     */
    public Optional<Long> getPadTokenId() {
        return metadata.getPadTokenId();
    }

    /**
     * Find tensors by name pattern.
     */
    public List<GGUFTensorInfo> findTensors(String namePattern) {
        return tensors.stream()
                .filter(tensor -> tensor.name().matches(namePattern))
                .toList();
    }

    /**
     * Find a tensor by exact name.
     */
    public Optional<GGUFTensorInfo> findTensor(String name) {
        return tensors.stream()
                .filter(tensor -> tensor.name().equals(name))
                .findFirst();
    }

    /**
     * Get tensors by layer number.
     */
    public List<GGUFTensorInfo> getTensorsByLayer(int layerNumber) {
        return tensors.stream()
                .filter(tensor -> tensor.getLayerNumber() == layerNumber)
                .toList();
    }

    /**
     * Get all weight tensors.
     */
    public List<GGUFTensorInfo> getWeightTensors() {
        return tensors.stream()
                .filter(GGUFTensorInfo::isWeight)
                .toList();
    }

    /**
     * Get all bias tensors.
     */
    public List<GGUFTensorInfo> getBiasTensors() {
        return tensors.stream()
                .filter(GGUFTensorInfo::isBias)
                .toList();
    }

    /**
     * Check if this is a valid GGUF file.
     */
    public boolean isValid() {
        return header.isValid() && 
               tensors.size() == header.tensorCount() &&
               metadata.size() == header.metadataKvCount();
    }

    /**
     * Get a summary string of the file.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("GGUF File Summary:\n");
        sb.append("  Version: ").append(header.getVersionString()).append("\n");
        sb.append("  Architecture: ").append(getArchitecture().orElse("Unknown")).append("\n");
        sb.append("  Name: ").append(getName().orElse("Unknown")).append("\n");
        sb.append("  Tensors: ").append(getTensorCount()).append("\n");
        sb.append("  Parameters: ").append(getTotalParameters()).append("\n");
        sb.append("  Size: ").append(String.format("%.2f MB", getTotalTensorSize() / (1024.0 * 1024.0))).append("\n");
        sb.append("  Avg BPW: ").append(String.format("%.2f", getAverageBitsPerWeight())).append("\n");
        getContextLength().ifPresent(ctx -> sb.append("  Context Length: ").append(ctx).append("\n"));
        getBlockCount().ifPresent(blocks -> sb.append("  Layers: ").append(blocks).append("\n"));
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("GGUFFile{version=%s, architecture=%s, tensors=%d, parameters=%d}",
                header.getVersionString(),
                getArchitecture().orElse("Unknown"),
                getTensorCount(),
                getTotalParameters());
    }
}
