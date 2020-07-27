package jp.nita.tapchord.Model

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import jp.nita.tapchord.Util.PREF_ATTACK_TIME
import jp.nita.tapchord.Util.PREF_DECAY_TIME
import jp.nita.tapchord.Util.PREF_ENABLE_ENVELOPE
import jp.nita.tapchord.Util.PREF_RELEASE_TIME
import jp.nita.tapchord.Util.PREF_SAMPLING_RATE
import jp.nita.tapchord.Util.PREF_SOUND_RANGE
import jp.nita.tapchord.Util.PREF_SUSTAIN_LEVEL
import jp.nita.tapchord.Util.PREF_VOLUME
import jp.nita.tapchord.Util.PREF_WAVEFORM
import jp.nita.tapchord.Util.convertRawNotesToFrequencies
import jp.nita.tapchord.Util.prefValue
import jp.nita.tapchord.Util.valueOfSamplingRate
import jp.nita.tapchord.Util.valueOfVolume
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

class Sound(var notesInRange: Array<Int>, cont: Context) {
    var mFrequencies: Array<Int>
    var mGenerator: WaveGenerator? = null
    var mMode = SOUND_MODE.ATTACK
    var mTerm: Long = 0
    var mModeTerm: Long = 0

    enum class SOUND_MODE(id: Int) {
        ATTACK(0),
        DECAY(1),
        SUSTAIN(2),
        RELEASE(3),
        FINISHED(4)
    }

