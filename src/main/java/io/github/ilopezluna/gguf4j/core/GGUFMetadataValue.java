package io.github.ilopezluna.gguf4j.core;

import io.github.ilopezluna.gguf4j.scalar.GGUFMetadataValueType;

import java.util.List;

/**
 * Represents a metadata value in a GGUF file.
 */
public sealed interface GGUFMetadataValue 
    permits GGUFMetadataValue.UInt8Value, GGUFMetadataValue.Int8Value, 
            GGUFMetadataValue.UInt16Value, GGUFMetadataValue.Int16Value,
            GGUFMetadataValue.UInt32Value, GGUFMetadataValue.Int32Value,
            GGUFMetadataValue.UInt64Value, GGUFMetadataValue.Int64Value,
            GGUFMetadataValue.Float32Value, GGUFMetadataValue.Float64Value,
            GGUFMetadataValue.BoolValue, GGUFMetadataValue.StringValue,
            GGUFMetadataValue.ArrayValue {

    GGUFMetadataValueType getType();
    Object getValue();

    record UInt8Value(int value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.UINT8;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record Int8Value(byte value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.INT8;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record UInt16Value(int value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.UINT16;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record Int16Value(short value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.INT16;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record UInt32Value(long value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.UINT32;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record Int32Value(int value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.INT32;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record UInt64Value(long value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.UINT64;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record Int64Value(long value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.INT64;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record Float32Value(float value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.FLOAT32;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record Float64Value(double value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.FLOAT64;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record BoolValue(boolean value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.BOOL;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record StringValue(String value) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.STRING;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    record ArrayValue(GGUFMetadataValueType elementType, List<GGUFMetadataValue> values) implements GGUFMetadataValue {
        @Override
        public GGUFMetadataValueType getType() {
            return GGUFMetadataValueType.ARRAY;
        }

        @Override
        public Object getValue() {
            return values;
        }

        public int size() {
            return values.size();
        }

        public GGUFMetadataValue get(int index) {
            return values.get(index);
        }
    }

    /**
     * Convenience methods for type checking and casting
     */
    default boolean isString() {
        return this instanceof StringValue;
    }

    default boolean isInt32() {
        return this instanceof Int32Value;
    }

    default boolean isUInt32() {
        return this instanceof UInt32Value;
    }

    default boolean isInt64() {
        return this instanceof Int64Value;
    }

    default boolean isUInt64() {
        return this instanceof UInt64Value;
    }

    default boolean isFloat32() {
        return this instanceof Float32Value;
    }

    default boolean isFloat64() {
        return this instanceof Float64Value;
    }

    default boolean isBool() {
        return this instanceof BoolValue;
    }

    default boolean isArray() {
        return this instanceof ArrayValue;
    }

    default String asString() {
        if (this instanceof StringValue sv) {
            return sv.value();
        }
        throw new IllegalStateException("Value is not a string: " + getType());
    }

    default int asInt32() {
        if (this instanceof Int32Value iv) {
            return iv.value();
        }
        throw new IllegalStateException("Value is not an int32: " + getType());
    }

    default long asUInt32() {
        if (this instanceof UInt32Value uv) {
            return uv.value();
        }
        throw new IllegalStateException("Value is not a uint32: " + getType());
    }

    default long asInt64() {
        if (this instanceof Int64Value lv) {
            return lv.value();
        }
        throw new IllegalStateException("Value is not an int64: " + getType());
    }

    default long asUInt64() {
        if (this instanceof UInt64Value ulv) {
            return ulv.value();
        }
        throw new IllegalStateException("Value is not a uint64: " + getType());
    }

    default float asFloat32() {
        if (this instanceof Float32Value fv) {
            return fv.value();
        }
        throw new IllegalStateException("Value is not a float32: " + getType());
    }

    default double asFloat64() {
        if (this instanceof Float64Value dv) {
            return dv.value();
        }
        throw new IllegalStateException("Value is not a float64: " + getType());
    }

    default boolean asBool() {
        if (this instanceof BoolValue bv) {
            return bv.value();
        }
        throw new IllegalStateException("Value is not a boolean: " + getType());
    }

    default ArrayValue asArray() {
        if (this instanceof ArrayValue av) {
            return av;
        }
        throw new IllegalStateException("Value is not an array: " + getType());
    }
}
