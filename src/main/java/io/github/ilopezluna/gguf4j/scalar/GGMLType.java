package io.github.ilopezluna.gguf4j.scalar;

/**
 * GGML data types as defined in the GGUF specification.
 * These correspond to the tensor data types used in GGML.
 */
public enum GGMLType {
    F32(0, 4, "F32"),
    F16(1, 2, "F16"),
    Q4_0(2, 0.5f, "Q4_0"),
    Q4_1(3, 0.5f, "Q4_1"),
    Q5_0(6, 0.625f, "Q5_0"),
    Q5_1(7, 0.625f, "Q5_1"),
    Q8_0(8, 1.0f, "Q8_0"),
    Q8_1(9, 1.0f, "Q8_1"),
    Q2_K(10, 0.25f, "Q2_K"),
    Q3_K(11, 0.375f, "Q3_K"),
    Q4_K(12, 0.5f, "Q4_K"),
    Q5_K(13, 0.625f, "Q5_K"),
    Q6_K(14, 0.75f, "Q6_K"),
    Q8_K(15, 1.0f, "Q8_K"),
    IQ2_XXS(16, 0.125f, "IQ2_XXS"),
    IQ2_XS(17, 0.15625f, "IQ2_XS"),
    IQ3_XXS(18, 0.1875f, "IQ3_XXS"),
    IQ1_S(19, 0.0625f, "IQ1_S"),
    IQ4_NL(20, 0.5f, "IQ4_NL"),
    IQ3_S(21, 0.1875f, "IQ3_S"),
    IQ2_S(22, 0.125f, "IQ2_S"),
    IQ4_XS(23, 0.5f, "IQ4_XS"),
    I8(24, 1.0f, "I8"),
    I16(25, 2.0f, "I16"),
    I32(26, 4.0f, "I32"),
    I64(27, 8.0f, "I64"),
    F64(28, 8.0f, "F64"),
    IQ1_M(29, 0.0625f, "IQ1_M"),
    BF16(30, 2.0f, "BF16"),
    Q4_0_4_4(31, 0.5f, "Q4_0_4_4"),
    Q4_0_4_8(32, 0.5f, "Q4_0_4_8"),
    Q4_0_8_8(33, 0.5f, "Q4_0_8_8"),
    TQ1_0(34, 0.0625f, "TQ1_0"),
    TQ2_0(35, 0.125f, "TQ2_0");

    private final int value;
    private final float bytesPerWeight;
    private final String name;

    GGMLType(int value, float bytesPerWeight, String name) {
        this.value = value;
        this.bytesPerWeight = bytesPerWeight;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public float getBytesPerWeight() {
        return bytesPerWeight;
    }

    public String getName() {
        return name;
    }

    /**
     * Get GGML type by its numeric value.
     */
    public static GGMLType fromValue(int value) {
        for (GGMLType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown GGML type value: " + value);
    }

    /**
     * Check if this type is quantized.
     */
    public boolean isQuantized() {
        return this != F32 && this != F16 && this != F64 && this != BF16 && 
               this != I8 && this != I16 && this != I32 && this != I64;
    }

    /**
     * Get the block size for quantized types.
     */
    public int getBlockSize() {
        return switch (this) {
            case Q4_0, Q4_1, Q5_0, Q5_1, Q8_0, Q8_1 -> 32;
            case Q2_K, Q3_K, Q4_K, Q5_K, Q6_K, Q8_K -> 256;
            case IQ2_XXS, IQ2_XS, IQ3_XXS, IQ1_S, IQ4_NL, IQ3_S, IQ2_S, IQ4_XS, IQ1_M -> 32;
            case TQ1_0, TQ2_0 -> 256;
            case Q4_0_4_4, Q4_0_4_8, Q4_0_8_8 -> 32;
            default -> 1;
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
