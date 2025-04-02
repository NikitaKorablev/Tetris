package com.example.tetrisnoactivity.helper

// sizeOuter - кол-во строк создаваемого массива
// sizeInner - кол-во столбцов сгенерированного бассива байт
fun array2dOfByte(sizeOuter: Int, sizeInner: Int): Array<ByteArray>
                            = Array(sizeOuter) {ByteArray(sizeInner)}

