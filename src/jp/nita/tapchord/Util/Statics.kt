package jp.nita.tapchord.Util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import jp.nita.tapchord.R
import java.util.*

object Statics {
    const val NIHIL = 0
    const val FARAWAY = 256

    enum class SITUATION(id: Int) {
        NORMAL(0),
        TRANSPOSE(1),
        TRANSPOSE_MOVING(2)
    }

    enum class OBJECT(id: Int) {
        CHORD_BUTTON(1),
        STATUSBAR_BUTTON(2),
        SCROLL_NOB(3),
        TOOLBAR_BUTTON(4),
        TOOLBAR(5),
        TRANSPOSE_SCALE_BUTTON(6),
        STATUS_BAR(7),
        KEYBOARD_INDICATORS(8)
    }

    enum class COLOR(id: Int) {
        ABSOLUTE_CYAN(-128),
        BLACK(-6),
        DARKGRAY(-5),
        GRAY(-4),
        PASTELGRAY(-3),
        LIGHTGRAY(-2),
        WHITE(-1),
        ABSOLUTE_LIGHT(0),
        RED(1),
        YELLOW(2),
        GREEN(3),
        BLUE(4),
        ORANGE(5),
        PURPLE(6)
    }

    const val SUS4 = "sus4"
    const val MINOR = "m"
    const val PREF_KEY = "tapchord"
    const val PREF_SCALE = "scale"
    const val PREF_DARKEN = "darken"
    const val PREF_VIBRATION = "vibration"
    const val PREF_VOLUME = "volume"
    const val PREF_SAMPLING_RATE = "sampling_rate"
    const val PREF_WAVEFORM = "waveform"
    const val PREF_SOUND_RANGE = "sound_range"
    const val PREF_ATTACK_TIME = "attack_time"
    const val PREF_DECAY_TIME = "decay_time"
    const val PREF_SUSTAIN_LEVEL = "sustain_level"
    const val PREF_RELEASE_TIME = "release_time"
    const val PREF_ENABLE_ENVELOPE = "enable_envelope"
    const val PREF_ANIMATION_QUALITY = "animation_quality"
    const val PREF_NEVER_SHOW_ALPHA_RELEASED = "never_show_alpha_released"
    const val VIBRATION_LENGTH = 40
    val NOTES_C2B = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    @JvmField
    val NOTES_5TH = arrayOf("Fbb", "Cbb", "Gbb", "Dbb", "Abb", "Ebb", "Bbb", "Fb", "Cb", "Gb", "Db", "Ab",
            "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#", "G#", "D#", "A#", "E#", "B#", "Fx", "Cx", "Gx",
            "Dx", "Ax", "Ex", "Bx", "F#x")

    @JvmField
    val SCALES = arrayOf("b7", "b6", "b5", "b4", "b3", "b2", "b1", "#b0", "#1", "#2", "#3", "#4", "#5",
            "#6", "#7")

    @JvmField
    val TENSIONS = arrayOf("add9", "-5/aug", "7", "M7")

    @JvmStatic
    fun color(which: COLOR, pressed: Int, dark: Boolean): Int {
        var red: Int
        var green: Int
        var blue: Int
        if (!dark) {
            when (which) {
                COLOR.BLACK -> {
                    red = 0x00; green = 0x00; blue = 0x00
                }
                COLOR.DARKGRAY -> {
                    red = 0x40; green = 0x40; blue = 0x40
                }
                COLOR.GRAY -> {
                    red = 0x80; green = 0x80; blue = 0x80
                }
                COLOR.PASTELGRAY -> {
                    red = 0xF8; green = 0xF8; blue = 0xF8
                }
                COLOR.LIGHTGRAY -> {
                    red = 0xE0; green = 0xE0; blue = 0xE0
                }
                COLOR.ABSOLUTE_LIGHT -> {
                    red = 0xFF; green = 0xFF; blue = 0xFF
                }
                COLOR.RED -> {
                    red = 0xFF; green = 0xA0; blue = 0xE0
                }
                COLOR.YELLOW -> {
                    red = 0xFF; green = 0xFF; blue = 0x70
                }
                COLOR.GREEN -> {
                    red = 0xA0; green = 0xFF; blue = 0xA0
                }
                COLOR.BLUE -> {
                    red = 0xA0; green = 0xE0; blue = 0xFF
                }
                COLOR.ORANGE -> {
                    red = 0xFF; green = 0xC0; blue = 0x80
                }
                COLOR.PURPLE -> {
                    red = 0xC0; green = 0xC0; blue = 0xFF
                }
                else -> {
                    red = 0xFF; green = 0xFF; blue = 0xFF
                }
            }
            when (pressed) {
                1 -> {
                    red /= 2; green /= 2; blue /= 2
                }
                -1 -> {
                    red = 256 - (256 - red) / 2; green = 256 - (256 - green) / 2; blue = 256 - (256 - blue) / 2
                }
                else -> {
                }
            }
        } else {
            when (which) {
                COLOR.BLACK -> {
                    red = 0; green = 80; blue = 80
                }
                COLOR.DARKGRAY -> {
                    red = 0; green = 48; blue = 48
                }
                COLOR.GRAY -> {
                    red = 0; green = 32; blue = 32
                }
                COLOR.PASTELGRAY -> {
                    red = 0; green = 16; blue = 16
                }
                COLOR.LIGHTGRAY -> {
                    red = 0; green = 8; blue = 8
                }
                COLOR.ABSOLUTE_LIGHT -> {
                    red = 0; green = 64; blue = 64
                }
                COLOR.RED, COLOR.YELLOW, COLOR.GREEN, COLOR.BLUE, COLOR.ORANGE, COLOR.PURPLE -> {
                    red = 0; green = 32; blue = 32
                }
                else -> {
                    red = 0; green = 0; blue = 0
                }
            }
            when (pressed) {
                -1 -> {
                    green /= 2; blue /= 2
                }
                1 -> {
                    red = 128 - (128 - red) / 2; green = 128 - (128 - green) / 2; blue = 128 - (128 - blue) / 2
                }
                else -> {
                }
            }
        }
        if (red > 255) red = 255
        if (green > 255) green = 255
        if (blue > 255) blue = 255
        if (which == COLOR.ABSOLUTE_CYAN) {
            red = 32; green = 196; blue = 196
        }
        return Color.argb(255, red, green, blue)
    }


