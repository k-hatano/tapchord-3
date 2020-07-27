package jp.nita.tapchord.View

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Handler
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import jp.nita.tapchord.Activity.MainActivity
import jp.nita.tapchord.Activity.SettingsActivity
import jp.nita.tapchord.Model.Shape
import jp.nita.tapchord.Model.Shape.Companion.maxLifetime
import jp.nita.tapchord.Model.Sound
import jp.nita.tapchord.R
import jp.nita.tapchord.Util.*
import jp.nita.tapchord.Util.Dimensions.RectFToRect
import jp.nita.tapchord.Util.Dimensions.pointOfButton
import jp.nita.tapchord.Util.Dimensions.radiusOfButton
import jp.nita.tapchord.Util.Dimensions.rectOfButton
import jp.nita.tapchord.Util.Dimensions.rectOfButtonArea
import jp.nita.tapchord.Util.Dimensions.rectOfKeyboardIndicator
import jp.nita.tapchord.Util.Dimensions.rectOfKeyboardIndicators
import jp.nita.tapchord.Util.Dimensions.rectOfScrollBar
import jp.nita.tapchord.Util.Dimensions.rectOfScrollNob
import jp.nita.tapchord.Util.Dimensions.rectOfStatusBar
import jp.nita.tapchord.Util.Dimensions.rectOfStatusBarButton
import jp.nita.tapchord.Util.Dimensions.rectOfToolbar
import jp.nita.tapchord.Util.Dimensions.rectOfToolbarButton
import jp.nita.tapchord.Util.Dimensions.rectOfToolbarTransposingButton
import jp.nita.tapchord.Util.Dimensions.scrollMax
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class TapChordView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var mWidth = 0
    var mHeight = 0
    var mOriginalIndices: Pair<Int, Int> = Pair(0, 0)
    var mOriginalScroll = 0
    var mSituation: SITUATION
    var mDestination: SITUATION
    var mStep: Int
    var mScroll: Int
    var mUpper: Int
    var mDestinationScale = 0
    private var mPlaying: Int
    var mPlayingIndices: Pair<Int, Int> = Pair(0, 0)
    var mTappedX = 0
    var mDestinationScroll = 0
    var mPlayingID = 0
    var mDarken: Boolean
    var mVibration = false
    var mIsIndicatorsTapped = false
    var mIsScrolling = false
    var mIsScalePullingDown = false
    var mStatusBarFlags = intArrayOf(0, 0, 0, 0)

    val STATUSBAR_KEY_CODES = intArrayOf(KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4)
    val KEY_CODES = arrayOf(intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0),
            intArrayOf(KeyEvent.KEYCODE_Q, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_Z),
            intArrayOf(KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_X),
            intArrayOf(KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_C),
            intArrayOf(KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_F, KeyEvent.KEYCODE_V),
            intArrayOf(KeyEvent.KEYCODE_T, KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_B),
            intArrayOf(KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_N),
            intArrayOf(KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_M),
            intArrayOf(KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_COMMA),
            intArrayOf(KeyEvent.KEYCODE_O, KeyEvent.KEYCODE_L, KeyEvent.KEYCODE_PERIOD),
            intArrayOf(KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_SEMICOLON, KeyEvent.KEYCODE_SLASH),
            intArrayOf(KeyEvent.KEYCODE_GRAVE, KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_BACKSLASH),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 0))
    val SPECIAL_KEY_CODES = intArrayOf(KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT, KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER)

    var mScale = 0
    var mSoundRange = 0
    var mPulling = 0
    var mLastTapped = -1
    var mLastTappedTime: Long = -1
    var mToolbarPressed = -1
    var mScalePressed = FARAWAY
    var mStepMax = 1.0f
    var mBarsShowingRate = 1.0f
    var mFlashEffectStep = 0
    private var mVibrator: Vibrator
    val mHandler = Handler()
    var mNotesOfChord = arrayOf<Int>()
    var mSound: Sound? = null // 本当はビューの中で再生処理なんてやっちゃいけない
    var mTaps = SparseArray<OBJECT>()
    private val mShapes: MutableList<Shape> = ArrayList()
    private val mKeyWatcher = Any()
    var mStopTimer: Timer? = null
    var mCancelSwitchingStatusBarTimer: Timer? = null
    var mCancelSpecialKeyTimer: Timer? = null
    var mIsShiftKeyPressed = false

    fun init(context: Context?) {
        preferenceValues
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        var color: COLOR
        var pressed: Int
        var width: Float
        val paint = Paint()
        val textPaint = Paint()
        var rect: RectF?
        var str: String? = ""

        val fontMetrics = textPaint.fontMetrics
        val fontHeight = fontMetrics.ascent + fontMetrics.descent

        mWidth = canvas.width
        mHeight = canvas.height
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        val rad = radiusOfButton(mHeight)
        textPaint.isAntiAlias = true
        textPaint.color = colorOf(COLOR.BLACK, 0, mDarken)
        textPaint.textSize = rad / 2.toFloat()
        rect = RectF(0F, 0F, mWidth.toFloat(), mHeight.toFloat())
        paint.color = colorOf(COLOR.WHITE, 0, mDarken)
        canvas.drawRect(rect, paint)

        if (mSituation == SITUATION.TRANSPOSE || mSituation == SITUATION.TRANSPOSE_MOVING || mDestination == SITUATION.TRANSPOSE || mDestination == SITUATION.TRANSPOSE_MOVING) {
            paint.style = Paint.Style.FILL
            paint.color = colorOf(COLOR.PASTELGRAY, 0, mDarken)
            canvas.drawRect(rectOfToolbar(mWidth, mHeight, 1.0f), paint)

            pressed = if (mToolbarPressed == 0) 1 else 0
            paint.color = colorOf(COLOR.PURPLE, pressed, mDarken)
            rect = rectOfToolbarButton(0, 0, mWidth, mHeight, 1.0f)
            canvas.drawOval(rect, paint)

            str = context.getString(R.string.ok)
            width = textPaint.measureText(str)
            canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)
            pressed = if (mToolbarPressed == 1) 1 else 0
            paint.color = colorOf(COLOR.PURPLE, pressed, mDarken)
            rect = rectOfToolbarButton(1, 0, mWidth, mHeight, 1.0f)
            canvas.drawOval(rect, paint)

            str = SCALES[7]
            width = textPaint.measureText(str)
            canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)

            pressed = if (mToolbarPressed == 2) 1 else 0
            paint.color = colorOf(COLOR.PURPLE, pressed, mDarken)
            rect = rectOfToolbarTransposingButton(0, 0, mWidth, mHeight, 1.0f)
            canvas.drawOval(rect, paint)

            str = context.getString(R.string.settings_volume_short)
            width = textPaint.measureText(str)
            canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)

            pressed = if (mToolbarPressed == 3) 1 else 0
            paint.color = colorOf(COLOR.PURPLE, pressed, mDarken)
            rect = rectOfToolbarTransposingButton(1, 0, mWidth, mHeight, 1.0f)
            canvas.drawOval(rect, paint)

            str = context.getString(R.string.settings_sound_range_short)
            width = textPaint.measureText(str)
            canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)

            pressed = if (mToolbarPressed == 4) 1 else 0
            paint.color = colorOf(COLOR.PURPLE, pressed, mDarken)
            rect = rectOfToolbarTransposingButton(2, 0, mWidth, mHeight, 1.0f)
            canvas.drawOval(rect, paint)

            str = context.getString(R.string.settings_waveform_short)
            width = textPaint.measureText(str)
            canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)
        }
        var delta: Int = mScroll / (mHeight / 5)
        for (x in -7 - delta..7 - delta) {
            pressed = if (x == mScalePressed) 1 else 0
            paint.color = colorOf(COLOR.LIGHTGRAY, pressed, mDarken)
            rect = rectOfButton(x, -2, mWidth, mHeight, mScroll)
            canvas.drawOval(rect, paint)

            if (x + mScale < -7) {
                textPaint.color = colorOf(COLOR.GRAY, 0, mDarken)
                str = stringOfScale(x + mScale + 12)
            } else if (x + mScale > 7) {
                textPaint.color = colorOf(COLOR.GRAY, 0, mDarken)
                str = stringOfScale(x + mScale - 12)
            } else {
                textPaint.color = colorOf(COLOR.BLACK, 0, mDarken)
                str = stringOfScale(x + mScale)
            }
            if (x == 0 && mIsScalePullingDown) {
                str = context.getString(R.string.ok)
            }
            width = textPaint.measureText(str)
            if (rect != null) {
                canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)
            }
        }
        textPaint.color = colorOf(COLOR.BLACK, 0, mDarken)
        delta = mScroll / (mHeight / 5)
        for (x in -7 - delta..7 - delta) {
            var maj = x + 15 + mScale
            if (maj < 0) maj += 12
            if (maj >= 36) maj -= 12
            var min = x + 18 + mScale
            if (min < 0) min += 12
            if (min >= 36) min -= 12
            val xx = normalizePositive(x, 12)
            for (y in -1..1) {
                color = COLOR.ABSOLUTE_LIGHT
                pressed = 0
                if (mPlaying > 0 && mPlayingIndices.first == x && mPlayingIndices.second == y) pressed = 1
                when (xx) {
                    11, 0, 1 -> color = COLOR.RED
                    2, 3, 4 -> color = COLOR.YELLOW
                    5, 6, 7 -> color = COLOR.GREEN
                    8, 9, 10 -> color = COLOR.BLUE
                }
                if (mSituation == SITUATION.TRANSPOSE || mDestination == SITUATION.TRANSPOSE) {
                    color = COLOR.LIGHTGRAY
                } else if (mDarken && (mIsScrolling || mPulling > 0)) {
                    color = COLOR.DARKGRAY
                }
                paint.color = colorOf(color, pressed, mDarken)
                rect = rectOfButton(x, y, mWidth, mHeight, mScroll)
                canvas.drawOval(rect, paint)
                when (y) {
                    -1 -> str = NOTES_5TH[maj] + SUS4
                    0 -> str = NOTES_5TH[maj]
                    1 -> str = NOTES_5TH[min] + MINOR
                }
                width = textPaint.measureText(str)
                if (rect != null) {
                    canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)
                }
            }
        }
        if (mSituation == SITUATION.TRANSPOSE || mDestination == SITUATION.TRANSPOSE) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = mHeight / 160.0f
            for (x in -7..7) {
                val xx = normalizePositive(x, 12)
                for (y in -1..1) {
                    color = COLOR.RED
                    when (xx) {
                        11, 0, 1 -> color = COLOR.RED
                        2, 3, 4 -> color = COLOR.YELLOW
                        5, 6, 7 -> color = COLOR.GREEN
                        8, 9, 10 -> color = COLOR.BLUE
                    }
                    if (mDarken) {
                        color = COLOR.DARKGRAY
                    }
                    paint.color = colorOf(color, 0, mDarken)
                    var sc = mScroll
                    if (mSituation == SITUATION.TRANSPOSE_MOVING) sc = 0
                    rect = rectOfButton(x, y, mWidth, mHeight, sc)
                    canvas.drawOval(rect, paint)
                }
            }
            if (mDarken) {
                paint.color = colorOf(COLOR.DARKGRAY, 0, true)
            } else {
                paint.color = colorOf(COLOR.RED, 0, false)
            }
            var sc = mScroll
            if (mSituation == SITUATION.TRANSPOSE_MOVING) sc = 0
            rect = rectOfButton(0, -2, mWidth, mHeight, sc)
            canvas.drawOval(rect, paint)
        }
        paint.style = Paint.Style.FILL
        paint.color = colorOf(COLOR.LIGHTGRAY, 0, mDarken)
        canvas.drawRect(rectOfStatusBar(mWidth, mHeight, mBarsShowingRate), paint)

        paint.color = colorOf(COLOR.LIGHTGRAY, 0, mDarken)
        canvas.drawRect(rectOfToolbar(mWidth, mHeight, mBarsShowingRate), paint)

        for (x in 0..3) {
            pressed = if (mStatusBarFlags[x] > 0) 1 else 0
            paint.color = colorOf(COLOR.ORANGE, pressed, mDarken)
            rect = rectOfStatusBarButton(x, 0, mWidth, mHeight, mBarsShowingRate)
            canvas.drawOval(rect, paint)

            if (mStatusBarFlags[x] >= 2) textPaint.color = colorOf(COLOR.ORANGE, 0, mDarken) else textPaint.color = colorOf(COLOR.BLACK, 0, mDarken)
            str = TENSIONS[x]
            if (x == 2 && mStatusBarFlags[3] > 0) str = "6"
            if (x == 3 && mStatusBarFlags[2] > 0) str = "6"
            width = textPaint.measureText(str)
            canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)
        }
        textPaint.color = colorOf(COLOR.BLACK, 0, mDarken)
        for (x in 0..2) {
            pressed = if (mToolbarPressed == x) 1 else 0
            paint.color = colorOf(COLOR.PURPLE, pressed, mDarken)
            rect = rectOfToolbarButton(x, 0, mWidth, mHeight, mBarsShowingRate)
            canvas.drawOval(rect, paint)

            str = when (x) {
                0 -> context.getString(R.string.action_settings)
                1 -> context.getString(R.string.darken)
                2 -> stringOfScale(mScale)
                else -> ""
            }
            width = textPaint.measureText(str)
            canvas.drawText(str, rect.centerX() - width / 2, rect.centerY() - fontHeight / 2, textPaint)
        }
        if (rectOfStatusBarButton(3, 0, mWidth, mHeight, mBarsShowingRate).right < rectOfKeyboardIndicator(0, 0, mWidth, mHeight, mBarsShowingRate)!!.left) {
            for (x in 0..11) {
                paint.color = colorOf(COLOR.GRAY, 0, mDarken)
                if (mIsIndicatorsTapped) paint.color = colorOf(COLOR.DARKGRAY, 0, mDarken)
                rect = rectOfKeyboardIndicator(x, 0, mWidth, mHeight, mBarsShowingRate)
                canvas.drawOval(rect, paint)
            }

            for (integer in mNotesOfChord) {
                paint.color = colorOf(COLOR.ABSOLUTE_LIGHT, 0, mDarken)
                rect = rectOfKeyboardIndicator(integer!!, 2, mWidth, mHeight, mBarsShowingRate)
                canvas.drawOval(rect, paint)
            }
        }
        paint.color = colorOf(COLOR.GRAY, 0, mDarken)
        rect = rectOfScrollBar(mWidth, mHeight, mBarsShowingRate)
        canvas.drawRect(rect, paint)

        paint.color = colorOf(COLOR.DARKGRAY, 0, mDarken)
        rect = rectOfScrollNob(mScroll, mUpper, mWidth, mHeight, mBarsShowingRate)
        canvas.drawRect(rect, paint)

        val DEG2RAD = (Math.PI * 2 / 360.0)
        if (mDarken) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = mHeight / 25.toFloat()
            paint.color = colorOf(COLOR.ABSOLUTE_CYAN, 1, mDarken)
            for (i in mShapes.indices) {
                if (i >= mShapes.size) {
                    break
                }
                val shape = mShapes[i]
                paint.alpha = 255 * shape.mLifeTime / maxLifetime
                val sx = shape.mCenter.x
                val sy = shape.mCenter.y
                if (shape.mStyle == Shape.SHAPE_STYLE.LINE) {
                    val r = shape.mRadStart
                    val ax = sx - (cos(r * DEG2RAD) * mWidth).toFloat()
                    val ay = sy - (sin(r * DEG2RAD) * mWidth).toFloat()
                    val bx = sx + (cos(r * DEG2RAD) * mWidth).toFloat()
                    val by = sy + (sin(r * DEG2RAD) * mWidth).toFloat()
                    canvas.drawLine(ax, ay, bx, by, paint)
                }

                if (shape.mStyle == Shape.SHAPE_STYLE.CIRCLE) {
                    canvas.drawCircle(sx, sy,
                            mHeight * (0.2f
                                    + (maxLifetime - shape.mLifeTime).toFloat() / maxLifetime)
                                    * 0.8f,
                            paint)
                }

                if (shape.mStyle == Shape.SHAPE_STYLE.TRIANGLE) {
                    val l = (mHeight
                            * (0.3f + (maxLifetime - shape.mLifeTime).toFloat() / maxLifetime)
                            * 0.7f)
                    val r = (shape.mRadStart * shape.mLifeTime
                            + shape.mRadEnd * (maxLifetime - shape.mLifeTime)) / maxLifetime.toFloat()
                    val ax = sx + (cos(r * DEG2RAD) * l).toFloat()
                    val ay = sy + (sin(r * DEG2RAD) * l).toFloat()
                    val bx = sx + (cos((r + 120) * DEG2RAD) * l).toFloat()
                    val by = sy + (sin((r + 120) * DEG2RAD) * l).toFloat()
                    val cx = sx + (cos((r + 240) * DEG2RAD) * l).toFloat()
                    val cy = sy + (sin((r + 240) * DEG2RAD) * l).toFloat()
                    canvas.drawLine(ax, ay, bx, by, paint)
                    canvas.drawLine(bx, by, cx, cy, paint)
                    canvas.drawLine(cx, cy, ax, ay, paint)
                }

                if (shape.mStyle == Shape.SHAPE_STYLE.SQUARE) {
                    val l = (mHeight
                            * (0.3f + (maxLifetime - shape.mLifeTime).toFloat() / maxLifetime)
                            * 0.7f)
                    val r = (shape.mRadStart * shape.mLifeTime
                            + shape.mRadEnd * (maxLifetime - shape.mLifeTime)) / maxLifetime.toFloat()
                    val ax = sx + (cos(r * DEG2RAD) * l).toFloat()
                    val ay = sy + (sin(r * DEG2RAD) * l).toFloat()
                    val bx = sx + (cos((r + 90) * DEG2RAD) * l).toFloat()
                    val by = sy + (sin((r + 90) * DEG2RAD) * l).toFloat()
                    val cx = sx + (cos((r + 180) * DEG2RAD) * l).toFloat()
                    val cy = sy + (sin((r + 180) * DEG2RAD) * l).toFloat()
                    val dx = sx + (cos((r + 270) * DEG2RAD) * l).toFloat()
                    val dy = sy + (sin((r + 270) * DEG2RAD) * l).toFloat()
                    canvas.drawLine(ax, ay, bx, by, paint)
                    canvas.drawLine(bx, by, cx, cy, paint)
                    canvas.drawLine(cx, cy, dx, dy, paint)
                    canvas.drawLine(dx, dy, ax, ay, paint)
                }
            }
        }

        // デバッグ用
        if (debugMode) {
            paint.color = colorOf(COLOR.BLACK, 0, mDarken)
            canvas.drawText("" + Sound.requiredTime, 4f, 20f, textPaint)
        }
    }

    fun actionDown(event: MotionEvent, index: Int): Boolean {
        var rect: RectF
        val x = event.getX(index).toInt()
        val y = event.getY(index).toInt()
        val id = event.getPointerId(index)
        if (mSituation == SITUATION.TRANSPOSE || mSituation == SITUATION.TRANSPOSE_MOVING) {
            for (i in 0..1) {
                rect = rectOfToolbarButton(i, 0, mWidth, mHeight, 1.0f)
                if (rect.contains(x.toFloat(), y.toFloat())) {
                    mToolbarPressed = i
                    mTaps.put(id, OBJECT.TOOLBAR_BUTTON)
                    vibrate()
                    invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
                    return false
                }
            }
            for (i in 0..2) {
                rect = rectOfToolbarTransposingButton(i, 0, mWidth, mHeight, 1.0f)
                if (rect.contains(x.toFloat(), y.toFloat())) {
                    mToolbarPressed = i + 2
                    mTaps.put(id, OBJECT.TOOLBAR_BUTTON)
                    vibrate()
                    invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
                    return false
                }
            }
        } else {
            for (i in 0..2) {
                rect = rectOfToolbarButton(i, 0, mWidth, mHeight, mBarsShowingRate)
                if (rect.contains(x.toFloat(), y.toFloat())) {
                    mToolbarPressed = i
                    mTaps.put(id, OBJECT.TOOLBAR_BUTTON)
                    vibrate()
                    invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
                    return false
                }
            }
        }
        if (mSituation == SITUATION.TRANSPOSE) {
            for (i in -7..7) {
                rect = rectOfButton(i, -2, mWidth, mHeight, mScroll)
                if (rect.contains(x.toFloat(), y.toFloat())) {
                    mIsScalePullingDown = false
                    mScalePressed = i
                    mTaps.put(id, OBJECT.TRANSPOSE_SCALE_BUTTON)
                    vibrate()
                    invalidate()
                    return false
                }
            }
        } else if (mSituation == SITUATION.NORMAL) {
            for (i in 0..3) {
                rect = rectOfStatusBarButton(i, 0, mWidth, mHeight, mBarsShowingRate)
                if (rect.contains(x.toFloat(), y.toFloat())) {
                    if (mLastTapped == i && System.currentTimeMillis() - mLastTappedTime < 400) mStatusBarFlags[i] = 2 else mStatusBarFlags[i] = 1
                    mTaps.put(id, OBJECT.STATUSBAR_BUTTON)
                    vibrate()
                    mLastTapped = i
                    mLastTappedTime = System.currentTimeMillis()
                    invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                    return false
                }
            }
            if (rectOfScrollNob(mScroll, mUpper, mWidth, mHeight, mBarsShowingRate).contains(x.toFloat(), y.toFloat())) {
                mOriginalIndices = Pair(x, y)
                mOriginalScroll = mScroll
                mIsScrolling = true
                mTaps.put(id, OBJECT.SCROLL_NOB)
                vibrate()
                if (mDarken) {
                    invalidate()
                } else {
                    invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
                }
                return false
            } else if (rectOfStatusBarButton(3, 0, mWidth, mHeight, mBarsShowingRate).right < rectOfKeyboardIndicator(0, 0, mWidth, mHeight, mBarsShowingRate)!!.left
                    && rectOfKeyboardIndicators(0, mWidth, mHeight, 1.0f).contains(x.toFloat(), y.toFloat())) {
                if (!mIsIndicatorsTapped) {
                    mIsIndicatorsTapped = true
                    mTaps.put(id, OBJECT.KEYBOARD_INDICATORS)
                    vibrate()
                    invalidate(RectFToRect(rectOfKeyboardIndicators(2, mWidth, mHeight, 1.0f)))
                }
                return false
            } else if (rectOfToolbar(mWidth, mHeight, 1.0f).contains(x.toFloat(), y.toFloat())) {
                if (mScroll == 0) {
                    var statusbarFlag = false
                    for (i in 0..3) {
                        if (mStatusBarFlags[i] >= 2) {
                            mStatusBarFlags[i] = 0
                            statusbarFlag = true
                        }
                    }
                    if (!statusbarFlag && mDarken) {
                        mFlashEffectStep = 1000 / MainActivity.heartBeatInterval
                    }
                } else {
                    mScroll = 0
                }
                mTaps.put(id, OBJECT.TOOLBAR)
                vibrate()
                invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
                return false
            } else if (rectOfStatusBar(mWidth, mHeight, 1.0f).contains(x.toFloat(), y.toFloat())) {
                var statusbarFlag = false
                for (i in 0..3) {
                    if (mStatusBarFlags[i] >= 2) {
                        statusbarFlag = true
                        break
                    }
                }
                if (statusbarFlag) {
                    for (i in 0..3) {
                        if (mStatusBarFlags[i] >= 2) mStatusBarFlags[i] = 0
                    }
                } else {
                    mScroll = 0
                }
                mTaps.put(id, OBJECT.STATUS_BAR)
                vibrate()
                invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                return false
            }
        }
        if (mPlaying <= 0) {
            if (rectOfButtonArea(mWidth, mHeight).contains(x.toFloat(), y.toFloat())) {
                val buttonXY = pointOfButton(x, y, mWidth, mHeight, mScroll)
                if (buttonXY.y >= -1 && buttonXY.y <= 1) {
                    play(buttonXY.x, buttonXY.y)
                    mOriginalScroll = mScroll
                    mTappedX = x
                    mPlayingID = id
                    mTaps.put(mPlayingID, OBJECT.CHORD_BUTTON)
                    vibrate()
                    if (mDarken) {
                        mShapes.add(Shape(PointF(x.toFloat(), y.toFloat())))
                    }
                    invalidate()
                    return true
                }
            }
        } else {
            if (rectOfButtonArea(mWidth, mHeight).contains(x.toFloat(), y.toFloat())) {
                // destinationScroll=originalScroll+(x-tappedX);
                mPulling = 1
                startPullingAnimation()
                invalidate(RectFToRect(rectOfButtonArea(mWidth, mHeight)))
                return true
            }
        }
        return false
    }

    fun actionMove(event: MotionEvent, index: Int): Boolean {
        var chordPressed = false
        var rect: RectF
        val x = event.getX(index).toInt()
        val y = event.getY(index).toInt()
        val id = event.getPointerId(index)
        val kind: OBJECT? = if (id >= 0) mTaps[id] else null
        when (kind) {
            OBJECT.SCROLL_NOB -> {
                if (-y + mOriginalIndices.second > mHeight / 5) {
                    if (mUpper == 0) {
                        vibrate()
                        mScroll = 0
                        mUpper = mHeight / 35 / 5 * 2
                    }
                } else {
                    mScroll = (-x + mOriginalIndices.first) * 5 + mOriginalScroll
                    if (mScroll < -scrollMax(mWidth, mHeight)) mScroll = -scrollMax(mWidth, mHeight)
                    if (mScroll > scrollMax(mWidth, mHeight)) mScroll = scrollMax(mWidth, mHeight)
                    mUpper = 0
                }
                invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
            }
            OBJECT.STATUSBAR_BUTTON -> {
                var i = 0
                while (i < 4) {
                    rect = rectOfStatusBarButton(i, 0, mWidth, mHeight, mBarsShowingRate)
                    if (rect.contains(x.toFloat(), y.toFloat())) {
                        if (mStatusBarFlags[i] >= 2 && mLastTapped == i) {
                            i++
                            continue
                        }
                        if (mStatusBarFlags[i] == 0) vibrate()
                        mStatusBarFlags[i] = 1
                    }
                    i++
                }
                if (y > mHeight * 7 / 35) {
                    var i = 0
                    while (i < 4) {
                        if (mStatusBarFlags[i] == 1) {
                            mStatusBarFlags[i] = 2
                            vibrate()
                        }
                        i++
                    }
                }
                invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
            }
            OBJECT.TOOLBAR_BUTTON -> {
                mToolbarPressed = -1
                if (mSituation == SITUATION.TRANSPOSE || mSituation == SITUATION.TRANSPOSE_MOVING) {
                    rect = rectOfToolbarButton(0, 0, mWidth, mHeight, 1.0f)
                    run {
                        var i = 0
                        while (i < 2) {
                            rect = rectOfToolbarButton(i, 0, mWidth, mHeight, 1.0f)
                            if (rect.contains(x.toFloat(), y.toFloat())) {
                                mToolbarPressed = i
                                mTaps.put(id, OBJECT.TOOLBAR_BUTTON)
                            }
                            i++
                        }
                    }
                    var i = 0
                    while (i < 3) {
                        rect = rectOfToolbarTransposingButton(i, 0, mWidth, mHeight, 1.0f)
                        if (rect.contains(x.toFloat(), y.toFloat())) {
                            mToolbarPressed = i + 2
                            mTaps.put(id, OBJECT.TOOLBAR_BUTTON)
                        }
                        i++
                    }
                } else {
                    var i = 0
                    while (i < 3) {
                        rect = rectOfToolbarButton(i, 0, mWidth, mHeight, mBarsShowingRate)
                        if (rect.contains(x.toFloat(), y.toFloat())) {
                            mToolbarPressed = i
                            mTaps.put(id, OBJECT.TOOLBAR_BUTTON)
                        }
                        i++
                    }
                }
                invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
            }
            OBJECT.CHORD_BUTTON -> {
                if (id == mPlayingID) {
                    if (mSituation == SITUATION.NORMAL) {
                        if (mPulling == 2) {
                            mScroll = mOriginalScroll + (x - mTappedX)
                            if (mScroll < -scrollMax(mWidth, mHeight)) mScroll = -scrollMax(mWidth, mHeight)
                            if (mScroll > scrollMax(mWidth, mHeight)) mScroll = scrollMax(mWidth, mHeight)
                        } else if (mPulling == 1) {
                            mDestinationScroll = mOriginalScroll + (x - mTappedX)
                            if (y > mHeight * 4 / 5) {
                                mDestinationScroll = 0
                            }
                        } else if (Math.abs(x - mTappedX) > mHeight / 5) {
                            mOriginalScroll = mScroll
                            // destinationScroll=originalScroll+(x-tappedX);
                            mPulling = 1
                            mStep = 100 / MainActivity.heartBeatInterval
                            startPullingAnimation()
                        }
                        if (y < mHeight / 5 && event.pointerCount == 1) {
                            var statusbarFlagsModified = false
                            var i = 0
                            while (i < 4) {
                                if (mStatusBarFlags[i] >= 2) {
                                    mStatusBarFlags[i] = 1
                                    statusbarFlagsModified = true
                                }
                                i++
                            }
                            if (statusbarFlagsModified) {
                                vibrate()
                            }
                        }
                        if (y > mHeight * 4 / 5 && mScroll != 0 && event.pointerCount == 1) {
                            mScroll = 0
                            vibrate()
                        }
                    }
                    chordPressed = true
                } else {
                    chordPressed = actionDown(event, index)
                }
                invalidate(RectFToRect(rectOfButtonArea(mWidth, mHeight)))
                invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                invalidate(RectFToRect(rectOfToolbar(mWidth, mHeight, 1.0f)))
            }
            OBJECT.TOOLBAR -> if (mDarken) {
                mFlashEffectStep = 300 / MainActivity.heartBeatInterval
            }
            OBJECT.KEYBOARD_INDICATORS -> {
                run {
                    if (mIsIndicatorsTapped != rectOfKeyboardIndicators(0, mWidth, mHeight, 1.0f).contains(event.x, event.y)) {
                        mIsIndicatorsTapped = rectOfKeyboardIndicators(0, mWidth, mHeight, 1.0f).contains(x.toFloat(), y.toFloat())
                        if (mIsIndicatorsTapped) {
                            vibrate()
                        }
                        invalidate(RectFToRect(rectOfKeyboardIndicators(0, mWidth, mHeight, 1.0f)))
                    }
                }
                if (mScalePressed == 0) {
                    if (!mIsScalePullingDown && y > mHeight * 7 / 35) {
                        mIsScalePullingDown = true
                        vibrate()
                        invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                    } else if (mIsScalePullingDown && y < mHeight * 7 / 35) {
                        mIsScalePullingDown = false
                        vibrate()
                        invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                    }
                }
            }
            OBJECT.TRANSPOSE_SCALE_BUTTON -> if (mScalePressed == 0) {
                if (!mIsScalePullingDown && y > mHeight * 7 / 35) {
                    mIsScalePullingDown = true
                    vibrate()
                    invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                } else if (mIsScalePullingDown && y < mHeight * 7 / 35) {
                    mIsScalePullingDown = false
                    vibrate()
                    invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                }
            }
            OBJECT.STATUS_BAR -> {
            }
            else -> chordPressed = actionDown(event, index)
        }
        return chordPressed
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var chordPressed = false
        Sound.tappedTime = System.currentTimeMillis()
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN ->                 // Log.i("TapChordView","DOWN Count:"+event.getPointerCount());
                actionDown(event, event.actionIndex)
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_POINTER_UP -> {
                // Log.i("TapChordView","MOVE Count:"+event.getPointerCount());
                var upIndex = -1
                if (event.actionMasked == MotionEvent.ACTION_POINTER_UP) {
                    upIndex = event.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                }
                val pointerCount = event.pointerCount
                var index = 0
                while (index < pointerCount) {
                    if (index == upIndex) {
                        index++
                        continue
                    }
                    chordPressed = chordPressed or actionMove(event, index)
                    index++
                }
                if (!chordPressed) {
                    stop()
                    mPlayingID = -1
                    mPulling = 0
                }
            }
            MotionEvent.ACTION_UP -> {
                // Log.i("TapChordView","UP Count:"+event.getPointerCount());
                stop()
                var l = 0
                while (l < 4) {
                    if (mStatusBarFlags[l] == 1) mStatusBarFlags[l] = 0
                    l++
                }
                if (mToolbarPressed >= 0) {
                    toolbarReleased(mToolbarPressed)
                }
                if (mIsScalePullingDown) {
                    mIsScalePullingDown = false
                    mOriginalScroll = mScroll
                    startAnimation(SITUATION.NORMAL)
                } else if (mScalePressed != FARAWAY && mScalePressed != 0) {
                    scaleReleased(mScalePressed)
                }
                if (mIsIndicatorsTapped) {
                    if (rectOfKeyboardIndicators(0, mWidth, mHeight, 1.0f).contains(event.x, event.y)) {
                        keyboardIndicatorsReleased()
                    }
                }
                mToolbarPressed = -1
                mScalePressed = FARAWAY
                mIsIndicatorsTapped = false
                mIsScrolling = false
                mPlayingID = -1
                mPulling = 0
                mUpper = 0
                mTaps = SparseArray<OBJECT>()
                invalidate()
            }
            else -> {
            }
        }
        return true
    }

    fun keyPressed(keyCode: Int, event: KeyEvent): Boolean {
        Log.i("TapChordView", "pressed $keyCode")
        if (event.repeatCount > 0 || event.isLongPress) {
            return true
        }
        synchronized(mKeyWatcher) {
            for (x in 0..14) {
                for (y in 0..2) {
                    if (KEY_CODES[x][y] == 0) continue
                    if (KEY_CODES[x][y] == keyCode) {
                        if (mIsShiftKeyPressed) {
                            var newX = x + 6
                            if (newX > 13) {
                                newX -= 12
                            }
                            playWithKey(newX, y)
                        } else {
                            playWithKey(x, y)
                        }
                        return true
                    }
                }
            }
            for (i in 0..3) {
                if (STATUSBAR_KEY_CODES[i] == keyCode) {
                    switchStatusBarWithKey(i)
                    return true
                }
            }
            for (i in SPECIAL_KEY_CODES.indices) {
                if (SPECIAL_KEY_CODES[i] == keyCode) {
                    performSpecialKey(i)
                    return true
                }
            }
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    mScroll -= mHeight * 7 / 35f.toInt()
                    if (mScroll < -scrollMax(mWidth, mHeight)) mScroll = -scrollMax(mWidth, mHeight)
                    if (mScroll > scrollMax(mWidth, mHeight)) mScroll = scrollMax(mWidth, mHeight)
                    invalidate()
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    mScroll += mHeight * 7 / 35f.toInt()
                    if (mScroll < -scrollMax(mWidth, mHeight)) mScroll = -scrollMax(mWidth, mHeight)
                    if (mScroll > scrollMax(mWidth, mHeight)) mScroll = scrollMax(mWidth, mHeight)
                    invalidate()
                }
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                    mScroll = 0
                    invalidate()
                }
                KeyEvent.KEYCODE_FOCUS -> {
                    var i = 0
                    while (i < 4) {
                        if (mStatusBarFlags[i] >= 2) {
                            mStatusBarFlags[i] = 0
                        }
                        i++
                    }
                    invalidate()
                }
                KeyEvent.KEYCODE_HEADSETHOOK -> {
                    if (mScroll == 0) {
                        var statusbarFlag = false
                        var i = 0
                        while (i < 4) {
                            if (mStatusBarFlags[i] >= 2) {
                                mStatusBarFlags[i] = 0
                                statusbarFlag = true
                            }
                            i++
                        }
                        if (!statusbarFlag && mDarken) {
                            mFlashEffectStep = 1000 / MainActivity.heartBeatInterval
                        }
                    } else {
                        mScroll = 0
                    }
                    invalidate()
                }
                KeyEvent.KEYCODE_CAMERA -> {
                    var statusbarFlag = false
                    if (mScroll != 0) {
                        statusbarFlag = true
                        mScroll = 0
                    }
                    var i = 0
                    while (i < 4) {
                        if (mStatusBarFlags[i] >= 2) {
                            mStatusBarFlags[i] = 0
                            statusbarFlag = true
                        }
                        i++
                    }
                    if (!statusbarFlag && mDarken) {
                        mFlashEffectStep = 1000 / MainActivity.heartBeatInterval
                    }
                    invalidate()
                }
                else -> {
                }
            }
        }
        return false
    }

    fun keyLongPressed(keyCode: Int, event: KeyEvent?): Boolean {
        Log.i("TapChordView", "longPressed $keyCode")
        return false
    }

    fun keyReleased(keyCode: Int, event: KeyEvent): Boolean {
        Log.i("TapChordView", "released $keyCode")
        if (event.repeatCount > 0 || event.isLongPress) {
            return true
        }
        synchronized(mKeyWatcher) {
            for (x in 0..14) {
                for (y in 0..2) {
                    if (KEY_CODES[x][y] == 0) continue
                    if (KEY_CODES[x][y] == keyCode) {
                        stopWithKey(x, y)
                        return true
                    }
                }
            }
            for (i in 0..3) {
                if (STATUSBAR_KEY_CODES[i] == keyCode) {
                    cancelSwitchingStatusBar(i)
                    return true
                }
            }
            for (i in SPECIAL_KEY_CODES.indices) {
                if (SPECIAL_KEY_CODES[i] == keyCode) {
                    cancelSpecialKey(i)
                    return true
                }
            }
        }
        return false
    }

    fun playWithKey(x: Int, y: Int) {
        if (mStopTimer != null) {
            mStopTimer!!.cancel()
            mStopTimer = null
            return
        }
        play(x - 7, y - 1)
    }

    fun stopWithKey(x: Int, y: Int) {
        mStopTimer = Timer()
        mStopTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    stop()
                    mStopTimer = null
                }
            }
        }, 100)
    }

    fun switchStatusBarWithKey(index: Int) {
        if (mCancelSwitchingStatusBarTimer != null) {
            mCancelSwitchingStatusBarTimer!!.cancel()
            mCancelSwitchingStatusBarTimer = null
            return
        }
        if (mLastTapped == index && System.currentTimeMillis() - mLastTappedTime < 400) {
            mStatusBarFlags[index] = 2
        } else {
            mStatusBarFlags[index] = 1
        }
        invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
        mLastTapped = index
        mLastTappedTime = System.currentTimeMillis()
    }

    fun cancelSwitchingStatusBar(index: Int) {
        mCancelSwitchingStatusBarTimer = Timer()
        mCancelSwitchingStatusBarTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    if (mStatusBarFlags[index] == 1) {
                        mStatusBarFlags[index] = 0
                        invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
                    }
                    mCancelSwitchingStatusBarTimer = null
                }
            }
        }, 100)
    }

    fun performSpecialKey(index: Int) {
        if (mCancelSpecialKeyTimer != null) {
            mCancelSpecialKeyTimer!!.cancel()
            mCancelSpecialKeyTimer = null
            return
        }
        when (SPECIAL_KEY_CODES[index]) {
            KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_DEL -> {
                var statusbarFlag = false
                var i = 0
                while (i < 4) {
                    if (mStatusBarFlags[i] >= 2) {
                        statusbarFlag = true
                        break
                    }
                    i++
                }
                if (statusbarFlag) {
                    var i = 0
                    while (i < 4) {
                        if (mStatusBarFlags[i] >= 2) mStatusBarFlags[i] = 0
                        i++
                    }
                } else {
                    mScroll = 0
                }
                invalidate()
            }
            KeyEvent.KEYCODE_SPACE -> {
                if (mScroll == 0) {
                    var i = 0
                    while (i < 4) {
                        if (mStatusBarFlags[i] >= 2) mStatusBarFlags[i] = 0
                        i++
                    }
                } else {
                    mScroll = 0
                }
                invalidate()
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> mIsShiftKeyPressed = true
            KeyEvent.KEYCODE_ENTER -> {
                val intent = Intent(this.context, SettingsActivity::class.java)
                this.context.startActivity(intent)
            }
            else -> {
            }
        }
    }

    fun cancelSpecialKey(index: Int) {
        mCancelSpecialKeyTimer = Timer()
        mCancelSpecialKeyTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    when (SPECIAL_KEY_CODES[index]) {
                        KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> mIsShiftKeyPressed = false
                        else -> {
                        }
                    }
                    mCancelSpecialKeyTimer = null
                }
            }
        }, 100)
    }

    fun toolbarReleased(which: Int) {
        if (mSituation == SITUATION.TRANSPOSE) {
            when (which) {
                0 -> {
                    mOriginalScroll = mScroll
                    startAnimation(SITUATION.NORMAL)
                }
                1 -> startTransposingAnimation(0)
                2 -> showVolumeSettingAlert()
                3 -> showSoundRangeSettingAlert()
                4 -> showWaveformSettingAlert()
            }
        } else {
            when (which) {
                0 -> {
                    val intent = Intent(this.context, SettingsActivity::class.java)
                    this.context.startActivity(intent)
                }
                1 -> {
                    setDarken(!mDarken)
                    if (mDarken) {
                        mFlashEffectStep = 300 / MainActivity.heartBeatInterval
                    }
                }
                2 -> {
                    mIsScalePullingDown = false
                    mOriginalScroll = mScroll
                    startAnimation(SITUATION.TRANSPOSE)
                }
                else -> {
                }
            }
        }
        mToolbarPressed = -1
        invalidate()
    }

    fun scaleReleased(which: Int) {
        var destScale: Int
        destScale = normalize(which + mScale, 12)
        startTransposingAnimation(destScale)
        invalidate()
    }

    fun keyboardIndicatorsReleased() {
        showSoundRangeSettingAlert()
    }

    fun showVolumeSettingAlert() {
        val volume = prefValue(context, PREF_VOLUME, 30) + 50
        val volumeView = TextView(context)
        volumeView.text = "" + volume
        volumeView.setTextAppearance(context, android.R.style.TextAppearance_Inverse)

        val seekBar = SeekBar(context)
        seekBar.progress = volume
        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                volumeView.text = "" + progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(volumeView)
        layout.addView(seekBar)
        layout.setPadding(8, 8, 8, 8)

        Dialogs.dialogBuilder(context, R.string.settings_volume)
                .setView(layout)
                .setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                    val vol = seekBar.progress - 50
                    setPrefValue(this@TapChordView.context, PREF_VOLUME, vol)
                }.setNegativeButton(context.getString(R.string.cancel)) { dialog, which -> }.show()
    }

    fun showSoundRangeSettingAlert() {
        val rangeView = TextView(this.context)
        rangeView.text = "" + stringOfSoundRange(mSoundRange)
        rangeView.setTextAppearance(this.context, android.R.style.TextAppearance_Inverse)

        val seekBar = SeekBar(this.context)
        seekBar.progress = mSoundRange + 24
        seekBar.max = 48
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                rangeView.text = "" + stringOfSoundRange(seekBar.progress - 24)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(rangeView)
        layout.addView(seekBar)
        layout.setPadding(8, 8, 8, 8)

        Dialogs.dialogBuilder(context, R.string.settings_sound_range)
                .setView(layout)
                .setPositiveButton(this.context.getString(R.string.ok)) { dialog, which ->
                    mSoundRange = seekBar.progress - 24
                    setPrefValue(this@TapChordView.context, PREF_SOUND_RANGE,
                            mSoundRange)
                    preferenceValues
                }
                .setNegativeButton(this.context.getString(R.string.cancel)) { dialog, which -> }.show()
        invalidate()
    }

    fun showWaveformSettingAlert() {
        val waveform = prefValue(context, PREF_WAVEFORM, 0)
        val list: Array<CharSequence?> = arrayOfNulls<CharSequence?>(7)
        for (i in list.indices) {
            list[i] = valueOfWaveform(i, context)
        }

        val dialog = Dialogs.dialogBuilder(context, R.string.settings_waveform)
                .setSingleChoiceItems(list, waveform) { dialog, which ->
                    setPrefValue(this@TapChordView.context, PREF_WAVEFORM,
                            which)
                    dialog.dismiss()
                }.create()
        dialog.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dialog.show()
    }

    fun play(x: Int, y: Int) {
        release()
        mNotesOfChord = notesOfChord(x + mScale, y, mStatusBarFlags)
        val f = convertNotesToNotesInRange(mNotesOfChord, mSoundRange)
        mSound = Sound(f, this.context)
        mPlaying = 1
        mPlayingIndices = Pair(x, y)

        mSound!!.play()
        invalidate(RectFToRect(rectOfButton(x, y, mWidth, mHeight, mScroll)))
        invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
    }

    fun stop() {
        if (mSound != null) {
            mSound!!.stop()
        }
        mPlaying = 0
        mNotesOfChord = arrayOf<Int>()
        invalidate(RectFToRect(rectOfButtonArea(mWidth, mHeight)))
        invalidate(RectFToRect(rectOfStatusBar(mWidth, mHeight, 1.0f)))
    }

    fun release() {
        if (mSound != null) {
            mSound!!.release()
            mSound = null
        }
        mPlaying = 0
        mNotesOfChord = arrayOf<Int>()
        invalidate()
    }

    fun activityPaused(activity: MainActivity?) {
        mVibrator.cancel()
        release()
    }

    fun activityResumed(activity: MainActivity) {
        mVibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        preferenceValues
        invalidate()
    }

    fun heartbeat(interval: Int) {
        if (mStep > 0) {
            mStep--
            if (mSituation == SITUATION.NORMAL) {
                if (mDestination == SITUATION.TRANSPOSE) {
                    mScroll = (mOriginalScroll * mStep / mStepMax).toInt()
                    mBarsShowingRate = mStep.toFloat() / mStepMax
                }
            } else if (mSituation == SITUATION.TRANSPOSE) {
                if (mDestination == SITUATION.NORMAL) {
                    mBarsShowingRate = (mStepMax - mStep) / mStepMax
                }
            } else if (mSituation == SITUATION.TRANSPOSE_MOVING) {
                if (mDestination == SITUATION.TRANSPOSE) {
                    mScroll = ((mScale - mDestinationScale) * (mHeight / 5) * (mStepMax - mStep) / mStepMax).toInt()
                }
            }
            if (mStep == 0 && mSituation != mDestination) {
                if (mSituation == SITUATION.TRANSPOSE_MOVING) {
                    setScale(mDestinationScale)
                    mScroll = 0
                }
                mSituation = mDestination
            }
            mHandler.post(Repainter())
        }
        if (mPulling == 1) {
            val max = 100 / MainActivity.heartBeatInterval
            mScroll = (mDestinationScroll * (max - mStep) + mScroll * mStep) / max
            if (mScroll < -scrollMax(mWidth, mHeight)) mScroll = -scrollMax(mWidth, mHeight)
            if (mScroll > scrollMax(mWidth, mHeight)) mScroll = scrollMax(mWidth, mHeight)
            mHandler.post(Repainter())
        }
        if (mFlashEffectStep > 0) {
            mFlashEffectStep--
            var stepMod = 10 / MainActivity.heartBeatInterval
            if (stepMod == 0) stepMod = 1
            if (mFlashEffectStep % stepMod == 0 && (Math.random() * 10).toInt() == 0) {
                val shape = Shape(PointF((Math.random() * mWidth).toFloat(), (mHeight / 2).toFloat()))
                shape.mStyle = Shape.SHAPE_STYLE.LINE
                shape.mRadStart = 80
                shape.mRadEnd = 80
                mShapes.add(shape)
            }
        }
        if (mShapes.size > 0) {
            for (i in mShapes.indices.reversed()) {
                mShapes[i].mLifeTime--
                if (mShapes[i].mLifeTime <= 0) mShapes.removeAt(i)
            }
            mHandler.post(Repainter())
        }
    }

    private inner class Repainter : Runnable {
        override fun run() {
            invalidate()
        }
    }

    fun startAnimation(dest: SITUATION) {
        mDestination = dest
        mStep = mStepMax.toInt()
    }

    fun startTransposingAnimation(ds: Int) {
        mSituation = SITUATION.TRANSPOSE_MOVING
        mStep = mStepMax.toInt()
        mDestinationScale = ds
    }

    fun startPullingAnimation() {
        mPulling = 1
        mStep = mStepMax.toInt()
    }

    fun setScale(s: Int) {
        mScale = s
        setPrefValue(this.context, PREF_SCALE, mScale)
    }

    fun setDarken(d: Boolean) {
        mDarken = d
        setPrefValue(this.context, PREF_DARKEN, if (mDarken) 1 else 0)
    }

    val preferenceValues: Unit
        get() {
            mScale = prefValue(this.context, PREF_SCALE, 0)
            mDarken = prefValue(this.context, PREF_DARKEN, 0) > 0
            mSoundRange = prefValue(this.context, PREF_SOUND_RANGE, 0)
            mVibration = prefValue(this.context, PREF_VIBRATION, 1) > 0
            mStepMax = 100.0f / MainActivity.heartBeatInterval
        }

    fun vibrate() {
        if (mVibration) {
            synchronized(vibrateProcess) { mVibrator.vibrate(VIBRATION_LENGTH.toLong()) }
        }
    }

    companion object {
        const val debugMode = false
        val vibrateProcess = Any()
    }

    init {
        mSituation = SITUATION.NORMAL
        mDestination = SITUATION.NORMAL
        mStep = 0
        mPlaying = 0
        mScroll = 0
        mUpper = 0
        mDarken = false
        mVibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}