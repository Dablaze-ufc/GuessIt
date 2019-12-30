package com.dablaze.android.guesstheword.screens.game

import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel() {
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 60000L
    }

    private var timer: CountDownTimer

    // The current word
    private var _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    //    The buzzer
    private var _buzzer = MutableLiveData<BuzzType>()
    val buzzType: LiveData<BuzzType>
        get() = _buzzer

    // timer
    private var _currentTime = MutableLiveData<Long>()
    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)

    }
    val currentTime: LiveData<Long>
        get() = _currentTime
    // The current score
    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private var _gamefinished = MutableLiveData<Boolean>()
    val gameFinished: LiveData<Boolean>
        get() = _gamefinished

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        resetList()
        nextWord()
        _score.value = 0
        _gamefinished.value = false
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS)
                    _buzzer.value = BuzzType.COUNTDOWN_PANIC
            }

            override fun onFinish() {
                _buzzer.value = BuzzType.GAME_OVER
                _currentTime.value = DONE
                _gamefinished.value = true
            }
        }
        timer.start()

        Log.i("GameViewModel", "GameViewModel Created")
    }

    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)

    }

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        _buzzer.value = BuzzType.NO_BUZZ
        nextWord()
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        _buzzer.value = BuzzType.CORRECT
        nextWord()
    }


    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Log.i("GameViewModel", "GameViewModel Destroyed")
    }

    fun gameFinishCompeted() {
        _gamefinished.value = false
    }

    fun onBuzzFinished() {
        _buzzer.value = BuzzType.NO_BUZZ
    }
}