    fun noteInSoundRange(note: Int, soundRange: Int): Int {
        var n = note
        while (n < soundRange || n >= soundRange + 12) {
            if (n < soundRange) n += 12
            if (n >= soundRange + 12) n -= 12
        }
        return n
    }

    fun frequencyOfNote(note: Int, soundRange: Int): Int {
        val f = 440.0
        var n = note
        while (n < soundRange || n >= soundRange + 12) {
            if (n < soundRange) n += 12
            if (n >= soundRange + 12) n -= 12
        }
        return Math.round(f * Math.pow(2.0, (n - 9) / 12.0)).toInt()
    }

    fun frequencyOfRawNote(note: Int): Int {
        val f = 440.0
        return Math.round(f * Math.pow(2.0, (note - 9) / 12.0)).toInt()
    }

    @JvmStatic
    fun notesOfChord(x: Int, y: Int, tensions: IntArray): Array<Int> {
        var x = x
        val notes: MutableList<Int> = ArrayList()
        if (y >= 1) x += 3
        if (y == -1) {
            if (tensions[1] > 0) {
                notes.add((x * 7 + 360) % 12)
                notes.add((x * 7 + 4 + 360) % 12)
                notes.add((x * 7 + 8 + 360) % 12)
            } else {
                notes.add((x * 7 + 360) % 12)
                notes.add((x * 7 + 5 + 360) % 12)
                notes.add((x * 7 + 7 + 360) % 12)
            }
        } else if (y == 0) {
            notes.add((x * 7 + 360) % 12)
            notes.add((x * 7 + 4 + 360) % 12)
            if (tensions[1] > 0) {
                notes.add((x * 7 + 6 + 360) % 12)
            } else {
                notes.add((x * 7 + 7 + 360) % 12)
            }
        } else if (y == 1) {
            notes.add((x * 7 + 360) % 12)
            notes.add((x * 7 + 3 + 360) % 12)
            if (tensions[1] > 0) {
                notes.add((x * 7 + 6 + 360) % 12)
            } else {
                notes.add((x * 7 + 7 + 360) % 12)
            }
        }
        if (tensions[0] > 0) {
            notes.add((x * 7 + 2 + 360) % 12)
        }
        if (tensions[2] > 0 && tensions[3] > 0) {
            notes.add((x * 7 + 9 + 360) % 12)
        } else if (tensions[2] > 0) {
            notes.add((x * 7 + 10 + 360) % 12)
        } else if (tensions[3] > 0) {
            notes.add((x * 7 + 11 + 360) % 12)
        }
        return notes.toTypedArray()
    }

    @JvmStatic
    fun convertNotesToNotesInRange(notes: Array<Int>, soundRange: Int): Array<Int> {
        val notesInRange: MutableList<Int> = ArrayList()
        for (note in notes) {
            notesInRange.add(noteInSoundRange(note, soundRange))
        }
        return notesInRange.toTypedArray()
    }

    fun convertRawNotesToFrequencies(notes: Array<Int>): Array<Int> {
        val freqs: MutableList<Int> = ArrayList()
        for (note in notes) {
            freqs.add(frequencyOfRawNote(note))
        }
        return freqs.toTypedArray()
    }

