package de.pfaffenrodt.gradientwindowoverlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout

class FadeoutLayout
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
):  FrameLayout(context, attrs, defStyleAttr) {

    /**
     * user define fade bounds
     */
    var fadeLeft = 0
        set(value) {
            field = value
            releaseResources()
            invalidate()
        }
    var fadeTop = 0
        set(value) {
            field = value
            releaseResources()
            invalidate()
        }
    var fadeRight = 0
        set(value) {
            field = value
            releaseResources()
            invalidate()
        }
    var fadeBottom = 0
        set(value) {
            field = value
            releaseResources()
            invalidate()
        }

    private val maskClipBounds = Rect()
    private val topRect = Rect()
    private val leftRect = Rect()
    private val rightRect = Rect()
    private val bottomRect = Rect()
    private var topShader = gradientShader(0, 0, Gravity.TOP)
    private var bottomShader = gradientShader(0, 0, Gravity.BOTTOM)
    private var leftShader = gradientShader(0, 0, Gravity.START)
    private var rightShader = gradientShader(0, 0, Gravity.END)
    private var maskBitmap: Bitmap? = null
    private val paint = Paint()

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.FadeoutLayout,
                    0, 0)
            try {
                fadeTop = a.getDimensionPixelSize(R.styleable.FadeoutLayout_fadeTop, 0)
                fadeBottom = a.getDimensionPixelSize(R.styleable.FadeoutLayout_fadeBottom, 0)
                fadeLeft = a.getDimensionPixelSize(R.styleable.FadeoutLayout_fadeLeft, 0)
                fadeRight = a.getDimensionPixelSize(R.styleable.FadeoutLayout_fadeRight, 0)
            } finally {
                a.recycle()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?: return
        super.dispatchDraw(canvas)
        drawMask()
        maskBitmap?.let {
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            canvas.getClipBounds(maskClipBounds)
            canvas.drawBitmap(it, maskClipBounds, maskClipBounds, paint)
        }
    }

    fun drawMask() {
        if (maskBitmap != null) {
            return
        }
        val from = Color.BLACK
        val to = Color.TRANSPARENT
        topRect.set(0, 0, width, fadeTop)
        topShader = gradientShader(topRect, Gravity.TOP, from, to)
        bottomRect.set(0, height-fadeBottom, width, height)
        bottomShader = gradientShader(bottomRect, Gravity.BOTTOM, from, to)
        leftRect.set(0, 0, fadeLeft, height)
        leftShader = gradientShader(leftRect, Gravity.START, from, to)
        rightRect.set(width - fadeRight, 0, width, height)
        rightShader = gradientShader(rightRect, Gravity.END, from, to)
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        this.maskBitmap = maskBitmap
        val maskCanvas = Canvas(maskBitmap)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)
        maskCanvas.getClipBounds(maskClipBounds)
        if (fadeTop > 0) {
            drawMaskPart(maskCanvas, topRect, topShader)
        }
        if (fadeBottom > 0) {
            drawMaskPart(maskCanvas, bottomRect, bottomShader)
        }
        if (fadeLeft > 0) {
            drawMaskPart(maskCanvas, leftRect, leftShader)
        }
        if (fadeRight > 0) {
            drawMaskPart(maskCanvas, rightRect, rightShader)
        }
    }

    private fun drawMaskPart(canvas: Canvas, rect: Rect, shader: Shader) {
        paint.shader = shader
        canvas.drawRect(rect, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w * h != oldw * h) {
            releaseResources()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseResources()
    }

    private fun releaseResources() {
        val bitmap = maskBitmap
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
            this.maskBitmap = null
        }
    }
}