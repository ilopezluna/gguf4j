package io.github.ilopezluna.gguf4j.core;

import io.github.ilopezluna.gguf4j.scalar.GGMLType;

import java.util.List;

/**
 * Represents the header section of a GGUF file.
 */
public record GGUFHeader(
    int magic,
    int version,
    long tensorCount,
    long metadataKvCount
) {
    
    public GGUFHeader {
        if (magic != GGUFConstants.GGUF_MAGIC) {
            throw new IllegalArgumentException("Invalid GGUF magic number: 0x" + Integer.toHexString(magic));
        }
        if (version < GGUFConstants.GGUF_VERSION_1 || version > GGUFConstants.GGUF_VERSION_3) {
            throw new IllegalArgumentException("Unsupported GGUF version: " + version);
        }
    }

    /**
     * Check if this is a valid GGUF header.
     */
    public boolean isValid() {
        return magic == GGUFConstants.GGUF_MAGIC && 
               version >= GGUFConstants.GGUF_VERSION_1 && 
               version <= GGUFConstants.GGUF_VERSION_3;
    }

    /**
     * Get the version as a string.
     */
    public String getVersionString() {
        return "v" + version;
    }
}
