package jp.nita.tapchord.Util

import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF

object Dimensions {
    @JvmStatic
    fun rectOfButtonArea(width: Int, height: Int): RectF {
        val unit = height * 7 / 35f
        return RectF(0F, unit, width.toFloat(), height - unit)
    }

    @JvmStatic
    fun rectOfButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): RectF {
        val unit = height * 7 / 35f
        val pX = width / 2 + x * unit
        val pY = height / 2 + y * unit
        return RectF(pX - unit / 2 + unit / 14 + scroll, pY - unit / 2 + unit / 14,
                pX + unit / 2 - unit / 14 + scroll, pY + unit / 2 - unit / 14)
    }

    @JvmStatic
    fun pointOfButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): Point {
        val unit = (height * 7 / 35f).toInt()
        val resX = Math.floor((x - width / 2 - scroll).toFloat() / unit + 0.5).toInt()
        val resY = Math.floor((y - height / 2).toFloat() / unit + 0.5).toInt()
        return Point(resX, resY)
    }

    fun topLeftOfButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): Point {
        val unit = (height * 7 / 35f).toInt()
        val resL = x / unit
        val resT = y / unit
        return Point(resL, resT)
    }

    fun bottomRightButton(x: Int, y: Int, width: Int, height: Int, scroll: Int): Point {
        val unit = (height * 7 / 35f).toInt()
        val resB = (width - x) / unit
        val resR = (height - y) / unit
        return Point(resB, resR)
    }

    @JvmStatic
    fun scrollMax(width: Int, height: Int): Int {
        val unit = height / 35f
        val max = unit * 7 * 15
        return (max - width.toFloat()).toInt() / 2
    }

    @JvmStatic
    fun rectOfScrollBar(width: Int, height: Int, showingRate: Float): RectF {
        val unit = height / 35f
        val max = unit * 7 * 15
        val hidingDelta = unit * (1.0f - showingRate) * 7
        return RectF(unit * 2, unit * 30.5f + hidingDelta,
                unit * 2 + max / 5, unit * 32.5f + hidingDelta)
    }

    @JvmStatic
    fun rectOfScrollNob(pos: Int, upper: Int, width: Int, height: Int, showingRate: Float): RectF {
        val unit = height / 35f
        val max = unit * 7 * 15
        val nob = width / 5.toFloat()
        val x = unit * 2 + max / 10 - pos / 5
        val hidingDelta = unit * (1.0f - showingRate) * 7
        return RectF(x - nob / 2, unit * 30f - upper + hidingDelta,
                x + nob / 2, unit * 33f - upper + hidingDelta)
    }

    @JvmStatic
    fun radiusOfButton(height: Int): Int {
        return height * 7 / 70 - 8
    }

    @JvmStatic
    fun rectOfStatusBar(width: Int, height: Int, showingRate: Float): RectF {
        val unit = height * 7 / 35f
        val hidingDelta = unit * (1.0f - showingRate)
        return RectF(0F, 0 - hidingDelta, width.toFloat(), height * 7 / 35 - hidingDelta)
    }

    @JvmStatic
    fun rectOfStatusBarButton(x: Int, y: Int, width: Int, height: Int, showingRate: Float): RectF {
        val unit = height * 7 / 35f
        val pX = x * unit + unit / 2
        val hidingDelta = unit * (1.0f - showingRate)
        return RectF(pX - unit / 2 + unit / 14, 0 + unit / 14 - hidingDelta,
                pX + unit / 2 - unit / 14, unit - unit / 14 - hidingDelta)
    }

    @JvmStatic
    fun rectOfToolbar(width: Int, height: Int, showingRate: Float): RectF {
        val unit = height * 7 / 35f
        val hidingDelta = unit * (1.0f - showingRate)
        return RectF(0F, height * 28 / 35 + hidingDelta, width.toFloat(), height + hidingDelta)
    }

    @JvmStatic
    fun rectOfToolbarButton(x: Int, y: Int, width: Int, height: Int, showingRate: Float): RectF {
        val unit = height * 7 / 35f
        val pX = x * unit + unit / 2
        val hidingDelta = unit * (1.0f - showingRate)
        return RectF(width - (pX + unit / 2) + unit / 14, height - unit + unit / 14 + hidingDelta,
                width - (pX - unit / 2) - unit / 14, height - unit / 14 + hidingDelta)
    }

    @JvmStatic
    fun rectOfToolbarTransposingButton(x: Int, y: Int, width: Int, height: Int, showingRate: Float): RectF {
        val unit = height * 7 / 35f
        val pX = x * unit + unit / 2
        val hidingDelta = unit * (1.0f - showingRate)
        return RectF(pX - unit / 2 + unit / 14, height - unit + unit / 14 + hidingDelta,
                pX + unit / 2 - unit / 14, height - unit / 14 + hidingDelta)
    }

    @JvmStatic
    fun rectOfKeyboardIndicator(i: Int, shrink: Int, width: Int, height: Int, showingRate: Float): RectF? {
        val unit = height / 35f
        val hidingDelta = unit * 7 * (1.0f - showingRate)
        var r: RectF? = null
        when (i % 12) {
            0 -> r = RectF(width - unit * 21 + shrink, unit * 4 + shrink - hidingDelta,
                    width - unit * 19 - shrink, unit * 6 - shrink - hidingDelta)
            2 -> r = RectF(width - unit * 18 + shrink, unit * 4 + shrink - hidingDelta,
                    width - unit * 16 - shrink, unit * 6 - shrink - hidingDelta)
            4 -> r = RectF(width - unit * 15 + shrink, unit * 4 + shrink - hidingDelta,
                    width - unit * 13 - shrink, unit * 6 - shrink - hidingDelta)
            5 -> r = RectF(width - unit * 12 + shrink, unit * 4 + shrink - hidingDelta,
                    width - unit * 10 - shrink, unit * 6 - shrink - hidingDelta)
            7 -> r = RectF(width - unit * 9 + shrink, unit * 4 + shrink - hidingDelta,
                    width - unit * 7 - shrink, unit * 6 - shrink - hidingDelta)
            9 -> r = RectF(width - unit * 6 + shrink, unit * 4 + shrink - hidingDelta,
                    width - unit * 4 - shrink, unit * 6 - shrink - hidingDelta)
            11 -> r = RectF(width - unit * 3 + shrink, unit * 4 + shrink - hidingDelta,
                    width - unit * 1 - shrink, unit * 6 - shrink - hidingDelta)
            1 -> r = RectF(width - unit * 19.5f + shrink, unit * 1 + shrink - hidingDelta,
                    width - unit * 17.5f - shrink, unit * 3 - shrink - hidingDelta)
            3 -> r = RectF(width - unit * 16.5f + shrink, unit * 1 + shrink - hidingDelta,
                    width - unit * 14.5f - shrink, unit * 3 - shrink - hidingDelta)
            6 -> r = RectF(width - unit * 10.5f + shrink, unit * 1 + shrink - hidingDelta,
                    width - unit * 8.5f - shrink, unit * 3 - shrink - hidingDelta)
            8 -> r = RectF(width - unit * 7.5f + shrink, unit * 1 + shrink - hidingDelta,
                    width - unit * 5.5f - shrink, unit * 3 - shrink - hidingDelta)
            10 -> r = RectF(width - unit * 4.5f + shrink, unit * 1 + shrink - hidingDelta,
                    width - unit * 2.5f - shrink, unit * 3 - shrink - hidingDelta)
        }
        return r
    }

    @JvmStatic
    fun rectOfKeyboardIndicators(shrink: Int, width: Int, height: Int, showingRate: Float): RectF {
        val unit = height / 35f
        val hidingDelta = unit * 7 * (1.0f - showingRate)
        return RectF(width - unit * 23 + shrink, 0F, width.toFloat(),
                unit * 7 - shrink - hidingDelta)
    }

    @JvmStatic
    fun RectFToRect(rectf: RectF): Rect {
        return Rect(rectf.left.toInt(), rectf.top.toInt(),
                rectf.right.toInt(), rectf.bottom.toInt())
    }
}