    fun convertNotesToFrequencies(notes: Array<Int>, soundRange: Int): Array<Int> {
        val freqs: MutableList<Int> = ArrayList()
        for (note in notes) {
            freqs.add(frequencyOfNote(note, soundRange))
        }
        return freqs.toTypedArray()
    }

    @JvmStatic
    fun stringOfScale(i: Int): String {
        return if (i < -7 || i > 7) "" else SCALES[i + 7]
    }

    @JvmStatic
    fun prefValue(context: Context, key: String?, def: Int): Int {
        val pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
        return pref.getInt(key, def)
    }

    @JvmStatic
    fun setPrefValue(context: Context, key: String?, `val`: Int) {
        val pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(key, `val`)
        editor.commit()
    }

    fun stringOfAnimationQuality(aq: Int, context: Context): String {
        return when (aq) {
            -1 -> context.getString(R.string.settings_animation_quality_low)
            0 -> context.getString(R.string.settings_animation_quality_medium)
            1 -> context.getString(R.string.settings_animation_quality_high)
            else -> context.getString(R.string.settings_animation_quality_medium)
        }
    }

    fun longStringOfScale(i: Int): String {
        return when (i) {
            -7 -> "b7 : Cb / Abm"
            -6 -> "b6 : Gb / Ebm"
            -5 -> "b5 : Db / Bbm"
            -4 -> "b4 : Ab / Fm"
            -3 -> "b3 : Eb / Cm"
            -2 -> "b2 : Bb / Gm"
            -1 -> "b1 : F / Dm"
            0 -> "#b0 : C / Am"
            1 -> "#1 : G / Em"
            2 -> "#2 : D / Bm"
            3 -> "#3 : A / F#m"
            4 -> "#4 : E / C#m"
            5 -> "#5 : G / G#m"
            6 -> "#6 : F# / D#m"
            7 -> "#7 : C# / A#m"
            else -> ""
        }
    }

    fun onOrOffString(context: Context, v: Int): String {
        return if (v > 0) context.getString(R.string.on) else context.getString(R.string.off)
    }

    fun valueOfVolume(i: Int): Int {
        return i + 50
    }

    fun valueOfSamplingRate(i: Int): Int {
        return when (i) {
            -3 -> 8000
            -2 -> 16000
            -1 -> 22050
            0 -> 44100
            else -> 0
        }
    }

    @JvmStatic
    fun valueOfWaveform(i: Int, context: Context): String {
        return when (i) {
            0 -> context.getString(R.string.settings_waveform_sine_wave)
            1 -> context.getString(R.string.settings_waveform_sawtooth_wave)
            2 -> context.getString(R.string.settings_waveform_triangle_wave)
            3 -> context.getString(R.string.settings_waveform_square_wave)
            4 -> context.getString(R.string.settings_waveform_fourth_pulse_wave)
            5 -> context.getString(R.string.settings_waveform_eighth_pulse_wave)
            6 -> context.getString(R.string.settings_waveform_shepard_tone)
            else -> ""
        }
    }

    fun valueOfAttackDecayReleaseTime(i: Int): Float {
        return i / 1000.0f
    }

    @JvmStatic
    fun stringOfSoundRange(soundRange: Int): String {
        return shortStringOfSoundRange(soundRange) + " - " + shortStringOfSoundRange(soundRange + 11)
    }

    fun stringOfSingleTime(t: Int, context: Context): String {
        return "" + t / 1000.0f + context.getString(R.string.settings_attack_decay_release_time_seconds)
    }

    fun stringOfSustainLevel(s: Int, context: Context): String {
        return "" + (s + 100) + context.getString(R.string.settings_sustain_level_percent)
    }

    fun stringOfEnvelope(e: Int, a: Int, d: Int, s: Int, r: Int, context: Context): String {
        return if (e > 0) {
            ("" + a / 1000.0f + context.getString(R.string.settings_attack_decay_release_time_seconds) + " - "
                    + d / 1000.0f + context.getString(R.string.settings_attack_decay_release_time_seconds) + " - "
                    + (s + 100) + context.getString(R.string.settings_sustain_level_percent) + " - " + r / 1000.0f + context.getString(R.string.settings_attack_decay_release_time_seconds))
        } else {
            context.getString(R.string.disabled)
        }
    }

    fun shortStringOfSoundRange(soundRange: Int): String {
        var soundRange = soundRange
        var octave = 4
        while (soundRange < 0 || soundRange >= 12) {
            if (soundRange < 0) {
                octave--
                soundRange += 12
            }
            if (soundRange >= 12) {
                octave++
                soundRange -= 12
            }
        }
        return "" + NOTES_C2B[soundRange] + octave
    }

}