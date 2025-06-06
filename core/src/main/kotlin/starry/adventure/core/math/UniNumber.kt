package starry.adventure.core.math

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.pow

abstract class UniNumber private constructor(): Number(), Comparable<UniNumber> {

    abstract fun toBigInt(): BigInteger
    abstract fun toBigDecimal(): BigDecimal

    companion object {
        operator fun invoke(raw: String): UniNumber = Value(raw)
        operator fun invoke(value: Number): UniNumber = Value(value.toString())
    }

    internal class Plus(val left: UniNumber, val right: UniNumber) : UniNumber() {
        override fun toDouble() = left.toDouble() + right.toDouble()
        override fun toFloat() = left.toFloat() + right.toFloat()
        override fun toInt() = left.toInt() + right.toInt()
        override fun toLong() = left.toLong() + right.toLong()
        override fun toShort() = (left.toShort() + right.toShort()).toShort()
        override fun toByte() = (left.toByte() + right.toByte()).toByte()
        override fun toBigInt() = left.toBigInt() + right.toBigInt()
        override fun toBigDecimal() = left.toBigDecimal() + right.toBigDecimal()
    }

    internal class UnaryMinus(val origin: UniNumber) : UniNumber() {
        override fun toDouble() = -origin.toDouble()
        override fun toFloat() = -origin.toFloat()
        override fun toInt() = -origin.toInt()
        override fun toLong() = -origin.toLong()
        override fun toShort() = (-origin.toShort()).toShort()
        override fun toByte() = (-origin.toByte()).toByte()
        override fun toBigInt() = -origin.toBigInt()
        override fun toBigDecimal() = -origin.toBigDecimal()
    }

    internal class Value(val raw: String) : UniNumber() {
        override fun toByte() = raw.toByte()
        override fun toDouble() = raw.toDouble()
        override fun toFloat() = raw.toFloat()
        override fun toInt() = raw.toInt()
        override fun toLong() = raw.toLong()
        override fun toShort() = raw.toShort()
        override fun toBigInt() = raw.toBigInteger()
        override fun toBigDecimal() = raw.toBigDecimal()
    }

    private class Times(val left: UniNumber, val right: UniNumber) : UniNumber() {
        override fun toDouble() = left.toDouble() * right.toDouble()
        override fun toFloat() = left.toFloat() * right.toFloat()
        override fun toInt() = left.toInt() * right.toInt()
        override fun toLong() = left.toLong() * right.toLong()
        override fun toShort() = (left.toShort() * right.toShort()).toShort()
        override fun toByte() = (left.toByte() * right.toByte()).toByte()
        override fun toBigInt() = left.toBigInt() * right.toBigInt()
        override fun toBigDecimal() = left.toBigDecimal() * right.toBigDecimal()
    }

    internal class Div(val left: UniNumber, val right: UniNumber) : UniNumber() {
        override fun toDouble() = left.toDouble() / right.toDouble()
        override fun toFloat() = left.toFloat() / right.toFloat()
        override fun toInt() = left.toInt() / right.toInt()
        override fun toLong() = left.toLong() / right.toLong()
        override fun toShort() = (left.toShort() / right.toShort()).toShort()
        override fun toByte() = (left.toByte() / right.toByte()).toByte()
        override fun toBigInt() = left.toBigInt() / right.toBigInt()
        override fun toBigDecimal() = left.toBigDecimal() / right.toBigDecimal()
    }

    internal class Pow(val base: UniNumber, val exponent: UniNumber) : UniNumber() {
        override fun toDouble() = base.toDouble().pow(exponent.toDouble())
        override fun toFloat() = base.toFloat().toDouble().pow(exponent.toFloat().toDouble()).toFloat()
        override fun toInt() = base.toInt().toDouble().pow(exponent.toInt().toDouble()).toInt()
        override fun toLong() = base.toLong().toDouble().pow(exponent.toLong().toDouble()).toLong()
        override fun toShort() = (base.toShort().toDouble().pow(exponent.toShort().toDouble())).toInt().toShort()
        override fun toByte() = (base.toByte().toDouble().pow(exponent.toByte().toDouble())).toInt().toByte()
        override fun toBigInt(): BigInteger = base.toBigInt().pow(exponent.toInt())
        override fun toBigDecimal(): BigDecimal = base.toBigDecimal().pow(exponent.toInt())
    }

    internal class Rem(val left: UniNumber, val right: UniNumber) : UniNumber() {
        override fun toDouble() = left.toDouble() % right.toDouble()
        override fun toFloat() = left.toFloat() % right.toFloat()
        override fun toInt() = left.toInt() % right.toInt()
        override fun toLong() = left.toLong() % right.toLong()
        override fun toShort() = (left.toShort() % right.toShort()).toShort()
        override fun toByte() = (left.toByte() % right.toByte()).toByte()
        override fun toBigInt() = left.toBigInt() % right.toBigInt()
        override fun toBigDecimal() = left.toBigDecimal() % right.toBigDecimal()
    }

    operator fun unaryMinus(): UniNumber = UnaryMinus(this)
    operator fun plus(other: UniNumber): UniNumber = Plus(this, other)
    operator fun minus(other: UniNumber) = plus(-other)
    operator fun times(other: UniNumber): UniNumber = Times(this, other)
    operator fun div(other: UniNumber): UniNumber = Div(this, other)
    operator fun rem(other: UniNumber): UniNumber = Rem(this, other)

    operator fun plus(other: Number): UniNumber = plus(UniNumber(other))
    operator fun minus(other: Number) = minus(UniNumber(other))
    operator fun times(other: Number): UniNumber = times(UniNumber(other))
    operator fun div(other: Number): UniNumber = div(UniNumber(other))
    operator fun rem(other: Number): UniNumber = rem(UniNumber(other))

    override fun compareTo(other: UniNumber): Int {
        return when {
            this.toBigDecimal() < other.toBigDecimal() -> -1
            this.toBigDecimal() > other.toBigDecimal() -> 1
            else -> 0
        }
    }

    override fun toString(): String = toBigDecimal().toPlainString()

}

fun String.toUniNumber(): UniNumber = UniNumber(this)
fun Number.toUniNumber(): UniNumber = UniNumber(this)
fun UniNumber.pow(exponent: UniNumber): UniNumber = UniNumber.Pow(this, exponent)
