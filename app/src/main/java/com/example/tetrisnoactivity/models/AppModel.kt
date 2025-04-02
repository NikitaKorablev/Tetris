package com.example.tetrisnoactivity.models

import android.graphics.Point
import android.os.Bundle
import android.security.identity.EphemeralPublicKeyNotFoundException
import androidx.core.graphics.translationMatrix
import com.example.tetrisnoactivity.constants.CellConstants
import com.example.tetrisnoactivity.constants.FieldConstants
import com.example.tetrisnoactivity.helper.array2dOfByte
import com.example.tetrisnoactivity.storage.AppPreferences
import kotlin.system.measureNanoTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.json.JSONObject

//@Serializable
class AppModel {
    var score: Int = 0
    private var preferences: AppPreferences? = null

    var currentBlock: Block? = null
    var currentState: String = Statuses.AWAITING_START.name

    private var field: Array<ByteArray> = array2dOfByte(
        FieldConstants.ROW_COUNT.value,
        FieldConstants.COLUMN_COUNT.value
    )

//    fun setModel(savedState: Bundle) {
//        this.score = savedState.getInt("score")
//        this.currentState = savedState.getString("currentState")!!
//        if (savedState.getString("block"))
//
//        this.currentBlock
//
//    }

    //Устанавливает свойство предпочтений
    fun setPreferences(preferences: AppPreferences?) {
        this.preferences = preferences
    }

    fun getBundle(): Bundle {
        val res = Bundle()
        res.putInt("score", this.score)
        res.putString("currentState", this.currentState)
//        res.putString("preferences", Json.encodeToString(this.preferences))

        if (this.currentBlock != null) {
            res.putString("block", this.currentBlock!!.toBundle().toString())
            val position: Point? = this.currentBlock?.position
            val block = JSONObject("""{
                |"frameNumber": "${this.currentBlock?.frameNumber}",
                |"shapeIndex": "${this.currentBlock?.shapeIndex}",
                |"color": "${this.currentBlock?.color}",
                |"position": {
                |   "x": "${position?.x}",
                |   "y": "${position?.y}"
                |}}""".trimMargin())
        }


        return res
    }

    fun getCellStatus(row: Int, column: Int): Byte {
        return field[row][column]
    }

    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null) {
            field[row][column] = status
        }
    }

    fun isGameOver(): Boolean {
        return currentState == Statuses.OVER.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameAwaitingStart(): Boolean {
        return currentState == Statuses.AWAITING_START.name
    }

    private fun boostScore() {
        score += 10
        if (score > preferences?.getHighScore() as Int)
            preferences?.saveHighScore(score)
    }

    private fun generateNextBlock() {
        currentBlock = Block.createBlock()
//        currentBlock!!.createBlock()
    }

    //Проверяем допкстимость посткпательного движения
    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) false
        else if (position.y + shape.size > FieldConstants.ROW_COUNT.value) false
        else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) false
        else {
            //[0, 10)
            for (i in 0 until shape.size) {
                for (j in 0 until shape[i].size) {
                    val y = position.y + i
                    val x = position.x + j
                    if (CellConstants.EMPTY.value != shape[i][j] &&
                            CellConstants.EMPTY.value != field[y][x]) {
                        return false
                    }
                }
            }
            true
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter { !ephemeralCellsOnly || field[i][it] == CellConstants.EPHEMERAL.value }
                .forEach { field[i][it] = CellConstants.EMPTY.value }
        }
    }

    private fun persistCellData() {
        for (i in 0 until field.size) {
            for (j in 0 until field[i].size) {
                var status = getCellStatus(i, j)
                if (status == CellConstants.EPHEMERAL.value) {
                    status = currentBlock?.staticValue!!
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    //Проверяет, построчно, заполняемость поля
    private fun assessField() {
        for (i in 0 until field.size) {
            var emptyCells = 0
            for (j in 0 until field[i].size) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConstants.EMPTY.value == status
                if (isEmpty) emptyCells++
            }
            if (emptyCells == 0) shiftRows(i)
        }
    }

    //Генерирует обновление поля
    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()
            var frameNumber: Int? = currentBlock?.getFrameNumber()
            val coordinate: Point? = Point()
            coordinate?.x = currentBlock?.getPosition()?.x
            coordinate?.y = currentBlock?.getPosition()?.y

            when (action) {
                Motions.LEFT.name -> {
                    coordinate?.x = currentBlock?.getPosition()?.x?.minus(1)
                }
                Motions.RIGHT.name -> {
                    coordinate?.x = currentBlock?.getPosition()?.x?.plus(1)
                }
                Motions.DOWN.name -> {
                    coordinate?.y = currentBlock?.getPosition()?.y?.plus(1)
                }

                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)
                    if (frameNumber != null && (frameNumber >= currentBlock?.getFrameCount() as Int)) {
                        frameNumber = 0
                    }
                }
            }

            if (!moveValid(coordinate as Point, frameNumber)) {
                //Елси движение не является валидным, то блок фиксируется в поле
                //на его текущей позиции с помощью translateBlock
                translateBlock(currentBlock?.getPosition() as Point,
                                currentBlock?.getFrameNumber() as Int)
                if (Motions.DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()
                    if (!blockAdditionPossible()) {
                        currentState = Statuses.OVER.name
                        currentBlock = null
                        resetField(false)
                    }
                }
            } else if (frameNumber != null) {
                translateBlock(coordinate, frameNumber)
                currentBlock?.setState(frameNumber, coordinate)
            }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)
            if (shape != null) {
                for (i in shape.indices) {
                    for (j in 0 until shape[i].size) {
                        val y = position.y + i
                        val x = position.x + j
                        if (CellConstants.EMPTY.value != shape[i][j])
                            field[y][x] = shape[i][j]
                    }
                }
            }
        }
    }

    //Проверяем, что поле ещё не заполненно, и блок может перемещаться
    private fun blockAdditionPossible(): Boolean {
        if (!moveValid(currentBlock?.getPosition() as Point,
                        currentBlock?.getFrameNumber())) return false
        return true
    }

    private fun shiftRows(nToRow: Int) {
        if (nToRow > 0) {
            for (j in nToRow - 1 downTo 0) {
                for (m in 0 until field[j].size) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }

        for (j in 0 until field[0].size)
            setCellStatus(0, j, CellConstants.EMPTY.value)
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateNextBlock()
        }
    }

    fun restartGame() {
        resetModel()
        startGame()
    }

    fun endGame() {
        score = 0
        currentState = AppModel.Statuses.OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        score = 0
    }

    //AWAITING_START - состояние игры до её запуска
    //ACTIVE, INACTIVE - состояние игрового процесса
    //OVER - статус игры на момент её завершения
    enum class Statuses {
        AWAITING_START, ACTIVE, INACTIVE, OVER
    }
    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATE
    }
}

