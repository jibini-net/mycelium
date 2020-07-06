package net.jibini.mycelium.transfer

import net.jibini.mycelium.transfer.impl.AsyncPipeImpl
import net.jibini.mycelium.transfer.impl.LatchedPipeImpl

/**
 * Allows objects of type T to be [pushed][Pipe#push] in one end and [pulled][Pipe#pull] out the other in a simplex
 * fashion
 *
 * @see AsyncPipe<T>
 * @see Patch<T>
 */
interface Pipe<T>
{
    /**
     * Inserts the given value into the pipe and alerts the receiving end that a new value is available; hangs until a
     * member or [subscribed callback][AsyncPipe#subscribe] has [pulled][Pipe#pull] any pending written values (unless
     * the pending written values are buffered)
     */
    fun push(value : T)

    /**
     * Hangs until a value is ready to be received through the pipe; if the pipe is asynchronous, it is not recommended
     * to use this method as all written values are consumed by [subscribed callbacks][AsyncPipe#subscribe]
     */
    fun pull() : T

    companion object
    {
        @JvmStatic
        fun <T> create() = LatchedPipeImpl<T>()

        @JvmStatic
        fun <T> createAsync(origin : Pipe<T>) = AsyncPipeImpl(origin)

        @JvmStatic
        fun <T> createAsync() = createAsync<T>(create())
    }
}

/**
 * Allows listening for data on a pipe without blocking execution; invokes listeners on a separate thread when data is
 * available over the pipe
 *
 * @see Pipe<T>
 * @see AsyncPatch<T>
 */
interface AsyncPipe<T> : Pipe<T>
{
    /**
     * Registers a callback which is invoked on a separate thread upon receiving any data through the pipe; if any
     * callbacks are subscribed, it is not recommended to [listen synchronously][Pipe#pull] for data
     */
    fun subscribe(callback : (T) -> Unit)
}