package com.example.tetrisnoactivity.models

// enum создаёт как объекты, так и константы
enum class Shape(val frameCount: Int, val startPosition: Int) {

    // Tetromino - экземпляр класса Shape
    // frameCount = 2, потому что tetramino может находиться в 2 положениях

    //----Tetromino-I----
    //----1--------------
    //----1--or--1-1-1-1-
    //----1--------------
    //----1--------------
    TetrominoI(2, 2) {
        override fun getFrame(frameNumber: Int): Frame {
            return when (frameNumber) {
                // 0 - моделька в горизонтальном положении
                0 -> Frame(4).addRow("1111")

                // 1 - моделька в вертикальном положении
                1 -> Frame(1)
                    .addRow("1")
                    .addRow("1")
                    .addRow("1")
                    .addRow("1")

                // если frameNumber != 0 или 1, то получаем исключение
                else -> throw IllegalArgumentException("$frameNumber is an invalid frame number.")
            }
        }
    },

    //----Tetromino-O----
    //----1-1------------
    //----1-1------------
    TetrominoO(1, 1) {
        override fun getFrame(frameNumber: Int): Frame {
            return Frame(2)
                .addRow("11")
                .addRow("11")
        }
    },

    //----Tetromino-Z----------
    //----1-1-0---------0-1----
    //----0-1-1---or----1-1----
    //------------------1-0----
    TetrominoZ(2, 1) {
        override fun getFrame(frameNumber: Int): Frame {
            return when (frameNumber) {
                0 -> Frame(3)
                    .addRow("110")
                    .addRow("011")
                1 -> Frame(2)
                    .addRow("01")
                    .addRow("11")
                    .addRow("10")
                else -> throw IllegalArgumentException("$frameNumber is an invalid number.")
            }
        }
    },

    //----Tetromino-S-----------
    //----0-1-1----------1-0----
    //----1-1-0----or----1-1----
    //-------------------0-1----
    TetrominoS(2, 1) {
        override fun getFrame(frameNumber: Int): Frame {
            return when (frameNumber) {
                0 -> Frame(3)
                    .addRow("011")
                    .addRow("110")
                1 -> Frame(2)
                    .addRow("10")
                    .addRow("11")
                    .addRow("01")
                else -> throw IllegalArgumentException("$frameNumber is an invalid number.")
            }
        }
    },

    //----Tetromino-T---------------------------------------
    //----0-1-0----------1-0----------0-1----------1-1-1----
    //----1-1-1----or----1-1----or----1-1----or----0-1-0----
    //-------------------1-0----------0-1-------------------
    TetrominoT(4, 1) {
        override fun getFrame(frameNumber: Int): Frame {
            return when (frameNumber) {
                0 -> Frame(3)
                    .addRow("010")
                    .addRow("111")
                1 -> Frame(2)
                    .addRow("10")
                    .addRow("11")
                    .addRow("10")
                2 -> Frame(3)
                    .addRow("111")
                    .addRow("010")
                3 -> Frame(2)
                    .addRow("01")
                    .addRow("11")
                    .addRow("01")
                else -> throw IllegalArgumentException("$frameNumber is an invalid number.")
            }
        }
    },

    //----Tetromino-J---------------------------------------
    //----0-1----------1-1----------1-0-0----------1-1-1----
    //----0-1----or----1-0----or----1-1-1----or----0-0-1----
    //----1-1----------1-0----------------------------------
    TetrominoJ(4, 1) {
        override fun getFrame(frameNumber: Int): Frame {
            return when (frameNumber) {
                0 -> Frame(3)
                    .addRow("100")
                    .addRow("111")
                1 -> Frame(2)
                    .addRow("11")
                    .addRow("10")
                    .addRow("10")
                2 -> Frame(3)
                    .addRow("111")
                    .addRow("001")
                3 -> Frame(2)
                    .addRow("01")
                    .addRow("01")
                    .addRow("11")
                else -> throw IllegalArgumentException("$frameNumber is an invalid number.")
            }
        }
    },

    //----Tetromino-L---------------------------------------
    //----1-0----------1-1----------0-0-1----------1-1-1----
    //----1-0----or----0-1----or----1-1-1----or----1-0-0----
    //----1-1----------0-1----------------------------------
    TetrominoL(4, 1) {
        override fun getFrame(frameNumber: Int): Frame {
            return when (frameNumber) {
                0 -> Frame(3)
                    .addRow("001")
                    .addRow("111")
                1 -> Frame(2)
                    .addRow("10")
                    .addRow("10")
                    .addRow("11")
                2 -> Frame(3)
                    .addRow("111")
                    .addRow("100")
                3 -> Frame(2)
                    .addRow("11")
                    .addRow("01")
                    .addRow("01")
                else -> throw IllegalArgumentException("$frameNumber is an invalid number.")
            }
        }
    };
    abstract fun getFrame(frameNumber: Int): Frame
}