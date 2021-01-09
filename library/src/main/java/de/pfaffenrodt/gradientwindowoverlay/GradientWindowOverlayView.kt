package de.pfaffenrodt.gradientwindowoverlay;

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

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

/**
 * Use this view above your views.
 * To achieve an fade in/out effect with your WindowBackground.
 */
public class GradientWindowOverlayView extends View {

    private int[] viewLocation = new int[2];
    private final Rect clipBounds =new Rect();

    private final Paint paint=new Paint();
    private Bitmap bitmap;
    private Canvas gradientCanvas;

    private int gravity = Gravity.TOP;

    private boolean isDrawnOverlay;

    public GradientWindowOverlayView(Context context) {
        super(context);
        init(context,null);
    }

    public GradientWindowOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GradientWindowOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        readAttributes(context, attrs);
        initPaint();
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        if(attrs!=null){
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.GradientWindowOverlayView,
                    0, 0);

            try {
                gravity = a.getInteger(R.styleable.GradientWindowOverlayView_android_gravity, Gravity.TOP);
            } finally {
                a.recycle();
            }
        }
    }

    private void initPaint() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable windowBackground=getWindowBackground();
        if(windowBackground==null){
            /**
             * nothing to draw
             */
            return;
        }

        /**
         * make sure view is clipped to bounds.
         */
        canvas.getClipBounds(clipBounds);

        if(gradientCanvas !=null && !isDrawnOverlay) {
            isDrawnOverlay =true;//not draw again
            gradientCanvas.save();
            getLocationInWindow(viewLocation);
            /**
             * move canvas to window position.
             */
            gradientCanvas.translate(-viewLocation[0], -viewLocation[1]);
            /**
             * draw window background in to bitmap
             */
            windowBackground.draw(gradientCanvas);
            gradientCanvas.restore();
            /**
             * clip gradient effect with Xfermode
             * @see #initPaint()
             * @see #getGradientShader(int, int)
             */
            gradientCanvas.drawRect(clipBounds, paint);
        }
        if(bitmap!=null) {
            /**
             * finally draw window background with gradient effect
             */
            canvas.drawBitmap(bitmap, clipBounds, clipBounds, null);
        }

    }

    private Drawable getWindowBackground(){
        if(getContext() instanceof Activity){
            Window window = ((Activity) getContext()).getWindow();
            View decorView = window.getDecorView();
            return decorView.getBackground();
        }
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w*h!=oldw*h||bitmap==null){
            releaseResources();
            bitmap=Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            gradientCanvas = new Canvas(bitmap);
            LinearGradient shader = getGradientShader(w, h);
            paint.setShader(shader);
            isDrawnOverlay =false;
        }
    }

    private LinearGradient getGradientShader(int w, int h) {
        float x0=0,y0=0,x1=0,y1=h;
        int color1 = Color.BLACK;
        int color2 = Color.TRANSPARENT;
        switch (gravity){
            case Gravity.START:
            case Gravity.LEFT:
                x1=w;
                y1=0;
            case Gravity.TOP:
                color1 = Color.BLACK;
                color2 = Color.TRANSPARENT;
                break;
            case Gravity.END:
            case Gravity.RIGHT:
                x1=w;
                y1=0;
            case Gravity.BOTTOM:
                color1 = Color.TRANSPARENT;
                color2 = Color.BLACK;
                break;
        }
        return new LinearGradient(x0,y0,x1,y1,color1,color2, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseResources();
    }

    private void releaseResources() {
        gradientCanvas=null;
        if(bitmap!=null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap=null;
        }
        isDrawnOverlay =false;
    }
}
