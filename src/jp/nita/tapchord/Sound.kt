package jp.nita.tapchord

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

class Sound(var notesInRange: Array<Int>, cont: Context) {
    var mFrequencies: Array<Int>
    var mGenerator: WaveGenerator? = null
    var mMode = 0
    var mTerm: Long = 0
    var mModeTerm: Long = 0

    val MODE_ATTACK = 0
    val MODE_DECAY = 1
    val MODE_SUSTAIN = 2
    val MODE_RELEASE = 3
    val MODE_FINISHED = 4

    var mVolume = 0
    var mSampleRate = 4000
    var mWaveForm = 0
    var mWaveLength = 0
    var mSoundRange = 0
    var mLength = 0
    var mAttackLength = 0
    var mDecayLength = 0
    var mSustainLength = 0
    var mReleaseLength = 0
    var mEnableEnvelope = false
    var mSustainLevel = 0.0
    val mContext: Context

    fun play() {
        mGenerator = WaveGenerator()
        mGenerator!!.start()
    }

    fun stop() {
        finish(MODE_RELEASE)
    }

    fun release() {
        finish(MODE_FINISHED)
    }

    fun getWave(length: Int): ShortArray {
        val w = ShortArray(length)
        for (i in 0 until length) {
            var s = 0.0
            when (mWaveForm) {
                0, 1, 2, 3, 4, 5 -> for (frequency in mFrequencies) {
                    s += wave(mTerm.toDouble() * frequency / mSampleRate, mWaveForm)
                }
                6 -> {
                    var j = 0
                    while (j < notesInRange.size) {
                        s += shepardTone(mTerm, notesInRange[j], mFrequencies[j], mSampleRate, mSoundRange, mWaveForm)
                        j++
                    }
                }
            }
            s *= mVolume / 400.0 * Short.MAX_VALUE
            if (mEnableEnvelope) {
                if (mMode == MODE_ATTACK && mModeTerm >= mAttackLength) {
                    mModeTerm = 0
                    mMode = MODE_DECAY
                }
                if (mMode == MODE_DECAY && mModeTerm >= mDecayLength) {
                    mModeTerm = 0
                    mMode = MODE_SUSTAIN
                }
                if (mMode == MODE_RELEASE && mModeTerm > mReleaseLength) {
                    mModeTerm = 0
                    mMode = MODE_FINISHED
                }
                s = if (mMode == MODE_ATTACK) {
                    s * (mModeTerm.toDouble() / mAttackLength.toDouble())
                } else if (mMode == MODE_DECAY) {
                    (s * (mDecayLength - mModeTerm).toDouble() / mDecayLength.toDouble()
                            + s * mSustainLevel * mModeTerm.toDouble() / mDecayLength.toDouble())
                } else if (mMode == MODE_SUSTAIN) {
                    s * mSustainLevel
                } else if (mMode == MODE_RELEASE) {
                    s * ((mReleaseLength - mModeTerm).toDouble() / mReleaseLength.toDouble()) * mSustainLevel
                } else {
                    0.0
                }
            }
            if (s >= Short.MAX_VALUE) s = Short.MAX_VALUE.toDouble()
            if (s <= -Short.MAX_VALUE) s = -Short.MAX_VALUE.toDouble()
            w[i] = s.toShort()
            mTerm++
            if (mMode != MODE_SUSTAIN) mModeTerm++
            if (mTerm >= mSampleRate) mTerm -= mSampleRate.toLong()
        }
        return w
    }

