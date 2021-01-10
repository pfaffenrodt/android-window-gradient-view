package de.pfaffenrodt.gradientview

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import de.pfaffenrodt.gradientwindowoverlay.FadeoutLayout

class MainActivity : AppCompatActivity() {
    val density: Float by lazy {
        resources.displayMetrics.density
    }
    val pixelSize: Int by lazy {
        (2 * density).toInt()
    }
    var playing = false
    var animateIncrease = true
    var handler: Handler? = null
    var fadeoutLayout: FadeoutLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fadeoutLayout = findViewById(R.id.fade_layout)
        handler = HandlerCompat.createAsync(Looper.getMainLooper())
    }

    fun decrease(view: View? = null) {
        val fadeoutLayout = fadeoutLayout?:return
        fadeoutLayout.fadeTop -= pixelSize
        fadeoutLayout.fadeBottom -= pixelSize
        fadeoutLayout.fadeLeft -= pixelSize
        fadeoutLayout.fadeRight -= pixelSize
    }

    fun increase(view: View? = null) {
        val fadeoutLayout = fadeoutLayout?:return
        fadeoutLayout.fadeTop += pixelSize
        fadeoutLayout.fadeBottom += pixelSize
        fadeoutLayout.fadeLeft += pixelSize
        fadeoutLayout.fadeRight += pixelSize
    }

    /**
     * this animation is just a test.
     * look into profiler. see what costs are to use this art of animation.
     * high cpu and memory costs.
     */
    fun play(view: View) {
        playing = !playing

        if (!playing) {
            return
        }
        loop()
    }

    fun loop() {
        handler?.postDelayed({
            if (!playing) {
                return@postDelayed
            }
            val fadeoutLayout = fadeoutLayout?:return@postDelayed
            if (fadeoutLayout.fadeTop > 800) {
                animateIncrease = false
            }
            if (fadeoutLayout.fadeTop < 10) {
                animateIncrease = true
            }
            if (animateIncrease) {
                increase()
            } else {
                decrease()
            }
            loop()
        }, 16)
    }
}