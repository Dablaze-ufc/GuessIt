package com.dablaze.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(final_score: Int) : ViewModel() {

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score
    private val _playAgain = MutableLiveData<Boolean>()
    val playAgain: LiveData<Boolean>
        get() = _playAgain

    init {
        _score.value = final_score
        Log.i("scoreViewModel", "Final score = $final_score")
    }

    fun onPlayAgain() {
        _playAgain.value = true
    }

    fun onPlayAgainComplete() {
        _playAgain.value = false
    }


}