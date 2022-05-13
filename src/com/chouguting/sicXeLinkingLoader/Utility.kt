package com.chouguting.sicXeLinkingLoader

fun String.isHexNumber(): Boolean {
    try {
        val testToInt = this.toInt(16)
    } catch (e: Exception) {
        return false
    }
    return true
}

fun Int.toHexString(): String {
    return Integer.toHexString(this)
}

fun String.toDecimalInt(): Int {
    return this.toInt(16)
}

fun MutableMap<String, Int>.toExternalSymbolTableString(): String {
    var str = "\nExternalSymbolTable\nName\t  Address\n"
    str += "==========================\n"
    for (indices in this) {
        str += "${indices.key}\t\t${indices.value.toHexString().uppercase()}\n"
    }
    return str
}


fun String.hexIsNegative(): Boolean {
    if (this[0].toString().toDecimalInt() >= 8) return true
    return false
}

//TODO:二補數
fun String.hexTwosComplement(): String {
    var binaryValue = Integer.toBinaryString(this.toDecimalInt()).toString()
    var complementString = ""
    for (i in 0 until binaryValue.length) {
        complementString += if (binaryValue[i] == '0') "1" else "0"
    }
    val complementDecimalValue = complementString.toInt(2) + 1
    return complementDecimalValue.toHexString()
}