package com.example.threadsafety

import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import java.util.ConcurrentModificationException

/**
 * ArrayList
 * NOT thread-safe for structural modifications.
 * If one thread is reading (iterating) and another thread
 * modifies the list, it throws a ConcurrentModificationException.
 *
 */
class UnsafeNotificationSystem {
    private val listeners = ArrayList<String>()

    fun register(name: String) = listeners.add(name)

    fun broadcast() {
        try {
            for (listener in listeners) {
                println("Unsafe Broadcast to: $listener")
                // Simulate some work
                Thread.sleep(50)
            }
        } catch (e: ConcurrentModificationException) {
            println("CRASH! Unsafe system failed: ${e.javaClass.simpleName}")
        }
    }
}

/**
 * CopyOnWriteArrayList
 * Thread-safe. Designed for scenarios where reads vastly outnumber writes. It uses a 'Snapshot'
 * iterator that never throws ConcurrentModificationException.
 *
 */
class SafeNotificationSystem {
    private val listeners = CopyOnWriteArrayList<String>()

    fun register(name: String) = listeners.add(name)

    fun broadcast() {
        for (listener in listeners) {
            println("Safe Broadcast to: $listener")
            Thread.sleep(50)
        }
    }
}

fun main() {
    println("--- Starting List Concurrency Lab ---")

    // THE UNSAFE CASE
    val unsafeSystem = UnsafeNotificationSystem()
    unsafeSystem.register("Observer_1")
    unsafeSystem.register("Observer_2")

    thread {
        unsafeSystem.broadcast()
    }

    thread {
        Thread.sleep(20) // Modify while the broadcast is happening
        println("Unsafe Writer: Attempting to register Observer_3...")
        try {
            unsafeSystem.register("Observer_3")
        } catch (_: Exception) {
        }
    }

    Thread.sleep(300)
    println("-------------------------------------")

    // THE SAFE CASE
    val safeSystem = SafeNotificationSystem()
    safeSystem.register("Observer_1")
    safeSystem.register("Observer_2")

    val reader = thread {
        println("Safe Reader: Starting broadcast...")
        safeSystem.broadcast()
        println("Safe Reader: Broadcast finished without crashing.")
    }

    thread {
        Thread.sleep(20)
        println("Safe Writer: Registering Observer_3 (creating new array copy)...")
        safeSystem.register("Observer_3")
    }

    reader.join()
}

/**
 * - CopyOnWriteArrayList is Fail-Safe: It allows changes by creating new copies, so the reader
 * never even sees the change until the next time they start a loop.
 * - While readers never lock, writers do lock each other so that two simultaneous "adds"
 * don't result in lost data during the copying process.
 *
 */