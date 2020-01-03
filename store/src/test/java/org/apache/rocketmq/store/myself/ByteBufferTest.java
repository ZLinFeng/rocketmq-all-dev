package org.apache.rocketmq.store.myself;

import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ByteBufferTest {

    @Test
    public void testByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);

        // message -> allocate 20; mark -> -1; position -> 0; limit -> 20; capacity -> 20
        printAll(byteBuffer, "allocate 20");

        for (int i = 0; i < 10; ++i) {
            byteBuffer.put((byte) i);
        }
        // message -> put 10; mark -> -1; position -> 10; limit -> 20; capacity -> 20
        printAll(byteBuffer, "put 10");

        byteBuffer.flip();
        // message -> flip; mark -> -1; position -> 0; limit -> 10; capacity -> 20
        printAll(byteBuffer, "flip");

        for (int i = 0; i < 5; ++i) {
            byteBuffer.get();
        }
        // message -> get 5; mark -> -1; position -> 5; limit -> 10; capacity -> 20
        printAll(byteBuffer, "get 5");

        byteBuffer.clear();
        // message -> clear; mark -> -1; position -> 0; limit -> 20; capacity -> 20
        printAll(byteBuffer, "clear");

        for (int i = 0; i < 10; ++i) {
            byteBuffer.put((byte) i);
        }
        // 写完之后读,和flip的区别在于limit
        byteBuffer.rewind();
        // message -> rewind; mark -> -1; position -> 0; limit -> 20; capacity -> 20
        printAll(byteBuffer, "rewind");
        for (int i = 0; i < 11; ++i) {
            System.out.println(byteBuffer.get() + ",");
        }
        // message -> get 11; mark -> -1; position -> 11; limit -> 20; capacity -> 20
        printAll(byteBuffer, "get 11");


        byteBuffer = ByteBuffer.allocate(20);

        byteBuffer.mark();
        // message -> mark; mark -> 0; position -> 0; limit -> 20; capacity -> 20
        printAll(byteBuffer, "mark");
        byteBuffer.position(10);
        printAll(byteBuffer, "position");

        byteBuffer.reset();
        printAll(byteBuffer, "reset");

    }

    private static void printAll(ByteBuffer byteBuffer, String message) {
        System.out.println("message -> " + message + "; mark -> " + mark(byteBuffer) + "; position -> " + byteBuffer.position() + "; limit -> " + byteBuffer.limit() + "; capacity -> " + byteBuffer.capacity());
    }

    private static int mark(ByteBuffer byteBuffer) {

        //mark 字段是在Buffer类: HeapByteBuffer -> ByteBuffer -> Buffer
        try {
            Field field = byteBuffer.getClass().getSuperclass().getSuperclass().getDeclaredField("mark");
            field.setAccessible(true);
            return field.getInt(byteBuffer);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -100;
    }

    private static void printBuffer(ByteBuffer byteBuffer) {
        String print = "Content is: ";
        for (int i = 0; i< byteBuffer.limit(); i++) {
            print = print + byteBuffer.get(i) + ",";
        }
        System.out.println(print);
    }

    @Test
    public void sliceTest() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        for (int i = 0; i < 10; ++i) {
            byteBuffer.put((byte) i);
        }
        byteBuffer.position(2);
        byteBuffer.limit(4);
        ByteBuffer sliceBuffer = byteBuffer.slice();
        printAll(byteBuffer, "source");
        printAll(sliceBuffer, "slice");
        System.out.println("slice data");
        printBuffer(sliceBuffer);

        // 源数据和拷贝的数据有影响  limit一致
        sliceBuffer.put(0, (byte) 100);
        System.out.print("slice -> ");
        printBuffer(sliceBuffer);
        sliceBuffer.put(1, (byte) 99);
        System.out.print("slice -> ");
        printBuffer(sliceBuffer);

        System.out.print("byteBuffer -> ");
        printBuffer(byteBuffer);
    }

    @Test
    public void duplicateTest() {

        //内容相互影响
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        for (int i = 0; i < 10; ++i) {
            byteBuffer.put((byte) i);
        }
        ByteBuffer duplicateBuffer = byteBuffer.duplicate();

        duplicateBuffer.put(0, (byte) 100);
        printBuffer(byteBuffer);
        duplicateBuffer.put(1, (byte) 101);
        printBuffer(byteBuffer);

        byteBuffer.put(0, (byte) 102);
        printBuffer(duplicateBuffer);
    }

    @Test
    public void arrayTest() {
        // byte[] 内容相互影响
    }

    @Test
    public void getTest() {
        // 不影响内容
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        for (int i = 0; i < 10; ++i) {
            byteBuffer.put((byte) i);
        }
        byteBuffer.position(2);
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        bytes[0] = (byte) -100;

        printAll(byteBuffer, "get");
        printBuffer(byteBuffer);
    }

    @Test
    public void compact() {
        // 内容相互影响
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        for (int i = 0; i < 10; ++i) {
            byteBuffer.put((byte) i);
        }
        printAll(byteBuffer, "Put");

        byteBuffer.position(3);
        ByteBuffer compactBuffer = byteBuffer.compact();

        // message -> compact; mark -> -1; position -> 7; limit -> 10; capacity -> 10
        // 7开始写
        printAll(compactBuffer, "compact");
        compactBuffer.put((byte) 100);
        printBuffer(compactBuffer);

        byteBuffer.clear();
        byteBuffer.put((byte) 101);
        printBuffer(compactBuffer);
    }

    @Test
    public void order() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        System.out.println("int len:" + intBuffer);
        printBuffer(byteBuffer);
        intBuffer.put(100);
        printBuffer(byteBuffer);
    }
}
