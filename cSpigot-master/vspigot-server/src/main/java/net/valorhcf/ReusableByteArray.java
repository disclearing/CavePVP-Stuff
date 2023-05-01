package net.valorhcf;

public class ReusableByteArray {

    private final ThreadLocal<byte[]> arrays;

    public ReusableByteArray(final int initialSize) {
        arrays = new ThreadLocal<byte[]>() {
            @Override
            protected byte[] initialValue() {
                return new byte[initialSize];
            }
        };
    }

    /**
     * Returns a (thread local) byte array of at least minSize
     * @param minSize Minimum size of returned array
     * @return byte array
     */
    public byte[] get(int minSize) {
        byte[] array = arrays.get();
        if (array.length < minSize) {
            array = new byte[minSize];
            arrays.set(array);
        }
        return array;
    }
}
