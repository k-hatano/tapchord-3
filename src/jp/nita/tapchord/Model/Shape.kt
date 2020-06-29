package jp.nita.tapchord.Model

import android.graphics.PointF
import jp.nita.tapchord.Activity.MainActivity

class Shape internal constructor(pf: PointF) {
    @JvmField
    var mStyle: SHAPE_STYLE = SHAPE_STYLE.LINE

    @JvmField
    var mRadStart: Int

    @JvmField
    var mRadEnd: Int

    @JvmField
    var mLifeTime: Int

    @JvmField
    val mCenter: PointF

    enum class SHAPE_STYLE(id: Int) {
        LINE(0),
        CIRCLE(1),
        TRIANGLE(2),
        SQUARE(3)
    }

    companion object {
        const val MAX_LIFETIME = 360

        @JvmStatic
        val maxLifetime: Int
            get() = MAX_LIFETIME / MainActivity.heartBeatInterval
    }

    init {
        var style = (Math.random() * 4).toInt()
        when (style) {
            0 -> mStyle = SHAPE_STYLE.LINE
            1 -> mStyle = SHAPE_STYLE.CIRCLE
            2 -> mStyle = SHAPE_STYLE.TRIANGLE
            3 -> mStyle = SHAPE_STYLE.SQUARE
        }

        mRadStart = (Math.random() * 360).toInt() - 180
        mRadEnd = mRadStart + (Math.random() * 180).toInt() - 90
        mLifeTime = maxLifetime
        mCenter = pf
    }
}