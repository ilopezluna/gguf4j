package io.github.ilopezluna.gguf4j.core;

import io.github.ilopezluna.gguf4j.scalar.GGMLType;
import io.github.ilopezluna.gguf4j.scalar.GGUFMetadataValueType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the GGUF parser.
 */
class GGUFParserTest {

    @Test
    void testParseSimpleGGUFFile() throws IOException {
        // Create a minimal GGUF file in memory
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Write header
        writeInt32(baos, GGUFConstants.GGUF_MAGIC);  // magic
        writeInt32(baos, GGUFConstants.GGUF_VERSION_3);  // version
        writeUInt64(baos, 1);  // tensor count
        writeUInt64(baos, 2);  // metadata count
        
        // Write metadata
        // First metadata entry: general.architecture = "test"
        writeString(baos, GGUFConstants.GENERAL_ARCHITECTURE);
        writeInt32(baos, GGUFMetadataValueType.STRING.getValue());
        writeString(baos, "test");
        
        // Second metadata entry: general.name = "test-model"
        writeString(baos, GGUFConstants.GENERAL_NAME);
        writeInt32(baos, GGUFMetadataValueType.STRING.getValue());
        writeString(baos, "test-model");
        
        // Write tensor info
        writeString(baos, "test.weight");  // tensor name
        writeUInt32(baos, 2);  // dimensions count
        writeUInt64(baos, 10);  // dim 0
        writeUInt64(baos, 20);  // dim 1
        writeInt32(baos, GGMLType.F32.getValue());  // tensor type
        writeUInt64(baos, 0);  // offset
        
        // Align to 32 bytes (tensor data would start here)
        while (baos.size() % 32 != 0) {
            baos.write(0);
        }
        
        byte[] data = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        // Parse the file
        GGUFFile ggufFile = GGUFParser.parse(bais);
        
        // Verify the results
        assertThat(ggufFile.getVersion()).isEqualTo(GGUFConstants.GGUF_VERSION_3);
        assertThat(ggufFile.getTensorCount()).isEqualTo(1);
        assertThat(ggufFile.getMetadataCount()).isEqualTo(2);
        
        assertThat(ggufFile.getArchitecture()).contains("test");
        assertThat(ggufFile.getName()).contains("test-model");
        
        assertThat(ggufFile.tensors()).hasSize(1);
        GGUFTensorInfo tensor = ggufFile.tensors().get(0);
        assertThat(tensor.name()).isEqualTo("test.weight");
        assertThat(tensor.type()).isEqualTo(GGMLType.F32);
        assertThat(tensor.dimensions()).containsExactly(10, 20);
        assertThat(tensor.getElementCount()).isEqualTo(200);
        assertThat(tensor.getSizeInBytes()).isEqualTo(800); // 200 * 4 bytes
    }

    @Test
    void testInvalidMagicNumber() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeInt32(baos, 0x12345678);  // invalid magic
        writeInt32(baos, GGUFConstants.GGUF_VERSION_3);
        
        byte[] data = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        assertThatThrownBy(() -> GGUFParser.parse(bais))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Unexpected end of stream");
    }

    @Test
    void testIsValidGGUFFile() {
        // Valid file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeInt32(baos, GGUFConstants.GGUF_MAGIC);
        writeInt32(baos, GGUFConstants.GGUF_VERSION_3);
        
        byte[] data = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        assertThat(GGUFParser.isValidGGUFFile(bais)).isTrue();
        
        // Invalid file
        baos = new ByteArrayOutputStream();
        writeInt32(baos, 0x12345678);
        writeInt32(baos, GGUFConstants.GGUF_VERSION_3);
        
        data = baos.toByteArray();
        bais = new ByteArrayInputStream(data);
        
        assertThat(GGUFParser.isValidGGUFFile(bais)).isFalse();
    }

    @Test
    void testGetVersion() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeInt32(baos, GGUFConstants.GGUF_MAGIC);
        writeInt32(baos, GGUFConstants.GGUF_VERSION_2);
        
        byte[] data = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        
        assertThat(GGUFParser.getVersion(bais)).isEqualTo(GGUFConstants.GGUF_VERSION_2);
    }

    // Helper methods for writing binary data
    private void writeInt32(ByteArrayOutputStream baos, int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        baos.writeBytes(buffer.array());
    }

    private void writeUInt32(ByteArrayOutputStream baos, long value) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt((int) value);
        baos.writeBytes(buffer.array());
    }

    private void writeUInt64(ByteArrayOutputStream baos, long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(value);
        baos.writeBytes(buffer.array());
    }

    private void writeString(ByteArrayOutputStream baos, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeUInt64(baos, bytes.length);
        baos.writeBytes(bytes);
    }
}
