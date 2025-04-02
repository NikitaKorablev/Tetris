package com.example.tetrisnoactivity.storage

import android.content.SharedPreferences
import android.content.Context
import kotlinx.serialization.Serializable

//@Serializable
class AppPreferences(ctx: Context) {
    private var data: SharedPreferences = ctx.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    fun saveHighScore(highScore: Int) {
        data.edit().putInt("HIGH_SCORE", highScore).apply()
    }
    fun getHighScore(): Int {
        return data.getInt("HIGH_SCORE", 0)
    }
    fun clearHighScore() {
        data.edit().putInt("HIGH_SCORE", 0).apply()
    }
}