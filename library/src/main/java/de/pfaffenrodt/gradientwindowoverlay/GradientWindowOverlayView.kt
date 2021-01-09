package de.pfaffenrodt.gradientwindowoverlay

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View

/**
 * Copyright 2016 Dimitri Pfaffenrodt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Use this view above your views.
 * To achieve an fade in/out effect with your WindowBackground.
 */
class GradientWindowOverlayView : View {
    private val viewLocation = IntArray(2)
    private val gradientClipBounds = Rect()
    private val paint = Paint()
    private var bitmap: Bitmap? = null
    private var gradientCanvas: Canvas? = null
    private var gravity = Gravity.TOP
    private var isDrawnOverlay = false

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        readAttributes(context, attrs)
        initPaint()
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.GradientWindowOverlayView,
                    0, 0)
            gravity = try {
                a.getInteger(R.styleable.GradientWindowOverlayView_android_gravity, Gravity.TOP)
            } finally {
                a.recycle()
            }
        }
    }

    private fun initPaint() {
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val windowBackground = windowBackground ?: return // nothing to draw
        /**
         * make sure view is clipped to bounds.
         */
        canvas.getClipBounds(gradientClipBounds)
        val gradientCanvas = gradientCanvas
        if (gradientCanvas != null && !isDrawnOverlay) {
            isDrawnOverlay = true //not draw again
            gradientCanvas.save()
            getLocationInWindow(viewLocation)
            /**
             * move canvas to window position.
             */
            gradientCanvas.translate((-viewLocation[0]).toFloat(), (-viewLocation[1]).toFloat())
            /**
             * draw window background in to bitmap
             */
            windowBackground.draw(gradientCanvas)
            gradientCanvas.restore()
            /**
             * clip gradient effect with Xfermode
             * @see .initPaint
             * @see .getGradientShader
             */
            gradientCanvas.drawRect(gradientClipBounds, paint)
        }
        if (bitmap != null) {
            /**
             * finally draw window background with gradient effect
             */
            canvas.drawBitmap(bitmap!!, gradientClipBounds, gradientClipBounds, null)
        }
    }

    private val windowBackground: Drawable?
        get() {
            if (context is Activity) {
                val window = (context as Activity).window
                val decorView = window.decorView
                return decorView.background
            }
            return null
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        var bitmap = bitmap
        if (w * h != oldw * h || bitmap == null) {
            releaseResources()
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            this.bitmap = bitmap
            gradientCanvas = Canvas(bitmap)
            val shader = getGradientShader(w, h)
            paint.shader = shader
            isDrawnOverlay = false
        }
    }

    private fun getGradientShader(w: Int, h: Int): LinearGradient {
        val x0 = 0f
        val y0 = 0f
        var x1 = 0f
        var y1 = h.toFloat()
        var color1 = Color.BLACK
        var color2 = Color.TRANSPARENT
        when (gravity) {
            Gravity.START, Gravity.LEFT -> {
                x1 = w.toFloat()
                y1 = 0f
                color1 = Color.BLACK
                color2 = Color.TRANSPARENT
            }
            Gravity.TOP -> {
                color1 = Color.BLACK
                color2 = Color.TRANSPARENT
            }
            Gravity.END, Gravity.RIGHT -> {
                x1 = w.toFloat()
                y1 = 0f
                color1 = Color.TRANSPARENT
                color2 = Color.BLACK
            }
            Gravity.BOTTOM -> {
                color1 = Color.TRANSPARENT
                color2 = Color.BLACK
            }
        }
        return LinearGradient(x0, y0, x1, y1, color1, color2, Shader.TileMode.CLAMP)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseResources()
    }

    private fun releaseResources() {
        gradientCanvas = null
        val bitmap = bitmap
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
            this.bitmap = null
        }
        isDrawnOverlay = false
    }
}