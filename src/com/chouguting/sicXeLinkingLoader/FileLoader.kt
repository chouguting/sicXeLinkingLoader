package com.chouguting.sicXeLinkingLoader

import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

class FileLoader(private val fileName: String) {
    fun loadFileToLines(): List<String> {
        lateinit var inputLines: List<String>
        try {
            inputLines = File(fileName).useLines { it.toList() }
        } catch (e: FileNotFoundException) {
            println("File not found" + e)
            exitProcess(-1)
        }
        return inputLines
    }
}