package com.chouguting.sicXeLinkingLoader

class LinkingLoader(val memory: Memory, val allObjFilesStringList: List<List<String>>) {
    fun linkAndLoad() {
        val externalSymbolTable: MutableMap<String, Int> = mutableMapOf()
        val programAddress = memory.startAddressDecimal
        var controlSectionAddress = programAddress

        //println("programAddress$programAddress")
        //pass 1
        for (controlSectionStringList in allObjFilesStringList) {
            var controlSectionLength = 0
            for (currentObjLine in controlSectionStringList) {
                if (currentObjLine.startsWith("H")) {
                    controlSectionLength = currentObjLine.substring(13, 19).toDecimalInt() //程式長度
                    val controlSectionName = currentObjLine.substring(1, 7).trim()
                    if (externalSymbolTable[controlSectionName] != null) {
                        println("有重複的程式")
                        return
                    }
                    externalSymbolTable[controlSectionName] = controlSectionAddress
                } else if (currentObjLine.startsWith("E")) {
                    break
                } else if (currentObjLine.startsWith("D")) {
                    var currentDRecord = currentObjLine.drop(1)
                    while (currentDRecord.isNotBlank()) {
                        val externalSymbolName = currentDRecord.substring(0, 6).trim()
                        val externalSymbolRelativeAddress = currentDRecord.substring(6, 12).trimStart().toDecimalInt()
                        if (externalSymbolTable[externalSymbolName] != null) {
                            println("有重複的externalSymbol定義")
                            return
                        }
                        externalSymbolTable[externalSymbolName] = externalSymbolRelativeAddress + controlSectionAddress
                        currentDRecord = currentDRecord.drop(12)
                    }

                }

            }
            controlSectionAddress += controlSectionLength
        }

        val programLength = controlSectionAddress - programAddress
        memory.createSpace(programLength)
        //println("programLength:${programLength}")

        println(externalSymbolTable.toExternalSymbolTableString())


        //pass 2
        controlSectionAddress = programAddress
        var executionAddress = programAddress //先不要
        for (controlSectionStringList in allObjFilesStringList) {
            var controlSectionLength = 0
            for (currentObjLine in controlSectionStringList) {
                if (currentObjLine.startsWith("H")) {
                    controlSectionLength = currentObjLine.substring(13, 19).toDecimalInt() //程式長度
                } else if (currentObjLine.startsWith("E")) {
                    if (currentObjLine.length >= 7) {
                        executionAddress = currentObjLine.substring(1, 7).toDecimalInt() + controlSectionAddress
                    }
                    break
                } else if (currentObjLine.startsWith("T")) {
                    var currentRecord = currentObjLine
                    val recordStartingAddress = currentRecord.substring(1, 7).toDecimalInt() + controlSectionAddress
                    val recordLength = currentRecord.substring(8, 10).toDecimalInt()
                    currentRecord = currentRecord.drop(9)
                    memory.putTRecord(currentRecord, recordStartingAddress, recordLength)
                } else if (currentObjLine.startsWith("M")) {
                    val modifyAddress = currentObjLine.substring(1, 7).toDecimalInt() + controlSectionAddress
                    val nibbleLength = currentObjLine.substring(7, 9).toDecimalInt()
                    val operation = if (currentObjLine[9] == '+') Memory.PLUS else Memory.MINUS
                    val modifyValue =
                        externalSymbolTable[currentObjLine.substring(10, currentObjLine.length).trim()] ?: 0
                    memory.modifyMemory(operation, modifyValue, modifyAddress, nibbleLength)
                }

            }
            controlSectionAddress += controlSectionLength
        }
        println(memory)
        println("execution point: ${executionAddress.toHexString().padStart(4, '0')}")
    }

}