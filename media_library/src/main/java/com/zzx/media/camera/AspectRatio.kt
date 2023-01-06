package com.zzx.media.camera

import android.hardware.Camera
import androidx.annotation.VisibleForTesting
import kotlin.math.abs

/**
 * A simple class representing an aspect ratio.
 */
class AspectRatio private constructor(val x: Int, val y: Int) : Comparable<AspectRatio> {

    fun matches(size: Camera.Size): Boolean {
        val gcd = gcd(size.height, size.height)
        val x = size.width / gcd
        val y = size.height / gcd
        return this.x == x && this.y == y
    }

    fun matches(size: Camera.Size, tolerance: Float): Boolean {
        return abs(toFloat() - size.width.toFloat() / size.height) <= tolerance
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this === other) {
            return true
        }
        if (other is AspectRatio) {
            return x == other.x && y == other.y
        }
        return false
    }

    override fun toString(): String {
        return "$x:$y"
    }

    fun toFloat(): Float {
        return x.toFloat() / y
    }

    override fun hashCode(): Int {
        return y xor (x shl Integer.SIZE / 2 or (x ushr Integer.SIZE)) / 2
    }

    override fun compareTo(another: AspectRatio): Int {
        if (equals(another)) {
            return 0
        } else if (toFloat() - another.toFloat() > 0) {
            return 1
        }
        return -1
    }

    /**
     * Returns a flipped aspect ratio, which means inverting its dimensions.
     * @return a flipped aspect ratio
     */
    fun flip(): AspectRatio {
        return of(y, x)
    }

    companion object {
        @VisibleForTesting
        val sCache = HashMap<String, AspectRatio>(16)

        /**
         * Creates an aspect ratio for the given size.
         * @param size the size
         * @return a (possibly cached) aspect ratio
         */
        fun of(size: Camera.Size): AspectRatio {
            return of(size.width, size.height)
        }

        /**
         * Creates an aspect ratio with the given values.
         * @param xTmp the width
         * @param yTmp the height
         * @return a (possibly cached) aspect ratio
         */
        fun of(xTmp: Int, yTmp: Int): AspectRatio {
            var x = xTmp
            var y = yTmp
            val gcd = gcd(x, y)
            x /= gcd
            y /= gcd
            val key = "$x:$y"
            var cached = sCache[key]
            if (cached == null) {
                cached = AspectRatio(x, y)
                sCache[key] = cached
            }
            return cached
        }

        /**
         * Parses an aspect ratio string, for example those previously obtained
         * with [.toString].
         *
         * @param string a string of the format x:y where x and y are integers
         * @return a (possibly cached) aspect ratio
         */
        fun parse(string: String): AspectRatio {
            val parts = string.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size != 2) {
                throw NumberFormatException("Illegal AspectRatio string. Must be x:y")
            }
            val x = Integer.valueOf(parts[0])
            val y = Integer.valueOf(parts[1])
            return of(x, y)
        }

        private fun gcd(a: Int, b: Int): Int {
            var a = a
            var b = b
            while (b != 0) {
                val c = b
                b = a % b
                a = c
            }
            return a
        }
    }
}