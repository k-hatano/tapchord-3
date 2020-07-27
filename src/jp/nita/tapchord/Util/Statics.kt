package jp.nita.tapchord.Util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import jp.nita.tapchord.R
import java.util.*

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

val NOTES_5TH = arrayOf("Fbb", "Cbb", "Gbb", "Dbb", "Abb", "Ebb", "Bbb", "Fb", "Cb", "Gb", "Db", "Ab",
        "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#", "G#", "D#", "A#", "E#", "B#", "Fx", "Cx", "Gx",
        "Dx", "Ax", "Ex", "Bx", "F#x")

val SCALES = arrayOf("b7", "b6", "b5", "b4", "b3", "b2", "b1", "#b0", "#1", "#2", "#3", "#4", "#5",
        "#6", "#7")

val TENSIONS = arrayOf("add9", "-5/aug", "7", "M7")

public fun colorOf(which: COLOR, pressed: Int, dark: Boolean): Int {
    var color: Triple<Int, Int, Int>
    if (!dark) {
        color = when (which) {
            COLOR.BLACK -> Triple(0x00, 0x00, 0x00)
            COLOR.DARKGRAY -> Triple(0x40, 0x40, 0x40)
            COLOR.GRAY -> Triple(0x80, 0x80, 0x80)
            COLOR.PASTELGRAY -> Triple(0xF8, 0xF8, 0xF8)
            COLOR.LIGHTGRAY -> Triple(0xE0, 0xE0, 0xE0)
            COLOR.ABSOLUTE_LIGHT -> Triple(0xFF, 0xFF, 0xFF)
            COLOR.RED -> Triple(0xFF, 0xA0, 0xE0)
            COLOR.YELLOW -> Triple(0xFF, 0xFF, 0x70)
            COLOR.GREEN -> Triple(0xA0, 0xFF, 0xA0)
            COLOR.BLUE -> Triple(0xA0, 0xE0, 0xFF)
            COLOR.ORANGE -> Triple(0xFF, 0xC0, 0x80)
            COLOR.PURPLE -> Triple(0xC0, 0xC0, 0xFF)
            else -> Triple(0xFF, 0xFF, 0xFF)
        }

        if (pressed == 1) {
            color = Triple(color.first / 2, color.second / 2, color.third / 2)
        }
        if (pressed == -1) {
            color = Triple(256 - (256 - color.first) / 2, 256 - (256 - color.second) / 2, 256 - (256 - color.third) / 2)
        }
    } else {
        color = when (which) {
            COLOR.BLACK -> Triple(0, 80, 80)
            COLOR.DARKGRAY -> Triple(0, 48, 48)
            COLOR.GRAY -> Triple(0, 32, 32)
            COLOR.PASTELGRAY -> Triple(0, 16, 16)
            COLOR.LIGHTGRAY -> Triple(0, 8, 8)
            COLOR.ABSOLUTE_LIGHT -> Triple(0, 64, 64)
            COLOR.RED, COLOR.YELLOW, COLOR.GREEN, COLOR.BLUE, COLOR.ORANGE, COLOR.PURPLE -> Triple(0, 32, 32)
            else -> Triple(0, 0, 0)
        }

        if (pressed == 1) {
            color = Triple(color.first, color.second / 2, color.third / 2)
        }
        if (pressed == -1) {
            color = Triple(128 - (128 - color.first) / 2, 128 - (128 - color.second) / 2, 128 - (128 - color.third) / 2)
        }
    }

    if (which == COLOR.ABSOLUTE_CYAN) {
        color = Triple(32, 196, 196)
    }

    var (red, green, blue) = color
    if (red > 255) {
        red = 255
    }
    if (green > 255) {
        green = 255
    }
    if (blue > 255) {
        blue = 255
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

fun notesOfChord(x: Int, y: Int, tensions: IntArray): Array<Int> {
    var x = x
    val notes: MutableList<Int> = ArrayList()
    if (y >= 1) x += 3
    if (y == -1) {
        if (tensions[1] > 0) {
            notes.add(normalizePositive(x * 7, 12))
            notes.add(normalizePositive(x * 7 + 4, 12))
            notes.add(normalizePositive(x * 7 + 8, 12))
        } else {
            notes.add(normalizePositive(x * 7, 12))
            notes.add(normalizePositive(x * 7 + 5, 12))
            notes.add(normalizePositive(x * 7 + 7, 12))
        }
    } else if (y == 0) {
        notes.add(normalizePositive(x * 7, 12))
        notes.add(normalizePositive(x * 7 + 4, 12))
        if (tensions[1] > 0) {
            notes.add(normalizePositive(x * 7 + 6, 12))
        } else {
            notes.add(normalizePositive(x * 7 + 7, 12))
        }
    } else if (y == 1) {
        notes.add(normalizePositive(x * 7, 12))
        notes.add(normalizePositive(x * 7 + 3, 12))
        if (tensions[1] > 0) {
            notes.add(normalizePositive(x * 7 + 6, 12))
        } else {
            notes.add(normalizePositive(x * 7 + 7, 12))
        }
    }
    if (tensions[0] > 0) {
        notes.add(normalizePositive(x * 7 + 2, 12))
    }
    if (tensions[2] > 0 && tensions[3] > 0) {
        notes.add(normalizePositive(x * 7 + 9, 12))
    } else if (tensions[2] > 0) {
        notes.add(normalizePositive(x * 7 + 10, 12))
    } else if (tensions[3] > 0) {
        notes.add(normalizePositive(x * 7 + 11, 12))
    }
    return notes.toTypedArray()
}

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

fun stringOfScale(i: Int): String {
    return if (i < -7 || i > 7) "" else SCALES[i + 7]
}

fun prefValue(context: Context, key: String?, def: Int): Int {
    val pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
    return pref.getInt(key, def)
}

fun setPrefValue(context: Context, key: String?, value: Int) {
    val pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
    val editor = pref.edit()
    editor.putInt(key, value)
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

fun onOrOffString(context: Context, value: Int): String {
    return if (value > 0) context.getString(R.string.on) else context.getString(R.string.off)
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

fun stringOfSoundRange(soundRange: Int): String {
    return shortStringOfSoundRange(soundRange) + " - " + shortStringOfSoundRange(soundRange + 11)
}

fun stringOfSingleTime(time: Int, context: Context): String {
    return "" + time / 1000.0f + context.getString(R.string.settings_attack_decay_release_time_seconds)
}

fun stringOfPercentage(sustainLevel: Int, context: Context): String {
    return "" + (sustainLevel + 100) + context.getString(R.string.settings_sustain_level_percent)
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

fun normalizePositive(n: Int, max: Int): Int {
    var tmpN: Int = n
    while (true) {
        var looping = false
        if (tmpN < 0) {
            tmpN += max
            looping = true
        }
        if (tmpN >= max) {
            tmpN -= max
            looping = true
        }
        if (!looping) break
    }
    return tmpN
}

fun normalize(n: Int, max: Int): Int {
    var tmpN: Int = n
    while (true) {
        var looping = false
        if (tmpN < -max / 2) {
            tmpN += max
            looping = true
        }
        if (tmpN > max / 2) {
            tmpN -= max
            looping = true
        }
        if (!looping) break
    }
    return tmpN
}