package com.example.tetrisnoactivity.models

import com.example.tetrisnoactivity.helper.array2dOfByte

// width - задаёт необходимую ширину генерируемого фрейма
// (число столбцов в байтовом массиве)
class Frame(private val width: Int) {
    val data: ArrayList<ByteArray> = ArrayList()

    fun addRow(byteStr: String): Frame {
        val row = ByteArray(byteStr.length)

        for (index in byteStr.indices) {
            row[index] = "${byteStr[index]}".toByte()
        }
        data.add(row)
        return this
    }

    fun as2dByteArray(): Array<ByteArray> {
        val bytes = array2dOfByte(data.size, width)
        return data.toArray(bytes)
    }
}