package net.jibini.mycelium.transfer;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

public class TestAsyncPipeJVM
{
    String value = null;

    @Test
    public void pipeAsyncCallbacksInJVM() throws InterruptedException
    {
        AsyncPipe<String> pipe = Pipe.createAsync();
        CountDownLatch latch = new CountDownLatch(1);

        pipe.subscribe(message ->
        {
            value = message;
            latch.countDown();
            return null;
        });

        pipe.push("Hello, world!");

        latch.await();
        assertEquals("Hello, world!", value);
    }
}
