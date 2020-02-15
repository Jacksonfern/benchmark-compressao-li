package com.jackson.app;

import java.io.IOException;

public interface Codec{
    public abstract int[] compress(int[] data);

    public abstract int[] uncompress(int[] data);

    public abstract String getMethod();

    public abstract String getFileName();

    public abstract void write(int[] data);

    public abstract void closeWriter() throws IOException;
}