package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.Pipe
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class LatchedPipeImpl<T> : Pipe<T>
{
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private var has : Boolean = false
    private var value : T? = null

    override fun push(value: T)
    {
        lock.withLock {
            condition.signalAll()
            while (has) condition.await()

            this.value = value
            this.has = true
            condition.signalAll()
        }
    }

    override fun pull(): T
    {
        lock.withLock {
            condition.signalAll()
            while (!has) condition.await()

            this.has = false
        }

        return value!!
    }
}