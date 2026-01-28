package com.example.threadsafety

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

/**
 * ABOUT
 * -----
 * Mutex stands for Mutual Exclusion
 * It is pessimistic in nature, assumes a collision to happen, and locks
 * However, it is non-blocking, i.e, suspending - it just goes to sleep and waits for
 * the Mutex to wake it up when it's its turn
 *
 * COMPARISON
 * ----------
 * - Why not synchronized : Synchronized is blocking and ties up the entire worker
 * thread while waiting
 * - Why not AtomicReferenceArray : it is a fixed-length array where every "bucket" is atomic.
 * - When to choose Atomic Array over a protected List - when we have a fixed number of elements
 * and we need fine-grained concurrency. It allows multiple threads to update different indices
 * of the array at the same time without blocking each other.
 */
fun main() = runBlocking {
    // Scenario: 1000 coroutines adding 1 item each to a list
    println("--- Thread Safety Lab: Mutable Lists ---\n")
    runUnsafeList()
    println("-".repeat(30))
    runMutexList()
}


suspend fun runUnsafeList() {
    val list = mutableListOf<Int>()
    println("Running Unsafe List...")

    val time = measureTimeMillis {
        try {
            coroutineScope {
                repeat(1000) { i ->
                    launch(Dispatchers.Default) {
                        // NO PROTECTION - multiple threads hitting the array at once
                        list.add(i)
                    }
                }
            }
        } catch (e: Exception) {
            println("Caught Expected Crash: ${e.message}")
        }
    }
    println("Unsafe List Final Size: ${list.size} (Target: 1000)")
    println("Time: ${time}ms")
}

suspend fun runMutexList() {
    val list = mutableListOf<Int>()
    val mutex = Mutex()
    println("Running Mutex Protected List...")

    val time = measureTimeMillis {
        coroutineScope {
            repeat(1000) { i ->
                launch(Dispatchers.Default) {
                    // withLock ensures only ONE coroutine enters this block at a time
                    mutex.withLock {
                        list.add(i)
                    }
                }
            }
        }
    }
    println("Mutex List Final Size: ${list.size} (Target: 1000)")
    println("Time: ${time}ms")
}