    var mVolume = 0
    var mSampleRate = 4000
    var mWaveForm = 0
    var mWaveLength = 0
    var mSoundRange = 0
    var mLength = 0
    var mAttack = 0
    var mDecay = 0
    var mSustain = 0
    var mRelease = 0
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
        finish(SOUND_MODE.RELEASE)
    }

    fun release() {
        finish(SOUND_MODE.FINISHED)
    }

    fun getWave(length: Int): ShortArray {
        val wave = ShortArray(length)
        for (i in 0 until length) {
            var sum = 0.0
            when (mWaveForm) {
                0, 1, 2, 3, 4, 5 -> for (frequency in mFrequencies) {
                    sum += wave(mTerm.toDouble() * frequency / mSampleRate, mWaveForm)
                }
                6 -> {
                    var j = 0
                    while (j < notesInRange.size) {
                        sum += shepardTone(mTerm, notesInRange[j], mFrequencies[j], mSampleRate, mSoundRange, mWaveForm)
                        j++
                    }
                }
            }
            sum *= mVolume / 400.0 * Short.MAX_VALUE
            if (mEnableEnvelope) {
                if (mMode == SOUND_MODE.ATTACK && mModeTerm >= mAttackLength) {
                    mModeTerm = 0
                    mMode = SOUND_MODE.DECAY
                }
                if (mMode == SOUND_MODE.DECAY && mModeTerm >= mDecayLength) {
                    mModeTerm = 0
                    mMode = SOUND_MODE.SUSTAIN
                }
                if (mMode == SOUND_MODE.RELEASE && mModeTerm > mReleaseLength) {
                    mModeTerm = 0
                    mMode = SOUND_MODE.FINISHED
                }
                sum = if (mMode == SOUND_MODE.ATTACK) {
                    sum * (mModeTerm.toDouble() / mAttackLength.toDouble())
                } else if (mMode == SOUND_MODE.DECAY) {
                    (sum * (mDecayLength - mModeTerm).toDouble() / mDecayLength.toDouble()
                            + sum * mSustainLevel * mModeTerm.toDouble() / mDecayLength.toDouble())
                } else if (mMode == SOUND_MODE.SUSTAIN) {
                    sum * mSustainLevel
                } else if (mMode == SOUND_MODE.RELEASE) {
                    sum * ((mReleaseLength - mModeTerm).toDouble() / mReleaseLength.toDouble()) * mSustainLevel
                } else {
                    0.0
                }
            }
            if (sum >= Short.MAX_VALUE) sum = Short.MAX_VALUE.toDouble()
            if (sum <= -Short.MAX_VALUE) sum = -Short.MAX_VALUE.toDouble()
            wave[i] = sum.toShort()
            mTerm++
            if (mMode != SOUND_MODE.SUSTAIN) mModeTerm++
            if (mTerm >= mSampleRate) mTerm -= mSampleRate.toLong()
        }
        return wave
    }

    fun updatePreferenceValues() {
        mVolume = valueOfVolume(prefValue(mContext, PREF_VOLUME, 30))
        mSoundRange = prefValue(mContext, PREF_SOUND_RANGE, 0)
        mSampleRate = valueOfSamplingRate(prefValue(mContext, PREF_SAMPLING_RATE, 0))
        mWaveForm = prefValue(mContext, PREF_WAVEFORM, 0)
        mEnableEnvelope = prefValue(mContext, PREF_ENABLE_ENVELOPE, 0) > 0
    }

    fun updateEnvelopePrefValues() {
        if (mEnableEnvelope) {
            mAttack = prefValue(mContext, PREF_ATTACK_TIME, 0)
            mDecay = prefValue(mContext, PREF_DECAY_TIME, 0)
            mSustain = prefValue(mContext, PREF_SUSTAIN_LEVEL, 0) + 100
            mRelease = prefValue(mContext, PREF_RELEASE_TIME, 0)

            mAttackLength = mAttack * mSampleRate / 1000
            mDecayLength = mDecay * mSampleRate / 1000
            mSustainLength = mSampleRate
            mReleaseLength = mRelease * mSampleRate / 1000
            mSustainLevel = mSustain.toDouble() / 100.0
        } else {
            mAttackLength = 0
            mDecayLength = 0
            mSustainLength = mSampleRate
            mReleaseLength = 0
            mSustainLevel = 1.0
        }
    }

    inner class WaveGenerator : Thread() {
        override fun run() {
            synchronized(modeProcess) {
                if (mTrack != null) {
                    mTrack!!.pause()
                    mTrack!!.stop()
                    mTrack!!.release()
                    mTrack = null
                }
                updatePreferenceValues()
                updateEnvelopePrefValues()
                if (mEnableEnvelope) {
                    mLength = AudioTrack.getMinBufferSize(mSampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT)
                    mTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                            mSampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            mLength,
                            AudioTrack.MODE_STREAM)
                    mMode = SOUND_MODE.ATTACK
                    mTerm = 0
                    mModeTerm = 0
                    startedPlayingTime = System.currentTimeMillis()
                    requiredTime = startedPlayingTime - tappedTime
                    mTrack!!.play()
                    while (mMode <= SOUND_MODE.RELEASE) {
                        mTrack!!.write(getWave(mLength), 0, mLength)
                    }
                    try {
                        sleep(mRelease.toLong())
                    } catch (ignore: InterruptedException) {
                    }
                } else {
                    mLength = AudioTrack.getMinBufferSize(mSampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT)
                    mTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                            mSampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            mLength,
                            AudioTrack.MODE_STREAM)
                    mMode = SOUND_MODE.SUSTAIN
                    mTerm = 0
                    mModeTerm = 0
                    startedPlayingTime = System.currentTimeMillis()
                    requiredTime = startedPlayingTime - tappedTime
                    mTrack!!.play()
                    while (mMode <= SOUND_MODE.SUSTAIN) {
                        mTrack!!.write(getWave(mLength), 0, mLength)
                    }
                }
                mTrack!!.stop()
                startedPlayingTime = System.currentTimeMillis()
                requiredTime = startedPlayingTime - tappedTime
                mTrack!!.release()
                mTrack = null
            }
        }
    }

    fun finish(modeParam: SOUND_MODE) {
        if (mTrack != null && mTrack!!.state == AudioTrack.STATE_INITIALIZED && !mEnableEnvelope) {
            try {
                // できるだけ早く音を止めるためだけのpauseなので、例外が発生しても無視する
                // TODO: 何とかした方がいいと思う
                mTrack!!.pause()
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
        var mTrack: AudioTrack? = null
        val gaussianTable = DoubleArray(100)
        val modeProcess = Any()
        fun wave(t: Double, which: Int): Double {
//		Log.i("Sound", "t:"+t);
            return when (which) {
                1 -> t - floor(t + 1 / 2.0)
                2 -> {
                    val tt = t - floor(t)
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
                3 -> if (sin(2.0 * Math.PI * t) > 0) 0.5 else -0.5
                4 -> if (t - floor(t) < 1.0 / 4.0) 0.5 else -0.5
                5 -> if (t - floor(t) < 1.0 / 8.0) 0.5 else -0.5
                else -> sin(2.0 * Math.PI * t)
            }
        }

        fun shepardTone(term: Long, noteInRange: Int, frequency: Int, sampleRate: Int, soundRange: Int, which: Int): Double {
            if (which == 6) {
                var result = 0.0
                var gaussian = 0.0
                val t = term.toDouble() * frequency / sampleRate
                val note = (noteInRange - soundRange - 6)

                gaussian = gaussianTable[note - 24 + gaussianTable.size / 2]
                result += sin(0.5 * Math.PI * t) * gaussian

                gaussian = gaussianTable[note - 12 + gaussianTable.size / 2]
                result += sin(1.0 * Math.PI * t) * gaussian

                gaussian = gaussianTable[note + gaussianTable.size / 2]
                result += sin(2.0 * Math.PI * t) * gaussian

                gaussian = gaussianTable[note + 12 + gaussianTable.size / 2]
                result += sin(4.0 * Math.PI * t) * gaussian

                gaussian = gaussianTable[note + 24 + gaussianTable.size / 2]
                result += sin(8.0 * Math.PI * t) * gaussian

                return result
            }
            return sin(2.0 * Math.PI * term * frequency / sampleRate)
        }

        const val SIGMA = 0.45
        val SIGMA_SQRT_PI = SIGMA * sqrt(2 * Math.PI)
        const val SIGMA_SQUARED_2 = 2 * SIGMA * SIGMA
        fun gaussian(t: Double): Double {
            val tOn12 = t / 12.0
            return 1.0 / SIGMA_SQRT_PI * exp(-tOn12 * tOn12 / SIGMA_SQUARED_2)
        }
    }

    init {
        mFrequencies = convertRawNotesToFrequencies(notesInRange)
        mContext = cont
        for (i in gaussianTable.indices) {
            gaussianTable[i] = gaussian(i - gaussianTable.size / 2.toDouble())
        }
    }
}