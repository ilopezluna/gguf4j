package io.github.ilopezluna.gguf4j.core;

import java.util.Map;
import java.util.Optional;

/**
 * Container for GGUF metadata key-value pairs.
 */
public record GGUFMetadata(Map<String, GGUFMetadataValue> values) {
    
    public GGUFMetadata {
        if (values == null) {
            throw new IllegalArgumentException("Metadata values cannot be null");
        }
    }

    /**
     * Get a metadata value by key.
     */
    public Optional<GGUFMetadataValue> get(String key) {
        return Optional.ofNullable(values.get(key));
    }

    /**
     * Check if a key exists in the metadata.
     */
    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    /**
     * Get the number of metadata entries.
     */
    public int size() {
        return values.size();
    }

    /**
     * Get all metadata keys.
     */
    public java.util.Set<String> keySet() {
        return values.keySet();
    }

    // Convenience methods for common metadata values

    /**
     * Get the model architecture.
     */
    public Optional<String> getArchitecture() {
        return get(GGUFConstants.GENERAL_ARCHITECTURE)
                .filter(GGUFMetadataValue::isString)
                .map(GGUFMetadataValue::asString);
    }

    /**
     * Get the model name.
     */
    public Optional<String> getName() {
        return get(GGUFConstants.GENERAL_NAME)
                .filter(GGUFMetadataValue::isString)
                .map(GGUFMetadataValue::asString);
    }

    /**
     * Get the file type (quantization).
     */
    public Optional<Long> getFileType() {
        return get(GGUFConstants.GENERAL_FILE_TYPE)
                .filter(v -> v.isUInt32() || v.isInt32())
                .map(v -> v.isUInt32() ? v.asUInt32() : (long) v.asInt32());
    }

    /**
     * Get context length for a specific architecture.
     */
    public Optional<Long> getContextLength(String architecture) {
        String key = architecture + GGUFConstants.CONTEXT_LENGTH;
        return get(key)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get embedding length for a specific architecture.
     */
    public Optional<Long> getEmbeddingLength(String architecture) {
        String key = architecture + GGUFConstants.EMBEDDING_LENGTH;
        return get(key)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get block count (number of layers) for a specific architecture.
     */
    public Optional<Long> getBlockCount(String architecture) {
        String key = architecture + GGUFConstants.BLOCK_COUNT;
        return get(key)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get feed forward length for a specific architecture.
     */
    public Optional<Long> getFeedForwardLength(String architecture) {
        String key = architecture + GGUFConstants.FEED_FORWARD_LENGTH;
        return get(key)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get attention head count for a specific architecture.
     */
    public Optional<Long> getAttentionHeadCount(String architecture) {
        String key = architecture + GGUFConstants.ATTENTION_HEAD_COUNT;
        return get(key)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get attention head count for key-value for a specific architecture.
     */
    public Optional<Long> getAttentionHeadCountKV(String architecture) {
        String key = architecture + GGUFConstants.ATTENTION_HEAD_COUNT_KV;
        return get(key)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get tokenizer model.
     */
    public Optional<String> getTokenizerModel() {
        return get(GGUFConstants.TOKENIZER_MODEL)
                .filter(GGUFMetadataValue::isString)
                .map(GGUFMetadataValue::asString);
    }

    /**
     * Get BOS token ID.
     */
    public Optional<Long> getBosTokenId() {
        return get(GGUFConstants.TOKENIZER_BOS_TOKEN_ID)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get EOS token ID.
     */
    public Optional<Long> getEosTokenId() {
        return get(GGUFConstants.TOKENIZER_EOS_TOKEN_ID)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get unknown token ID.
     */
    public Optional<Long> getUnkTokenId() {
        return get(GGUFConstants.TOKENIZER_UNK_TOKEN_ID)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    /**
     * Get padding token ID.
     */
    public Optional<Long> getPadTokenId() {
        return get(GGUFConstants.TOKENIZER_PAD_TOKEN_ID)
                .filter(v -> v.isUInt32() || v.isInt32() || v.isUInt64() || v.isInt64())
                .map(v -> {
                    if (v.isUInt32()) return v.asUInt32();
                    if (v.isInt32()) return (long) v.asInt32();
                    if (v.isUInt64()) return v.asUInt64();
                    return v.asInt64();
                });
    }

    @Override
    public String toString() {
        return String.format("GGUFMetadata{%d entries}", values.size());
    }
}
