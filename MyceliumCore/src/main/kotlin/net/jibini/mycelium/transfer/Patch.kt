package net.jibini.mycelium.transfer

import net.jibini.mycelium.transfer.impl.LatchedPatchImpl

/**
 * Adds a [second channel][Patch#uplink] to [Pipe<T>], allowing full duplex communication
 *
 * @see AsyncPatch<T>
 * @see Pipe<T>
 */
interface Patch<T> : Pipe<T>
{
    /**
     * Provides the "other end" of the patch; any data written to this [Pipe<T>] can be read through [Patch#pull], and
     * similarly any data written via [Patch#push] can be read through this [Pipe<T>]
     */
    fun uplink() : Pipe<T>

    companion object
    {
        @JvmStatic
        fun <T> create() = LatchedPatchImpl<T>()
    }
}

/**
 * A [Patch<T>] which allows [subscription][AsyncPatch#subscribe] to either of its ends
 *
 * @see Patch<T>
 * @see AsyncPipe<T>
 */
interface AsyncPatch<T> : Patch<T>, AsyncPipe<T>
{
    override fun uplink() : AsyncPipe<T>
}