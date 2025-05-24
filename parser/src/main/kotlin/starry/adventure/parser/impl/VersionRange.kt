package starry.adventure.parser.impl

import starry.adventure.parser.Parser
import starry.adventure.parser.operator.choose
import starry.adventure.parser.operator.list
import starry.adventure.parser.symbol
import starry.adventure.parser.util.rule
import starry.adventure.parser.util.singleLineString
import starry.adventure.parser.whitespace
import java.nio.CharBuffer

interface VersionCondition {
    fun asString(): String
    fun test(version: String): Boolean
}

fun compareVersion(v1: String, v2: String): Int {

    // 分割版本号并转换为整数列表
    val parts1 = v1.split('.').map { it.toIntOrNull() ?: 0 }
    val parts2 = v2.split('.').map { it.toIntOrNull() ?: 0 }

    // 补齐到相同长度，用 0 填充
    val maxLength = maxOf(parts1.size, parts2.size)
    val padded1 = parts1 + List(maxLength - parts1.size) { 0 }
    val padded2 = parts2 + List(maxLength - parts2.size) { 0 }

    // 逐个比较每个部分
    for ((a, b) in padded1.zip(padded2)) {
        if (a != b) {
            return a.compareTo(b)
        }
    }

    return 0
}

class VersionRange(wrapped: VersionCondition): VersionCondition by wrapped {

    override fun toString() = asString()

}

class EqVersionCondition(val version: String, val strict: Boolean = false) : VersionCondition {

    override fun test(version: String): Boolean {
        if (strict) return version == this.version
        val result = compareVersion(version, this.version)
        return result == 0
    }

    override fun asString() = if (strict) "===$version" else "==$version"

}


class LtVersionCondition(val version: String, val equal: Boolean = false) : VersionCondition {

    override fun test(version: String): Boolean {
        val result = compareVersion(version, this.version)
        return result == -1 || (equal && result == 0)
    }

    override fun asString() = if (equal) "<=$version" else "<$version"

}

class GtVersionCondition(val version: String, val equal: Boolean = false) : VersionCondition {

    override fun test(version: String): Boolean {
        val result = compareVersion(version, this.version)
        return result == 1 || (equal && result == 0)
    }

    override fun asString() = if (equal) ">=$version" else ">$version"

}

class AllVersionCondition(val versionConditions: List<VersionCondition>): VersionCondition {

    override fun test(version: String) = versionConditions.all { it.test(version) }
    override fun asString(): String = "all(${versionConditions.joinToString(separator = ", ", transform = VersionCondition::asString)})"

}

class AnyVersionCondition(val versionConditions: List<VersionCondition>): VersionCondition {

    override fun test(version: String) = versionConditions.any { it.test(version) }
    override fun asString(): String = "any(${versionConditions.joinToString(separator = ", ", transform = VersionCondition::asString)})"

}

fun parseVersionRange(text: String): VersionRange {
    val versionRangeParser = rule("versionRangeParser") {
        +VersionConditionParser.versionCondition
    }
    versionRangeParser.state(Parser.State(CharBuffer.wrap(text)))
    return VersionRange(versionRangeParser.parse())
}


object VersionConditionParser {

    private fun versionCondition() = rule("versionCondition") {
        +choose(eqParser, strictEqParser, ltEqParser, gtParser, ltEqParser, gtEqParser, allParser, anyParser)
    }

    val versionCondition = versionCondition()

    val eqParser by rule {
        val symbol = +symbol("==")
        +whitespace
        val string = +singleLineString
        EqVersionCondition(string)
    }

    val strictEqParser by rule {
        val symbol = +symbol("===")
        +whitespace
        val string = +singleLineString
        EqVersionCondition(string, true)
    }

    val ltParser by rule {
        val symbol = +symbol("<")
        +whitespace
        val string = +singleLineString
        LtVersionCondition(string)
    }

    val ltEqParser by rule {
        val symbol = +symbol("<=")
        +whitespace
        val string = +singleLineString
        LtVersionCondition(string, true)
    }

    val gtParser by rule {
        val symbol = +symbol(">")
        +whitespace
        val string = +singleLineString
        GtVersionCondition(string)
    }

    val gtEqParser by rule {
        val symbol = +symbol(">=")
        +whitespace
        val string = +singleLineString
        GtVersionCondition(string, true)
    }

    val anyParser = anyParser()
    private fun anyParser(): Parser<AnyVersionCondition> = rule("anyParser") {
        +symbol("any")
        +whitespace
        val parsed = +versionCondition.list()
        AnyVersionCondition(parsed)
    }

    val allParser = allParser()
    private fun allParser(): Parser<AllVersionCondition> = rule("allParser") {
        +symbol("all")
        +whitespace
        val parsed = +versionCondition.list()
        AllVersionCondition(parsed)
    }

}
