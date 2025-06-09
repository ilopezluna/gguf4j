package io.github.ilopezluna.gguf4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Binary reader with support for little-endian byte order.
 * Provides methods to read various data types from an InputStream.
 */
public class BinaryReader implements AutoCloseable {
    private final InputStream inputStream;
    private final ByteOrder byteOrder;
    private long position = 0;

    public BinaryReader(InputStream inputStream) {
        this(inputStream, ByteOrder.LITTLE_ENDIAN);
    }

    public BinaryReader(InputStream inputStream, ByteOrder byteOrder) {
        this.inputStream = inputStream;
        this.byteOrder = byteOrder;
    }

    public long getPosition() {
        return position;
    }

    /**
     * Read a single byte.
     */
    public byte readByte() throws IOException {
        int b = inputStream.read();
        if (b == -1) {
            throw new IOException("Unexpected end of stream");
        }
        position++;
        return (byte) b;
    }

    /**
     * Read multiple bytes into a buffer.
     */
    public byte[] readBytes(int length) throws IOException {
        byte[] buffer = new byte[length];
        int totalRead = 0;
        while (totalRead < length) {
            int read = inputStream.read(buffer, totalRead, length - totalRead);
            if (read == -1) {
                throw new IOException("Unexpected end of stream");
            }
            totalRead += read;
        }
        position += length;
        return buffer;
    }

    /**
     * Read an unsigned 8-bit integer.
     */
    public int readUInt8() throws IOException {
        return readByte() & 0xFF;
    }

    /**
     * Read a signed 8-bit integer.
     */
    public byte readInt8() throws IOException {
        return readByte();
    }

    /**
     * Read an unsigned 16-bit integer.
     */
    public int readUInt16() throws IOException {
        byte[] bytes = readBytes(2);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        return buffer.getShort() & 0xFFFF;
    }

    /**
     * Read a signed 16-bit integer.
     */
    public short readInt16() throws IOException {
        byte[] bytes = readBytes(2);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        return buffer.getShort();
    }

    /**
     * Read an unsigned 32-bit integer.
     */
    public long readUInt32() throws IOException {
        byte[] bytes = readBytes(4);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        return buffer.getInt() & 0xFFFFFFFFL;
    }

    /**
     * Read a signed 32-bit integer.
     */
    public int readInt32() throws IOException {
        byte[] bytes = readBytes(4);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        return buffer.getInt();
    }

    /**
     * Read an unsigned 64-bit integer.
     */
    public long readUInt64() throws IOException {
        byte[] bytes = readBytes(8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        long value = buffer.getLong();
        if (value < 0) {
            throw new IOException("Unsigned 64-bit integer overflow");
        }
        return value;
    }

    /**
     * Read a signed 64-bit integer.
     */
    public long readInt64() throws IOException {
        byte[] bytes = readBytes(8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        return buffer.getLong();
    }

    /**
     * Read a 32-bit floating point number.
     */
    public float readFloat32() throws IOException {
        byte[] bytes = readBytes(4);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        return buffer.getFloat();
    }

    /**
     * Read a 64-bit floating point number.
     */
    public double readFloat64() throws IOException {
        byte[] bytes = readBytes(8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(byteOrder);
        return buffer.getDouble();
    }

    /**
     * Read a boolean value.
     */
    public boolean readBool() throws IOException {
        return readByte() != 0;
    }

    /**
     * Read a UTF-8 string with length prefix.
     */
    public String readString() throws IOException {
        long length = readUInt64();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("String too long: " + length);
        }
        byte[] bytes = readBytes((int) length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Skip a number of bytes.
     */
    public void skip(long bytes) throws IOException {
        long skipped = inputStream.skip(bytes);
        if (skipped != bytes) {
            // If skip didn't work as expected, read and discard
            long remaining = bytes - skipped;
            while (remaining > 0) {
                int toRead = (int) Math.min(remaining, 8192);
                byte[] buffer = readBytes(toRead);
                remaining -= buffer.length;
            }
        } else {
            position += bytes;
        }
    }

    /**
     * Align position to the specified boundary.
     */
    public void align(int alignment) throws IOException {
        long remainder = position % alignment;
        if (remainder != 0) {
            skip(alignment - remainder);
        }
    }

    /**
     * Close the underlying input stream.
     */
    public void close() throws IOException {
        inputStream.close();
    }
}
