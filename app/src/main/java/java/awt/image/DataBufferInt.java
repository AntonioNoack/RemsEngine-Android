package java.awt.image;

public class DataBufferInt extends DataBuffer {

    int[] data;

    public int[] getData() {
        return data;
    }

    @Override
    public void setElem(int index, int value) {
        data[index] = value;
    }
}
