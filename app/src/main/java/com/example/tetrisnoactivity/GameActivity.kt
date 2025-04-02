package com.example.tetrisnoactivity

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tetrisnoactivity.models.AppModel
import com.example.tetrisnoactivity.storage.AppPreferences
import com.example.tetrisnoactivity.view.TetrisView
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

import androidx.lifecycle.ViewModel

class GameActivity: AppCompatActivity() {
    var tvHighScore: TextView? = null
    var tvCurrentScore: TextView? = null

    private lateinit var tetrisView: TetrisView
    var appPreferences:  AppPreferences? = null
    private val appModel: AppModel = AppModel()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG1, "on_create")

        setContentView(R.layout.activity_game)
        appPreferences = AppPreferences(this)
        appModel.setPreferences(appPreferences)

        val btnRestart = findViewById<Button>(R.id.btn_restart)
        val btnExit = findViewById<Button>(R.id.btn_exit)
        tvHighScore = findViewById<TextView>(R.id.tv_high_score)
        tvCurrentScore = findViewById<TextView>(R.id.tv_current_score)
        tetrisView = findViewById<TetrisView>(R.id.view_tetris)
        tetrisView.setActivity(this)
        tetrisView.setModel(appModel)
        tetrisView.setOnTouchListener(this::onTetrisViewTouch)
        btnRestart.setOnClickListener(this::btnRestartClick)
        btnExit.setOnClickListener(this::btnExitClick)

        updateHighScore()
        updateCurrentScore(savedInstanceState?.getString("currentScore"))

        Log.d(TAG2, savedInstanceState?.getString("gameModel").toString())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("currentScore", tvCurrentScore?.text.toString())
        outState.putString("gameModel", Json.encodeToString(tetrisView.modelToBundle().toString()))

        super.onSaveInstanceState(outState)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//
//        tvCurrentScore?.text = savedInstanceState.getString("currentScore")
//        tetrisView = Json.decodeFromString<TetrisView>(savedInstanceState.getString("gameView").toString())
//    }

    private fun btnExitClick(view: View) {
        System.exit(0)
    }

    private fun btnRestartClick(view: View) {
        appModel.restartGame()
    }

    private fun onTetrisViewTouch(view: View, event: MotionEvent): Boolean {
        if (appModel.isGameOver() || appModel.isGameAwaitingStart()) {
            appModel.startGame()
            tetrisView.setGameCommandWithDelay(AppModel.Motions.DOWN)
        } else if (appModel.isGameActive()) {
            when (resolveTouchDirection(view, event)) {
                0 -> moveTetromino(AppModel.Motions.LEFT)
                1 -> moveTetromino(AppModel.Motions.ROTATE)
                2 -> moveTetromino(AppModel.Motions.DOWN)
                3 -> moveTetromino(AppModel.Motions.RIGHT)
            }
        }
        return true
    }

    private fun resolveTouchDirection(view: View, event: MotionEvent): Int {
        val x = event.x / view.width
        val y = event.y / view.height
        val direction: Int
        direction = if (y > x) {
            if (x > 1 - y) 2 else 0
        } else {
            if (x > 1 - y) 3 else 1
        }
        return direction
    }

    private fun moveTetromino(motion: AppModel.Motions) {
        if (appModel.isGameActive()) {
            tetrisView.setGameCommand(motion)
        }
    }

    private fun updateHighScore() {
        tvHighScore?.text = "${appPreferences?.getHighScore()}"
    }

    private fun updateCurrentScore(score: String?) {
        tvCurrentScore?.text = score ?: "0"
    }

    companion object {
        const val TAG1 = "Game_Activity"
        const val TAG2 = "Log_Out"
    }
}