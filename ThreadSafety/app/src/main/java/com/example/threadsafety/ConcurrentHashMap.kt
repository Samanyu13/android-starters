package com.example.threadsafety

import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

/**
 * Regular HashMap
 * This will likely lose data or crash.
 * When two threads call 'put' at the exact same time, one update can overwrite the other.
 *
 */
class UnsafeCache {
    private val data = HashMap<String, Int>()

    fun increment(key: String) {
        val current = data[key] ?: 0
        data[key] = current + 1
    }

    fun get(key: String) = data[key]
}

/**
 * ConcurrentHashMap
 * Thread-safe without explicit 'synchronized' blocks.
 * It uses internal 'Compare-And-Swap' and bucket-level locking.
 *
 */
class SafeCache {
    private val data = ConcurrentHashMap<String, Int>()

    fun increment(key: String) {
        // compute() is an atomic operation provided by ConcurrentHashMap
        data.compute(key) { _, value -> (value ?: 0) + 1 }
    }

    fun get(key: String) = data[key]
}

fun main() {
    val iterations = 1000
    val key = "key"

    // UNSAFE
    val unsafe = UnsafeCache()
    val t1 = thread { repeat(iterations) { unsafe.increment(key) } }
    val t2 = thread { repeat(iterations) { unsafe.increment(key) } }
    t1.join(); t2.join()

    println("Unsafe Result: ${unsafe.get(key)} (Expected ${iterations * 2})")
    // Note: Often shows less than 2000 because of "lost updates"

    // SAFE
    val safe = SafeCache()
    val t3 = thread { repeat(iterations) { safe.increment(key) } }
    val t4 = thread { repeat(iterations) { safe.increment(key) } }
    t3.join(); t4.join()

    println("Safe Result: ${safe.get(key)} (Expected ${iterations * 2})")
    // Note: Always 2000
}

/**
 * When to use it in Android?
 *
 * Use ConcurrentHashMap for Global Caches.
 * If you have an Image Downloader that caches Bitmaps in memory, multiple background
 * threads will be checking if (cache.containsKey(url)) and calling cache.put(url, bitmap).
 * Using ConcurrentHashMap ensures your app doesn't crash during these simultaneous lookups.
 *
 * Different from SynchronizedMap
 * it locks the whole map - so basically, one writer at a time
 *
 */