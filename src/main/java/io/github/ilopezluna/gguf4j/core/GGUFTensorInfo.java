package io.github.ilopezluna.gguf4j.core;

import io.github.ilopezluna.gguf4j.scalar.GGMLType;

import java.util.Arrays;

/**
 * Represents tensor information in a GGUF file.
 */
public record GGUFTensorInfo(
    String name,
    long[] dimensions,
    GGMLType type,
    long offset
) {
    
    public GGUFTensorInfo {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tensor name cannot be null or empty");
        }
        if (dimensions == null || dimensions.length == 0) {
            throw new IllegalArgumentException("Tensor dimensions cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Tensor type cannot be null");
        }
    }

    /**
     * Get the number of dimensions.
     */
    public int getDimensionCount() {
        return dimensions.length;
    }

    /**
     * Get the total number of elements in the tensor.
     */
    public long getElementCount() {
        long count = 1;
        for (long dim : dimensions) {
            count *= dim;
        }
        return count;
    }

    /**
     * Get the size of the tensor in bytes.
     */
    public long getSizeInBytes() {
        long elementCount = getElementCount();
        if (type.isQuantized()) {
            // For quantized types, calculate based on block size
            int blockSize = type.getBlockSize();
            long blockCount = (elementCount + blockSize - 1) / blockSize;
            return (long) (blockCount * type.getBytesPerWeight() * blockSize);
        } else {
            return (long) (elementCount * type.getBytesPerWeight());
        }
    }

    /**
     * Get a string representation of the dimensions.
     */
    public String getDimensionsString() {
        return Arrays.toString(dimensions);
    }

    /**
     * Check if this tensor is a weight tensor (typically ends with .weight).
     */
    public boolean isWeight() {
        return name.endsWith(".weight");
    }

    /**
     * Check if this tensor is a bias tensor (typically ends with .bias).
     */
    public boolean isBias() {
        return name.endsWith(".bias");
    }

    /**
     * Get the layer number from the tensor name if it follows the pattern.
     * Returns -1 if no layer number can be extracted.
     */
    public int getLayerNumber() {
        // Try to extract layer number from patterns like "blk.0.attn_q.weight"
        String[] parts = name.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("blk".equals(parts[i]) && i + 1 < parts.length) {
                try {
                    return Integer.parseInt(parts[i + 1]);
                } catch (NumberFormatException e) {
                    // Continue searching
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return String.format("GGUFTensorInfo{name='%s', dimensions=%s, type=%s, offset=%d, size=%d bytes}",
                name, getDimensionsString(), type, offset, getSizeInBytes());
    }
}