    inner class WaveGenerator : Thread() {
        override fun run() {
            synchronized(modeProcess) {
                if (track != null) {
                    track!!.pause()
                    track!!.stop()
                    track!!.release()
                    track = null
                }
                mVolume = Statics.valueOfVolume(Statics.preferenceValue(mContext, Statics.PREF_VOLUME, 30))
                mSoundRange = Statics.preferenceValue(mContext, Statics.PREF_SOUND_RANGE, 0)
                mSampleRate = Statics
                        .valueOfSamplingRate(Statics.preferenceValue(mContext, Statics.PREF_SAMPLING_RATE, 0))
                mWaveForm = Statics.preferenceValue(mContext, Statics.PREF_WAVEFORM, 0)
                mEnableEnvelope = Statics.preferenceValue(mContext, Statics.PREF_ENABLE_ENVELOPE, 0) > 0
                if (mEnableEnvelope) {
                    val attack = Statics.preferenceValue(mContext, Statics.PREF_ATTACK_TIME, 0)
                    val decay = Statics.preferenceValue(mContext, Statics.PREF_DECAY_TIME, 0)
                    val sustain = Statics.preferenceValue(mContext, Statics.PREF_SUSTAIN_LEVEL, 0) + 100
                    val release = Statics.preferenceValue(mContext, Statics.PREF_RELEASE_TIME, 0)
                    mAttackLength = attack * mSampleRate / 1000
                    mDecayLength = decay * mSampleRate / 1000
                    mSustainLength = mSampleRate
                    mReleaseLength = release * mSampleRate / 1000
                    mSustainLevel = sustain.toDouble() / 100.0
                    mLength = AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT)
                    track = AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, mLength,
                            AudioTrack.MODE_STREAM)
                    mMode = MODE_ATTACK
                    mTerm = 0
                    mModeTerm = 0
                    startedPlayingTime = System.currentTimeMillis()
                    requiredTime = startedPlayingTime - tappedTime
                    track!!.play()
                    while (mMode <= MODE_RELEASE) {
                        track!!.write(getWave(mLength), 0, mLength)
                    }
                    try {
                        sleep(release.toLong())
                    } catch (ignore: InterruptedException) {
                    }
                } else {
                    mAttackLength = 0
                    mDecayLength = 0
                    mSustainLength = mSampleRate
                    mReleaseLength = 0
                    mSustainLevel = 1.0
                    mLength = AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT)
                    track = AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, mLength,
                            AudioTrack.MODE_STREAM)
                    mMode = MODE_SUSTAIN
                    mTerm = 0
                    mModeTerm = 0
                    startedPlayingTime = System.currentTimeMillis()
                    requiredTime = startedPlayingTime - tappedTime
                    track!!.play()
                    while (mMode <= MODE_SUSTAIN) {
                        track!!.write(getWave(mLength), 0, mLength)
                    }
                }
                track!!.stop()
                startedPlayingTime = System.currentTimeMillis()
                requiredTime = startedPlayingTime - tappedTime
                track!!.release()
                track = null
            }
        }
    }

    fun finish(modeParam: Int) {
        if (track != null && track!!.state == AudioTrack.STATE_INITIALIZED && !mEnableEnvelope) {
            try {
                // できるだけ早く音を止めるためだけのpauseなので、例外が発生しても無視する
                // TODO: 何とかした方がいいと思う
                track!!.pause()
            } catch (ignore: IllegalStateException) {
            }
        }
        mModeTerm = 0
        mMode = modeParam
    }

    companion object {
        @JvmField
        var tappedTime: Long = 0
        var startedPlayingTime: Long = 0
        @JvmField
        var requiredTime: Long = 0
        var track: AudioTrack? = null
        val gaussianTable = DoubleArray(100)
        val modeProcess = Any()
        fun wave(t: Double, which: Int): Double {
//		Log.i("Sound", "t:"+t);
            return when (which) {
                1 -> t - Math.floor(t + 1 / 2.0)
                2 -> {
                    val tt = t - Math.floor(t)
                    if (tt < 0.25) {
                        tt * 4
                    } else if (tt < 0.50) {
                        (0.5 - tt) * 4
                    } else if (tt < 0.75) {
                        (-tt + 0.5) * 4
                    } else {
                        (-1 + tt) * 4
                    }
                }
                3 -> if (Math.sin(2.0 * Math.PI * t) > 0) 0.5 else -0.5
                4 -> if (t - Math.floor(t) < 1.0 / 4.0) 0.5 else -0.5
                5 -> if (t - Math.floor(t) < 1.0 / 8.0) 0.5 else -0.5
                else -> Math.sin(2.0 * Math.PI * t)
            }
        }

        val LOG_2 = Math.log(2.0)
        fun shepardTone(term: Long, noteInRange: Int, frequency: Int, sampleRate: Int, soundRange: Int, which: Int): Double {
            if (which == 6) {
                var r = 0.0
                var g = 0.0
                val t = term.toDouble() * frequency / sampleRate
                val n = (noteInRange - soundRange - 6)
                g = gaussianTable[n - 24 + gaussianTable.size / 2]
                r += Math.sin(0.5 * Math.PI * t) * g
                g = gaussianTable[n - 12 + gaussianTable.size / 2]
                r += Math.sin(1.0 * Math.PI * t) * g
                g = gaussianTable[n + gaussianTable.size / 2]
                r += Math.sin(2.0 * Math.PI * t) * g
                g = gaussianTable[n + 12 + gaussianTable.size / 2]
                r += Math.sin(4.0 * Math.PI * t) * g
                g = gaussianTable[n + 24 + gaussianTable.size / 2]
                r += Math.sin(8.0 * Math.PI * t) * g
                return r
            }
            return Math.sin(2.0 * Math.PI * term * frequency / sampleRate)
        }

        const val SIGMA = 0.45
        val SIGMA_SQRT_PI = SIGMA * Math.sqrt(2 * Math.PI)
        const val SIGMA_SQUARED_2 = 2 * SIGMA * SIGMA
        fun gaussian(t: Double): Double {
            val tOn12 = t / 12.0
            return 1.0 / SIGMA_SQRT_PI * Math.exp(-tOn12 * tOn12 / SIGMA_SQUARED_2)
        }
    }

    init {
        mFrequencies = Statics.convertRawNotesToFrequencies(notesInRange)
        mContext = cont
        for (i in gaussianTable.indices) {
            gaussianTable[i] = gaussian(i - gaussianTable.size / 2.toDouble())
        }
    }
}