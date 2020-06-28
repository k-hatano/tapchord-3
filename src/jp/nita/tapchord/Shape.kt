package jp.nita.tapchord

import android.graphics.PointF

class Shape internal constructor(pf: PointF) {
    @JvmField
    var mStyle: Int
    @JvmField
    var mRadStart: Int
    @JvmField
    var mRadEnd: Int
    @JvmField
    var mLifeTime: Int
    @JvmField
    val mCenter: PointF

    companion object {
        const val STYLE_LINE = 0
        const val STYLE_CIRCLE = 1
        const val STYLE_TRIANGLE = 2
        const val STYLE_SQUARE = 3
        const val MAX_LIFETIME = 360
        @JvmStatic
        val maxLifetime: Int
            get() = MAX_LIFETIME / MainActivity.heartBeatInterval
    }

    init {
        mStyle = (Math.random() * 4).toInt()
        mRadStart = (Math.random() * 360).toInt() - 180
        mRadEnd = mRadStart + (Math.random() * 180).toInt() - 90
        mLifeTime = maxLifetime
        mCenter = pf
    }
}