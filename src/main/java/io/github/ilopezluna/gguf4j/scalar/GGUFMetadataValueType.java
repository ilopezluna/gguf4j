package io.github.ilopezluna.gguf4j.scalar;

/**
 * GGUF metadata value types as defined in the GGUF specification.
 */
public enum GGUFMetadataValueType {
    UINT8(0, 1),
    INT8(1, 1),
    UINT16(2, 2),
    INT16(3, 2),
    UINT32(4, 4),
    INT32(5, 4),
    FLOAT32(6, 4),
    BOOL(7, 1),
    STRING(8, -1), // Variable length
    ARRAY(9, -1),  // Variable length
    UINT64(10, 8),
    INT64(11, 8),
    FLOAT64(12, 8);

    private final int value;
    private final int size; // -1 for variable length

    GGUFMetadataValueType(int value, int size) {
        this.value = value;
        this.size = size;
    }

    public int getValue() {
        return value;
    }

    public int getSize() {
        return size;
    }

    public boolean isVariableLength() {
        return size == -1;
    }

    /**
     * Get metadata value type by its numeric value.
     */
    public static GGUFMetadataValueType fromValue(int value) {
        for (GGUFMetadataValueType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown GGUF metadata value type: " + value);
    }

    @Override
    public String toString() {
        return name();
    }
}
