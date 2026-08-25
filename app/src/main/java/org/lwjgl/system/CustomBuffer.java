package org.lwjgl.system;

@SuppressWarnings("unused")
public abstract class CustomBuffer {

    private int position = 0;
    private int limit = 0;

    public int position() {
        return position;
    }

    public CustomBuffer position(int pos) {
        position = pos;
        return this;
    }

    public int limit() {
        return limit;
    }

    public CustomBuffer limit(int lim) {
        limit = lim;
        return this;
    }

    public abstract int capacity();

    public CustomBuffer flip() {
        limit = position;
        position = 0;
        return this;
    }

    public CustomBuffer clear() {
        position = 0;
        limit = capacity();
        return this;
    }
}
