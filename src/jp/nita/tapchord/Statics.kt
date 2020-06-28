package jp.nita.tapchord

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import java.util.*

object Statics {
    const val NIHIL = 0
    const val FARAWAY = 256
    const val SITUATION_NORMAL = 0
    const val SITUATION_TRANSPOSE = 1
    const val SITUATION_TRANSPOSE_MOVING = 2

    // final public static int SITUATION_PULLING=3;
    const val CHORD_BUTTON = 1
    const val STATUSBAR_BUTTON = 2
    const val SCROLL_NOB = 3
    const val TOOLBAR_BUTTON = 4
    const val TOOL_BAR = 5
    const val TRANSPOSE_SCALE_BUTTON = 6
    const val STATUS_BAR = 7
    const val KEYBOARD_INDICATORS = 8
    const val COLOR_ABSOLUTE_CYAN = -128
    const val COLOR_BLACK = -6
    const val COLOR_DARKGRAY = -5
    const val COLOR_GRAY = -4
    const val COLOR_PASTELGRAY = -3
    const val COLOR_LIGHTGRAY = -2
    const val COLOR_WHITE = -1
    const val COLOR_ABSOLUTE_LIGHT = 0
    const val COLOR_RED = 1
    const val COLOR_YELLOW = 2
    const val COLOR_GREEN = 3
    const val COLOR_BLUE = 4
    const val COLOR_ORANGE = 5
    const val COLOR_PURPLE = 6
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
    fun color(which: Int, pressed: Int, dark: Boolean): Int {
        var r: Int
        var g: Int
        var b: Int
        if (!dark) {
            when (which) {
                COLOR_BLACK -> {
                    r = 0x00
                    g = 0x00
                    b = 0x00
                }
                COLOR_DARKGRAY -> {
                    r = 0x40
                    g = 0x40
                    b = 0x40
                }
                COLOR_GRAY -> {
                    r = 0x80
                    g = 0x80
                    b = 0x80
                }
                COLOR_PASTELGRAY -> {
                    r = 0xF8
                    g = 0xF8
                    b = 0xF8
                }
                COLOR_LIGHTGRAY -> {
                    r = 0xE0
                    g = 0xE0
                    b = 0xE0
                }
                COLOR_ABSOLUTE_LIGHT -> {
                    r = 0xFF
                    g = 0xFF
                    b = 0xFF
                }
                COLOR_RED -> {
                    r = 0xFF
                    g = 0xA0
                    b = 0xE0 // SUM=0x28
                }
                COLOR_YELLOW -> {
                    r = 0xFF
                    g = 0xFF
                    b = 0x70
                }
                COLOR_GREEN -> {
                    r = 0xA0
                    g = 0xFF
                    b = 0xA0
                }
                COLOR_BLUE -> {
                    r = 0xA0
                    g = 0xE0
                    b = 0xFF
                }
                COLOR_ORANGE -> {
                    r = 0xFF
                    g = 0xC0
                    b = 0x80
                }
                COLOR_PURPLE -> {
                    r = 0xC0
                    g = 0xC0
                    b = 0xFF
                }
                else -> {
                    r = 0xFF
                    g = 0xFF
                    b = 0xFF
                }
            }
            when (pressed) {
                1 -> {
                    r /= 2
                    g /= 2
                    b /= 2
                }
                -1 -> {
                    r = 256 - (256 - r) / 2
                    g = 256 - (256 - g) / 2
                    b = 256 - (256 - b) / 2
                }
                else -> {
                }
            }
        } else {
            when (which) {
                COLOR_BLACK -> {
                    r = 0
                    g = 80
                    b = 80
                }
                COLOR_DARKGRAY -> {
                    r = 0
                    g = 48
                    b = 48
                }
                COLOR_GRAY -> {
                    r = 0
                    g = 32
                    b = 32
                }
                COLOR_PASTELGRAY -> {
                    r = 0
                    g = 16
                    b = 16
                }
                COLOR_LIGHTGRAY -> {
                    r = 0
                    g = 8
                    b = 8
                }
                COLOR_ABSOLUTE_LIGHT -> {
                    r = 0
                    g = 64
                    b = 64
                }
                COLOR_RED, COLOR_YELLOW, COLOR_GREEN, COLOR_BLUE, COLOR_ORANGE, COLOR_PURPLE -> {
                    r = 0
                    g = 32
                    b = 32
                }
                else -> {
                    r = 0
                    g = 0
                    b = 0
                }
            }
            when (pressed) {
                -1 -> {
                    g /= 2
                    b /= 2
                }
                1 -> {
                    r = 128 - (128 - r) / 2
                    g = 128 - (128 - g) / 2
                    b = 128 - (128 - b) / 2
                }
                else -> {
                }
            }
        }
        if (r > 255) r = 255
        if (g > 255) g = 255
        if (b > 255) b = 255
        if (which == COLOR_ABSOLUTE_CYAN) {
            r = 32
            g = 196
            b = 196
        }
        return Color.argb(255, r, g, b)
    }

