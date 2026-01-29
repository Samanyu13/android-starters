package com.example.threadsafety

import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * ABOUT
 * -----
 * Unlike locks or atomics, volatile solves a different problem: Visibility.
 *
 * The problem - CPU Caching
 * Modern phones have multiple CPU cores. To make things fast, each core has its own L1 Cache.
 * If Thread A (on Core 1) changes a variable, it might keep that change in its local cache for a
 * few milliseconds before writing it to the "Main RAM." If Thread B (on Core 2) reads that same
 * variable, it might read an old value from Main RAM.
 *
 * What it does
 * Provides Visibility: It guarantees that every read of the variable comes from Main RAM, and every
 * write goes straight to Main RAM.
 *
 */
class ForceFailure {
    // Try toggling @Volatile on and off here
    @Volatile
    var keepRunning = true

    fun runTest() {
        println("Testing: Can we break it? (If this hangs, non-volatile failed to see the update)")

        val worker = thread {
            var count = 0L
            // This encourages the CPU to cache 'keepRunning' in cache.
            while (keepRunning) {
                count++
            }
            println("Worker finally stopped at count: $count")
        }

        // Give the worker 100ms to "cache" the true value
        Thread.sleep(100)

        println("Main: Setting keepRunning to false...")
        keepRunning = false

        // Wait to see if the worker ever finishes
        worker.join(3000)
        if (worker.isAlive) {
            println("FAILURE: The worker is still running! It missed the update.")
            exitProcess(0) // Force kill the stuck thread
        } else {
            println("SUCCESS: The worker stopped.")
        }
    }
}

fun main() {
    ForceFailure().runTest()
}

/**
 * Without @Volatile
 * -----------------
 * - The Worker thread started. Because the while(keepRunning) loop was so fast, the CPU core
 * running that thread decided to optimize. It moved the value of keepRunning (true) into Cache.
 * - The Worker thread stopped checking the actual RAM - basically its always true
 * - Main thread set keepRunning = false. This update was written to the Main RAM.
 * - The Worker thread's core was never told to refresh its cache and it goes on :/
 *
 * With @Volatile
 * - It tells the CPU - to not ever cache this variable in a register
 * - Every time the Worker reaches while(keepRunning), it is forced to collect the synced value
 * Main thread changes the value to false => immediately flushed to RAM
 * - The very next time the Worker checks the condition, it sees the false and breaks instantly.
 *
 */