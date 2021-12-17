package nl.tritewolf.tritejection.utils;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

final class ClassFileBuffer implements DataInput {
    
    private byte[] buffer;
    private int size;
    private int pointer;

    ClassFileBuffer() {
        this(8 * 1024);
    }

    ClassFileBuffer(final int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity < 1: " + initialCapacity);
        }
        this.buffer = new byte[initialCapacity];
    }

    public void readFrom(final InputStream in) throws IOException {
        pointer = 0;
        size = 0;
        int n;
        do {
            n = in.read(buffer, size, buffer.length - size);
            if (n > 0) {
                size += n;
            }
            resizeIfNeeded();
        } while (n >= 0);
    }

    public void seek(final int position) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException("position < 0: " + position);
        }
        if (position > size) {
            throw new EOFException();
        }
        this.pointer = position;
    }

    public int size() {
        return size;
    }

    @Override
    public void readFully(final byte[] bytes) throws IOException {
        readFully(bytes, 0, bytes.length);
    }

    @Override
    public void readFully(final byte[] bytes, final int offset, final int length)
        throws IOException {

        if (length < 0 || offset < 0 || offset + length > bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        if (pointer + length > size) {
            throw new EOFException();
        }
        System.arraycopy(buffer, pointer, bytes, offset, length);
        pointer += length;
    }

    @Override
    public int skipBytes(final int n) throws IOException {
        seek(pointer + n);
        return n;
    }

    @Override
    public byte readByte() throws IOException {
        if (pointer >= size) {
            throw new EOFException();
        }
        return buffer[pointer++];
    }

    @Override
    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        if (pointer >= size) {
            throw new EOFException();
        }
        return read();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        if (pointer + 2 > size) {
            throw new EOFException();
        }
        return (read() << 8) + read();
    }

    @Override
    public short readShort() throws IOException {
        return (short)readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return (char)readUnsignedShort();
    }

    @Override
    public int readInt() throws IOException {
        if (pointer + 4 > size) {
            throw new EOFException();
        }
        return (read() << 24) +
            (read() << 16) +
            (read() << 8) +
            read();
    }

    @Override
    public long readLong() throws IOException {
        if (pointer + 8 > size) {
            throw new EOFException();
        }
        return ((long)read() << 56) +
            ((long)read() << 48) +
            ((long)read() << 40) +
            ((long)read() << 32) +
            (read() << 24) +
            (read() << 16) +
            (read() << 8) +
            read();
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        throw new UnsupportedOperationException("readLine() is deprecated and not supported");
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    private int read() {
        return buffer[pointer++] & 0xff;
    }

    private void resizeIfNeeded() {
        if (size >= buffer.length) {
            final byte[] newBuffer = new byte[buffer.length * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
    }

}
