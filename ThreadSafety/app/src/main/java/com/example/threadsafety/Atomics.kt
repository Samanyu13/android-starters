package com.example.threadsafety

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * ABOUT
 * -----
 * Atomics use something known as CAS (Compare-And-Swap) - its not a lock, but an expectation.
 * Basically, a thread, say, A,  tells the CPU - "I see value X and wants to operate on it.
 * Please do it only if the current value is still X." CPU checks and updates X only if it still
 * has the value as reported by thread A.
 * So what if it was updated by another thread B? Since it is not a lock, thread B does not go to
 * sleep - rather, it immediately retries. Often called Spin-lock or an Optimistic Loop.
 *
 *
 * TYPES
 * -----
 * Basic types
 * - AtomicBoolean : Thread-safe "on/off" switches or "one-time" triggers
 * - AtomicLong / AtomicInteger : Simple math, counters, sequence IDs
 * - For float, double: We use AtomicLong / AtomicInteger as containers
 *
 * Universal
 * - AtomicReference<T> - Updating complex Data Classes or Configuration objects
 *
 * Atomic Arrays
 * - AtomicIntegerArray / AtomicLongArray / AtomicReferenceArray : Fixed-size collections where
 * elements are updated by multiple threads.
 */
fun main() = runBlocking {
    println("Starting Thread Safety Lab: Atomic Operations\n")

    runBrokenCounter()

    println("-".repeat(30))

    runAtomicCounter()

    println("-".repeat(30))

    runAtomicFloat()
}

/**
 * API 1: The Non-Thread-Safe Approach
 * Demonstration of a Race Condition using a standard variable.
 * This will fail because '++' is not an atomic operation; multiple threads
 * will read the same value before the others can update it, leading to "lost" counts.
 */
suspend fun runBrokenCounter() {
    var counter = 0
    mutableListOf<Job>()

    val time = measureTimeMillis {
        coroutineScope {
            repeat(1000) {
                launch(Dispatchers.Default) {
                    repeat(1000) {
                        counter++
                    }
                }
            }
        }
    }

    println("Broken Counter Result: $counter")
    println("Target: 1,000,000")
    println("Success: ${counter == 1_000_000}")
    println("Time taken: ${time}ms")
}

/**
 * API 2: The Atomic Approach
 *  Demonstration of Thread Safety using AtomicInteger.
 *  This succeeds because it uses low-level CPU 'Compare-And-Swap' (CAS) instructions
 *  to ensure that the increment is treated as a single, uninterruptible unit.
 */
suspend fun runAtomicCounter() {
    val atomicCounter = AtomicInteger(0)

    val time = measureTimeMillis {
        coroutineScope {
            repeat(1000) {
                launch(Dispatchers.Default) {
                    repeat(1000) {
                        atomicCounter.incrementAndGet()
                    }
                }
            }
        }
    }

    println("Atomic Counter Result: ${atomicCounter.get()}")
    println("Target: 1,000,000")
    println("Success: ${atomicCounter.get() == 1_000_000}")
    println("Time taken: ${time}ms")
}

/**
 * API 3: For float case
 * We use AtomicInteger to hold the bits of the float.
 */
suspend fun runAtomicFloat() {
    val atomicBits = AtomicInteger(0.0f.toRawBits())
    val coroutines = 1000
    val iterations = 1000
    val increment = 1.1f

    val time = measureTimeMillis {
        coroutineScope {
            repeat(coroutines) {
                launch(Dispatchers.Default) {
                    repeat(iterations) {
                        updateAtomicFloat(atomicBits, increment)
                    }
                }
            }
        }
    }

    val finalResult = Float.fromBits(atomicBits.get())
    val expected = coroutines * iterations * increment

    println("Result: $finalResult")
    println("Target: $expected")
    println("Time:   ${time}ms")
}

/**
 * Helper function representing the "CAS Loop"
 */
fun updateAtomicFloat(atomicBits: AtomicInteger, valueToAdd: Float) {
    var expectedBits: Int
    var nextBits: Int
    do {
        expectedBits = atomicBits.get()
        val currentFloat = Float.fromBits(expectedBits)
        val nextFloat = currentFloat + valueToAdd
        nextBits = nextFloat.toRawBits()

        // Only swap if the bits haven't changed since we read them
    } while (!atomicBits.compareAndSet(expectedBits, nextBits))
}