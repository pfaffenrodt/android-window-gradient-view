package de.pfaffenrodt.gradientwindowoverlay

import android.graphics.*
import android.view.Gravity

fun gradientShader(
        width: Int,
        height: Int,
        gravity: Int,
        from: Int = Color.BLACK,
        to: Int = Color.TRANSPARENT,
): LinearGradient {
    return gradientShader(RectF(0f,0f, width.toFloat(), height.toFloat()), gravity, from, to)
}

fun gradientShader(
        rect: Rect,
        gravity: Int,
        from: Int = Color.BLACK,
        to: Int = Color.TRANSPARENT,
): LinearGradient {
    return gradientShader(rect.toRectF(), gravity, from, to)
}

private fun Rect.toRectF(): RectF {
    return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun gradientShader(
        rect: RectF,
        gravity: Int,
        from: Int = Color.BLACK,
        to: Int = Color.TRANSPARENT,
): LinearGradient {
    var x0 = 0f
    var y0 = 0f
    var x1 = 0f
    var y1 = 0f
    var color1 = from
    var color2 = to
    when (gravity) {
        Gravity.TOP -> {
            color1 = from
            color2 = to
            y0 = rect.top
            y1 = rect.bottom
        }
        Gravity.BOTTOM -> {
            color1 = to
            color2 = from
            y0 = rect.top
            y1 = rect.bottom
        }
        Gravity.START, Gravity.LEFT -> {
            x0 = rect.left
            x1 = rect.right
            color1 = from
            color2 = to
        }
        Gravity.END, Gravity.RIGHT -> {
            x0 = rect.left
            x1 = rect.right
            color1 = to
            color2 = from
        }
    }
    return LinearGradient(x0, y0, x1, y1, color1, color2, Shader.TileMode.CLAMP)
}