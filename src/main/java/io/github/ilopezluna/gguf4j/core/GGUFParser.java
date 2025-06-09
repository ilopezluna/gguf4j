package io.github.ilopezluna.gguf4j.core;

import io.github.ilopezluna.gguf4j.io.BinaryReader;
import io.github.ilopezluna.gguf4j.scalar.GGMLType;
import io.github.ilopezluna.gguf4j.scalar.GGUFMetadataValueType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for GGUF files.
 */
public class GGUFParser {

    /**
     * Parse a GGUF file from a file path.
     */
    public static GGUFFile parse(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return parse(inputStream);
        }
    }

    /**
     * Parse a GGUF file from an InputStream.
     */
    public static GGUFFile parse(InputStream inputStream) throws IOException {
        try (BinaryReader reader = new BinaryReader(inputStream)) {
            return parse(reader);
        }
    }

    /**
     * Parse a GGUF file from a BinaryReader.
     */
    public static GGUFFile parse(BinaryReader reader) throws IOException {
        // Read header
        GGUFHeader header = parseHeader(reader);
        
        // Read metadata
        GGUFMetadata metadata = parseMetadata(reader, header.metadataKvCount());
        
        // Read tensor information
        List<GGUFTensorInfo> tensors = parseTensorInfo(reader, header.tensorCount());
        
        // Calculate tensor data offset (aligned to DEFAULT_ALIGNMENT)
        reader.align(GGUFConstants.DEFAULT_ALIGNMENT);
        long tensorDataOffset = reader.getPosition();
        
        return new GGUFFile(header, metadata, tensors, tensorDataOffset);
    }

    /**
     * Parse only the header and metadata (useful for quick inspection without reading tensor info).
     */
    public static GGUFFile parseHeaderAndMetadata(InputStream inputStream) throws IOException {
        try (BinaryReader reader = new BinaryReader(inputStream)) {
            // Read header
            GGUFHeader header = parseHeader(reader);
            
            // Read metadata
            GGUFMetadata metadata = parseMetadata(reader, header.metadataKvCount());
            
            // Create empty tensor list and dummy offset for quick parsing
            List<GGUFTensorInfo> emptyTensors = new ArrayList<>();
            
            // Note: This creates an invalid GGUFFile (tensor count mismatch)
            // but is useful for quick metadata inspection
            return new GGUFFile(
                new GGUFHeader(header.magic(), header.version(), 0, header.metadataKvCount()),
                metadata, 
                emptyTensors, 
                0
            );
        }
    }

    private static GGUFHeader parseHeader(BinaryReader reader) throws IOException {
        int magic = reader.readInt32();
        int version = reader.readInt32();
        long tensorCount = reader.readUInt64();
        long metadataKvCount = reader.readUInt64();
        
        return new GGUFHeader(magic, version, tensorCount, metadataKvCount);
    }

    private static GGUFMetadata parseMetadata(BinaryReader reader, long count) throws IOException {
        Map<String, GGUFMetadataValue> values = new HashMap<>();
        
        for (long i = 0; i < count; i++) {
            // Read key
            String key = reader.readString();
            
            // Read value
            GGUFMetadataValue value = parseMetadataValue(reader);
            
            values.put(key, value);
        }
        
        return new GGUFMetadata(values);
    }

    private static GGUFMetadataValue parseMetadataValue(BinaryReader reader) throws IOException {
        int typeValue = reader.readInt32();
        GGUFMetadataValueType type = GGUFMetadataValueType.fromValue(typeValue);
        
        return switch (type) {
            case UINT8 -> new GGUFMetadataValue.UInt8Value(reader.readUInt8());
            case INT8 -> new GGUFMetadataValue.Int8Value(reader.readInt8());
            case UINT16 -> new GGUFMetadataValue.UInt16Value(reader.readUInt16());
            case INT16 -> new GGUFMetadataValue.Int16Value(reader.readInt16());
            case UINT32 -> new GGUFMetadataValue.UInt32Value(reader.readUInt32());
            case INT32 -> new GGUFMetadataValue.Int32Value(reader.readInt32());
            case UINT64 -> new GGUFMetadataValue.UInt64Value(reader.readUInt64());
            case INT64 -> new GGUFMetadataValue.Int64Value(reader.readInt64());
            case FLOAT32 -> new GGUFMetadataValue.Float32Value(reader.readFloat32());
            case FLOAT64 -> new GGUFMetadataValue.Float64Value(reader.readFloat64());
            case BOOL -> new GGUFMetadataValue.BoolValue(reader.readBool());
            case STRING -> new GGUFMetadataValue.StringValue(reader.readString());
            case ARRAY -> parseArrayValue(reader);
        };
    }

    private static GGUFMetadataValue.ArrayValue parseArrayValue(BinaryReader reader) throws IOException {
        // Read element type
        int elementTypeValue = reader.readInt32();
        GGUFMetadataValueType elementType = GGUFMetadataValueType.fromValue(elementTypeValue);
        
        // Read array length
        long arrayLength = reader.readUInt64();
        if (arrayLength > Integer.MAX_VALUE) {
            throw new IOException("Array too large: " + arrayLength);
        }
        
        List<GGUFMetadataValue> elements = new ArrayList<>((int) arrayLength);
        
        // Read elements
        for (long i = 0; i < arrayLength; i++) {
            GGUFMetadataValue element = parseMetadataValueOfType(reader, elementType);
            elements.add(element);
        }
        
        return new GGUFMetadataValue.ArrayValue(elementType, elements);
    }

    private static GGUFMetadataValue parseMetadataValueOfType(BinaryReader reader, GGUFMetadataValueType type) throws IOException {
        return switch (type) {
            case UINT8 -> new GGUFMetadataValue.UInt8Value(reader.readUInt8());
            case INT8 -> new GGUFMetadataValue.Int8Value(reader.readInt8());
            case UINT16 -> new GGUFMetadataValue.UInt16Value(reader.readUInt16());
            case INT16 -> new GGUFMetadataValue.Int16Value(reader.readInt16());
            case UINT32 -> new GGUFMetadataValue.UInt32Value(reader.readUInt32());
            case INT32 -> new GGUFMetadataValue.Int32Value(reader.readInt32());
            case UINT64 -> new GGUFMetadataValue.UInt64Value(reader.readUInt64());
            case INT64 -> new GGUFMetadataValue.Int64Value(reader.readInt64());
            case FLOAT32 -> new GGUFMetadataValue.Float32Value(reader.readFloat32());
            case FLOAT64 -> new GGUFMetadataValue.Float64Value(reader.readFloat64());
            case BOOL -> new GGUFMetadataValue.BoolValue(reader.readBool());
            case STRING -> new GGUFMetadataValue.StringValue(reader.readString());
            case ARRAY -> throw new IOException("Nested arrays are not supported");
        };
    }

    private static List<GGUFTensorInfo> parseTensorInfo(BinaryReader reader, long count) throws IOException {
        List<GGUFTensorInfo> tensors = new ArrayList<>();
        
        for (long i = 0; i < count; i++) {
            // Read tensor name
            String name = reader.readString();
            
            // Read number of dimensions
            long nDims = reader.readUInt32();
            if (nDims > Integer.MAX_VALUE) {
                throw new IOException("Too many dimensions: " + nDims);
            }
            
            // Read dimensions
            long[] dimensions = new long[(int) nDims];
            for (int j = 0; j < nDims; j++) {
                dimensions[j] = reader.readUInt64();
            }
            
            // Read tensor type
            int typeValue = reader.readInt32();
            GGMLType type = GGMLType.fromValue(typeValue);
            
            // Read tensor offset
            long offset = reader.readUInt64();
            
            tensors.add(new GGUFTensorInfo(name, dimensions, type, offset));
        }
        
        return tensors;
    }

    /**
     * Validate that a stream contains a valid GGUF file header.
     */
    public static boolean isValidGGUFFile(InputStream inputStream) {
        try (BinaryReader reader = new BinaryReader(inputStream)) {
            int magic = reader.readInt32();
            int version = reader.readInt32();
            
            return magic == GGUFConstants.GGUF_MAGIC && 
                   version >= GGUFConstants.GGUF_VERSION_1 && 
                   version <= GGUFConstants.GGUF_VERSION_3;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get the GGUF version from a stream without fully parsing.
     */
    public static int getVersion(InputStream inputStream) throws IOException {
        try (BinaryReader reader = new BinaryReader(inputStream)) {
            int magic = reader.readInt32();
            if (magic != GGUFConstants.GGUF_MAGIC) {
                throw new IOException("Not a valid GGUF file");
            }
            return reader.readInt32();
        }
    }
}
