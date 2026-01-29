package com.example.threadsafety

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.concurrent.thread

/**
 * THE STANDARD WAY (AtomicInteger)
 * Easy to use, but creates a separate object for every single instance of LegacyCounter
 *
 */
class LegacyCounter {
    val count = AtomicInteger(0) // Extra object created here!

    fun increment() {
        count.incrementAndGet()
    }
}

/**
 * This is not a commonly used approach in app biz logic - just for knowledge purposes I guess
 * ---------------------------------------------------------
 * AtomicIntegerFieldUpdater
 * Performs atomic math on a primitive field. No extra objects created per instance.
 * Used in high-perf libraries like Netty or Coroutines.
 *
 */
class ProCounter {
    // The field MUST be volatile and a var
    @Volatile @JvmField var count: Int = 0

    companion object {
        // Create ONE static updater for the whole class
        private val UPDATER = AtomicIntegerFieldUpdater.newUpdater(
            ProCounter::class.java,
            "count"
        )
    }

    fun increment() {
        // Use the updater to change 'this' instance's field
        UPDATER.incrementAndGet(this)
    }
}

fun main() {
    println("--- Starting Atomic Performance Lab ---")

    // Standard AtomicInteger
    val legacy = LegacyCounter()
    val t1 = thread { repeat(1000) { legacy.increment() } }
    val t2 = thread { repeat(1000) { legacy.increment() } }
    t1.join(); t2.join()
    println("Legacy Result: ${legacy.count.get()}")

    // FieldUpdater
    val pro = ProCounter()
    val t3 = thread { repeat(1000) { pro.increment() } }
    val t4 = thread { repeat(1000) { pro.increment() } }
    t3.join(); t4.join()
    println("Pro Result: ${pro.count}")
}