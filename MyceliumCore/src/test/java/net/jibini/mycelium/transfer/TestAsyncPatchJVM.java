package net.jibini.mycelium.transfer;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

public class TestAsyncPatchJVM
{
    String value = null;

    @Test
    public void patchAsyncCallbacksUpstreamInJVM() throws InterruptedException
    {
        AsyncPatch<String> patch = Patch.createAsync();
        CountDownLatch latch = new CountDownLatch(1);

        patch.getUplink().subscribe(message ->
        {
            value = message;
            latch.countDown();
            return null;
        });

        patch.push("Hello, world!");
        
        latch.await();
        assertEquals("Hello, world!", value);
    }

    @Test
    public void patchAsyncCallbacksDownstreamInJVM() throws InterruptedException
    {
        AsyncPatch<String> patch = Patch.createAsync();
        CountDownLatch latch = new CountDownLatch(1);

        patch.subscribe(message ->
        {
            value = message;
            latch.countDown();
            return null;
        });

        patch.getUplink().push("Hello, world!");

        latch.await();
        assertEquals("Hello, world!", value);
    }
}
