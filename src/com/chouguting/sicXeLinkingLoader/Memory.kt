package com.chouguting.sicXeLinkingLoader


class Memory() {

    companion object {
        val PLUS = 1
        val MINUS = -1
    }

    var startAddressDecimal: Int = 0
    var endAddressDecimal: Int = 0
    lateinit var memorySpace: Array<ByteString>

    val startAddressHexString: String
        get() {
            return Integer.toHexString(startAddressDecimal).uppercase()
        }


    //如果輸入16進位字串，就先轉成10進位
    constructor(startAddressHexString: String) : this() {
        startAddressDecimal = startAddressHexString.toInt(16)
    }

    override fun toString(): String {
        var str = "\nMemory(startAddress=$startAddressHexString)\n"
        str += "address\t                      Contents\n"
        str += "=======      =========================================\n"
        var widthCounter = 0
        var locationCounter = startAddressDecimal
        str += "${locationCounter.toHexString()}\t\t"
        for (byte in memorySpace) {
            widthCounter += 1
            locationCounter += 1
            str += byte
            if (widthCounter == 16) {
                str += "\n"
                widthCounter = 0
                str += "${locationCounter.toHexString().uppercase()}\t\t"
            } else if (widthCounter % 4 == 0) {
                str += "\t"
            }
        }
        return str
    }

    fun putTRecord(recordContent: String, address: Int, length: Int) {
        var currentRecord = recordContent
        var locationCounter = address - startAddressDecimal
        while (currentRecord.isNotBlank()) {
            memorySpace[locationCounter] = ByteString(currentRecord.substring(0, 2).uppercase())
            currentRecord = currentRecord.drop(2)
            locationCounter += 1
        }
    }

    fun modifyMemory(operation: Int, modifyValue: Int, absoluteAddress: Int, nibbleLength: Int) {
        val address = absoluteAddress - startAddressDecimal
        val actualModifyOffset = modifyValue * operation //加或減
        val firstByteIsHalf = (nibbleLength % 2 == 1)
        var stringToBeModified = if (firstByteIsHalf) memorySpace[address].lowerNibble else ""
        var locationCounter = if (firstByteIsHalf) address + 1 else address
        for (i in 0 until nibbleLength / 2) {
            stringToBeModified += memorySpace[locationCounter]
            locationCounter += 1
        }

        val intToBeModified = if (stringToBeModified.hexIsNegative()) ((stringToBeModified.hexTwosComplement()
            .toDecimalInt()) * -1) else stringToBeModified.toDecimalInt()
        //val intToBeModified = stringToBeModified.toDecimalInt()
        var modifiedResult = intToBeModified + actualModifyOffset
        var resultString = modifiedResult.toHexString().uppercase().padStart(nibbleLength, '0')
        if (resultString.length > nibbleLength) {
            resultString = resultString.drop(resultString.length - nibbleLength)
        }
        locationCounter = address
        if (firstByteIsHalf) {
            memorySpace[address].lowerNibble = resultString[0].toString()
            locationCounter += 1
            resultString = resultString.drop(1)
        }
        for (i in 0 until nibbleLength / 2) {
            memorySpace[locationCounter] = ByteString(resultString.substring(0, 2))
            resultString = resultString.drop(2)
            locationCounter += 1
        }
    }


    fun createSpace(spaceSize: Int) {
        memorySpace = Array(spaceSize) { ByteString("**") }
    }


}

class ByteString() {
    var higherNibble = "0"
    var lowerNibble = "0"

    constructor(byteHexString: String) : this() {
        setByte(byteHexString)
    }

    fun setByte(byteHexString: String) {
        higherNibble = if (byteHexString[0] == '*') {
            "*"
        } else {
            byteHexString[0].uppercase()
        }
        lowerNibble = if (byteHexString[1] == '*') {
            "*"
        } else {
            byteHexString[1].uppercase()
        }
    }

    override fun toString(): String {
        return higherNibble + lowerNibble
    }


}