    @JvmStatic
    fun rectOfButtonArea(width: Int, height: Int): RectF {
        val vert = height * 7 / 35f
        return RectF(0F, vert, width.toFloat(), height - vert)
    }

    @JvmStatic
    fun rectOfButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): RectF {
        val vert = height * 7 / 35f
        val pX = width / 2 + x * vert
        val pY = height / 2 + y * vert
        return RectF(pX - vert / 2 + vert / 14 + scroll, pY - vert / 2 + vert / 14,
                pX + vert / 2 - vert / 14 + scroll, pY + vert / 2 - vert / 14)
    }

    @JvmStatic
    fun pointOfButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): Point {
        val vert = (height * 7 / 35f).toInt()
        val resX = Math.floor((x - width / 2 - scroll).toFloat() / vert + 0.5).toInt()
        val resY = Math.floor((y - height / 2).toFloat() / vert + 0.5).toInt()
        return Point(resX, resY)
    }

    fun topLeftOfButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): Point {
        val vert = (height * 7 / 35f).toInt()
        val resL = x / vert
        val resT = y / vert
        return Point(resL, resT)
    }

    fun bottomRightButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): Point {
        val vert = (height * 7 / 35f).toInt()
        val resB = (width - x) / vert
        val resR = (height - y) / vert
        return Point(resB, resR)
    }

    @JvmStatic
    fun scrollMax(width: Int, height: Int): Int {
        val vert = height / 35f
        val max = vert * 7 * 15
        return (max - width.toFloat()).toInt() / 2
    }

    @JvmStatic
    fun rectOfScrollBar(width: Int, height: Int, showingRate: Float): RectF {
        val vert = height / 35f
        val max = vert * 7 * 15
        val hidingDelta = vert * (1.0f - showingRate) * 7
        return RectF(vert * 2, vert * 30.5f + hidingDelta, vert * 2 + max / 5, vert * 32.5f + hidingDelta)
    }

    @JvmStatic
    fun rectOfScrollNob(pos: Int, upper: Int, width: Int, height: Int, showingRate: Float): RectF {
        val vert = height / 35f
        val max = vert * 7 * 15
        val nob = width / 5.toFloat()
        val x = vert * 2 + max / 10 - pos / 5
        val hidingDelta = vert * (1.0f - showingRate) * 7
        return RectF(x - nob / 2, vert * 30f - upper + hidingDelta, x + nob / 2, vert * 33f - upper + hidingDelta)
    }

    @JvmStatic
    fun radiusOfButton(height: Int): Int {
        return height * 7 / 70 - 8
    }

    @JvmStatic
    fun rectOfStatusBar(width: Int, height: Int, showingRate: Float): RectF {
        val vert = height * 7 / 35f
        val hidingDelta = vert * (1.0f - showingRate)
        return RectF(0F, 0 - hidingDelta, width.toFloat(), height * 7 / 35 - hidingDelta)
    }

    @JvmStatic
    fun rectOfStatusBarButton(x: Int, y: Int, width: Int, height: Int, showingRate: Float): RectF {
        val vert = height * 7 / 35f
        val pX = x * vert + vert / 2
        val hidingDelta = vert * (1.0f - showingRate)
        return RectF(pX - vert / 2 + vert / 14, 0 + vert / 14 - hidingDelta, pX + vert / 2 - vert / 14,
                vert - vert / 14 - hidingDelta)
    }

    @JvmStatic
    fun rectOfToolbar(width: Int, height: Int, showingRate: Float): RectF {
        val vert = height * 7 / 35f
        val hidingDelta = vert * (1.0f - showingRate)
        return RectF(0F, height * 28 / 35 + hidingDelta, width.toFloat(), height + hidingDelta)
    }

    @JvmStatic
    fun rectOfToolbarButton(x: Int, y: Int, width: Int, height: Int, showingRate: Float): RectF {
        val vert = height * 7 / 35f
        val pX = x * vert + vert / 2
        val hidingDelta = vert * (1.0f - showingRate)
        return RectF(width - (pX + vert / 2) + vert / 14, height - vert + vert / 14 + hidingDelta,
                width - (pX - vert / 2) - vert / 14, height - vert / 14 + hidingDelta)
    }

    @JvmStatic
    fun rectOfToolbarTransposingButton(x: Int, y: Int, width: Int, height: Int, showingRate: Float): RectF {
        val vert = height * 7 / 35f
        val pX = x * vert + vert / 2
        val hidingDelta = vert * (1.0f - showingRate)
        return RectF(pX - vert / 2 + vert / 14, height - vert + vert / 14 + hidingDelta, pX + vert / 2 - vert / 14,
                height - vert / 14 + hidingDelta)
    }

    @JvmStatic
    fun rectOfKeyboardIndicator(i: Int, shrink: Int, width: Int, height: Int, showingRate: Float): RectF? {
        val vert = height / 35f
        val hidingDelta = vert * 7 * (1.0f - showingRate)
        var r: RectF? = null
        when (i % 12) {
            0 -> r = RectF(width - vert * 21 + shrink, vert * 4 + shrink - hidingDelta, width - vert * 19 - shrink,
                    vert * 6 - shrink - hidingDelta)
            2 -> r = RectF(width - vert * 18 + shrink, vert * 4 + shrink - hidingDelta, width - vert * 16 - shrink,
                    vert * 6 - shrink - hidingDelta)
            4 -> r = RectF(width - vert * 15 + shrink, vert * 4 + shrink - hidingDelta, width - vert * 13 - shrink,
                    vert * 6 - shrink - hidingDelta)
            5 -> r = RectF(width - vert * 12 + shrink, vert * 4 + shrink - hidingDelta, width - vert * 10 - shrink,
                    vert * 6 - shrink - hidingDelta)
            7 -> r = RectF(width - vert * 9 + shrink, vert * 4 + shrink - hidingDelta, width - vert * 7 - shrink,
                    vert * 6 - shrink - hidingDelta)
            9 -> r = RectF(width - vert * 6 + shrink, vert * 4 + shrink - hidingDelta, width - vert * 4 - shrink,
                    vert * 6 - shrink - hidingDelta)
            11 -> r = RectF(width - vert * 3 + shrink, vert * 4 + shrink - hidingDelta, width - vert * 1 - shrink,
                    vert * 6 - shrink - hidingDelta)
            1 -> r = RectF(width - vert * 19.5f + shrink, vert * 1 + shrink - hidingDelta, width - vert * 17.5f - shrink,
                    vert * 3 - shrink - hidingDelta)
            3 -> r = RectF(width - vert * 16.5f + shrink, vert * 1 + shrink - hidingDelta, width - vert * 14.5f - shrink,
                    vert * 3 - shrink - hidingDelta)
            6 -> r = RectF(width - vert * 10.5f + shrink, vert * 1 + shrink - hidingDelta, width - vert * 8.5f - shrink,
                    vert * 3 - shrink - hidingDelta)
            8 -> r = RectF(width - vert * 7.5f + shrink, vert * 1 + shrink - hidingDelta, width - vert * 5.5f - shrink,
                    vert * 3 - shrink - hidingDelta)
            10 -> r = RectF(width - vert * 4.5f + shrink, vert * 1 + shrink - hidingDelta, width - vert * 2.5f - shrink,
                    vert * 3 - shrink - hidingDelta)
        }
        return r
    }

    @JvmStatic
    fun rectOfKeyboardIndicators(shrink: Int, width: Int, height: Int, showingRate: Float): RectF {
        val vert = height / 35f
        val hidingDelta = vert * 7 * (1.0f - showingRate)
        return RectF(width - vert * 23 + shrink, 0F, width.toFloat(), vert * 7 - shrink - hidingDelta)
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
    fun preferenceValue(context: Context, key: String?, def: Int): Int {
        val pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
        return pref.getInt(key, def)
    }

    @JvmStatic
    fun setPreferenceValue(context: Context, key: String?, `val`: Int) {
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

    @JvmStatic
    fun RectFToRect(rectf: RectF): Rect {
        return Rect(rectf.left.toInt(), rectf.top.toInt(), rectf.right.toInt(), rectf.bottom.toInt())
    }
}