package org.apache.rocketmq.store.myself;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedByteBufferTest {

    @Test
    public void testMapped() {
        File file = new File("/Users/zhulinfeng/Desktop/IdeaJProject/rocketmq-rocketmq-all-4.2.0/store/src/test/java/org/apache/rocketmq/store/myself/test");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel fileChannel = fileInputStream.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024);
            byte[] writeData = new byte[4];
            mappedByteBuffer.put(writeData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static abstract class WriteAndRead {
        private String name;

        public WriteAndRead(String name) {
            this.name = name;
        }

        public void run() {
            System.out.println("Run method: " + name);
            long startTime = System.currentTimeMillis();
            runInner();
            System.out.println("Use time: " + (System.currentTimeMillis() - startTime));
        }

        public abstract void runInner();
    }

    @Test
    public void runTest() {
        WriteAndRead[] runners = {
                new WriteAndRead("Stream") {
                    @Override
                    public void runInner() {

                    }
                },
                new WriteAndRead("Mapped") {
                    @Override
                    public void runInner() {

                    }
                }
        };
        for (WriteAndRead writeAndRead : runners) {
            writeAndRead.run();
        }
    }
}
