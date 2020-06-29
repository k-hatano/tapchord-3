package jp.nita.tapchord.Util

import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF

object Dimensions {
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
        return RectF(vert * 2, vert * 30.5f + hidingDelta,
                vert * 2 + max / 5, vert * 32.5f + hidingDelta)
    }

    @JvmStatic
    fun rectOfScrollNob(pos: Int, upper: Int, width: Int, height: Int, showingRate: Float): RectF {
        val vert = height / 35f
        val max = vert * 7 * 15
        val nob = width / 5.toFloat()
        val x = vert * 2 + max / 10 - pos / 5
        val hidingDelta = vert * (1.0f - showingRate) * 7
        return RectF(x - nob / 2, vert * 30f - upper + hidingDelta,
                x + nob / 2, vert * 33f - upper + hidingDelta)
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
        return RectF(pX - vert / 2 + vert / 14, 0 + vert / 14 - hidingDelta,
                pX + vert / 2 - vert / 14, vert - vert / 14 - hidingDelta)
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
        return RectF(pX - vert / 2 + vert / 14, height - vert + vert / 14 + hidingDelta,
                pX + vert / 2 - vert / 14, height - vert / 14 + hidingDelta)
    }

    @JvmStatic
    fun rectOfKeyboardIndicator(i: Int, shrink: Int, width: Int, height: Int, showingRate: Float): RectF? {
        val vert = height / 35f
        val hidingDelta = vert * 7 * (1.0f - showingRate)
        var r: RectF? = null
        when (i % 12) {
            0 -> r = RectF(width - vert * 21 + shrink, vert * 4 + shrink - hidingDelta,
                    width - vert * 19 - shrink, vert * 6 - shrink - hidingDelta)
            2 -> r = RectF(width - vert * 18 + shrink, vert * 4 + shrink - hidingDelta,
                    width - vert * 16 - shrink, vert * 6 - shrink - hidingDelta)
            4 -> r = RectF(width - vert * 15 + shrink, vert * 4 + shrink - hidingDelta,
                    width - vert * 13 - shrink, vert * 6 - shrink - hidingDelta)
            5 -> r = RectF(width - vert * 12 + shrink, vert * 4 + shrink - hidingDelta,
                    width - vert * 10 - shrink, vert * 6 - shrink - hidingDelta)
            7 -> r = RectF(width - vert * 9 + shrink, vert * 4 + shrink - hidingDelta,
                    width - vert * 7 - shrink, vert * 6 - shrink - hidingDelta)
            9 -> r = RectF(width - vert * 6 + shrink, vert * 4 + shrink - hidingDelta,
                    width - vert * 4 - shrink, vert * 6 - shrink - hidingDelta)
            11 -> r = RectF(width - vert * 3 + shrink, vert * 4 + shrink - hidingDelta,
                    width - vert * 1 - shrink, vert * 6 - shrink - hidingDelta)
            1 -> r = RectF(width - vert * 19.5f + shrink, vert * 1 + shrink - hidingDelta,
                    width - vert * 17.5f - shrink, vert * 3 - shrink - hidingDelta)
            3 -> r = RectF(width - vert * 16.5f + shrink, vert * 1 + shrink - hidingDelta,
                    width - vert * 14.5f - shrink, vert * 3 - shrink - hidingDelta)
            6 -> r = RectF(width - vert * 10.5f + shrink, vert * 1 + shrink - hidingDelta,
                    width - vert * 8.5f - shrink, vert * 3 - shrink - hidingDelta)
            8 -> r = RectF(width - vert * 7.5f + shrink, vert * 1 + shrink - hidingDelta,
                    width - vert * 5.5f - shrink, vert * 3 - shrink - hidingDelta)
            10 -> r = RectF(width - vert * 4.5f + shrink, vert * 1 + shrink - hidingDelta,
                    width - vert * 2.5f - shrink, vert * 3 - shrink - hidingDelta)
        }
        return r
    }

    @JvmStatic
    fun rectOfKeyboardIndicators(shrink: Int, width: Int, height: Int, showingRate: Float): RectF {
        val vert = height / 35f
        val hidingDelta = vert * 7 * (1.0f - showingRate)
        return RectF(width - vert * 23 + shrink, 0F, width.toFloat(),
                vert * 7 - shrink - hidingDelta)
    }

    @JvmStatic
    fun RectFToRect(rectf: RectF): Rect {
        return Rect(rectf.left.toInt(), rectf.top.toInt(),
                rectf.right.toInt(), rectf.bottom.toInt())
    }
}