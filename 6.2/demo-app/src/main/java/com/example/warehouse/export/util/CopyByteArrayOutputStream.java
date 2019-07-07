package com.example.warehouse.export.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class CopyByteArrayOutputStream extends ByteArrayOutputStream {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out;
    private final boolean enabled;

    public CopyByteArrayOutputStream(PrintStream out) {
        this(out, true);
    }

    public CopyByteArrayOutputStream(PrintStream copy, boolean enabled) {
        this.out = copy;
        this.enabled = enabled;
    }

    @Override
    public void write(int b) {
        if (enabled) {
            out.write(b);
        }
        super.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (enabled) {
            out.write(b);
        }
        super.write(b);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        if (enabled) {
            out.write(b, off, len);
        }
        super.write(b, off, len);
    }
}
