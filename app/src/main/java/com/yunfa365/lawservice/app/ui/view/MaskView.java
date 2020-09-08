package com.yunfa365.lawservice.app.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MaskView extends View {
    /**
     * 遮罩颜色
     */
    private int maskColor = Color.argb(100, 0, 0, 0);

    /**
     * 镂空矩形
     */
    private Rect frame = new Rect();

    /**
     * 镂空边框
     */
    private Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 镂空区域
     */
    private Paint eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();

    public MaskView(Context context) {
        super(context);
        init();
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 硬件加速不支持，图层混合。
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // 取景框颜色、线宽
        border.setColor(Color.WHITE);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(5);

        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        /*int width = (int) (w * 0.92f);
        int height = (int) (h * 0.9f);

        int left = (w - width) / 2;
        int top = (h - height) / 2;
        int right = width + left;
        int bottom = height;

        frame.left = left;
        frame.top = top;
        frame.right = right;
        frame.bottom = bottom;*/

        int width = w - getPaddingLeft() - getPaddingRight();
        int height = h - getPaddingTop() - getPaddingBottom();

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = width + left;
        int bottom = height + top;

        frame.left = left;
        frame.top = top;
        frame.right = right;
        frame.bottom = bottom;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = frame.left;
        int top = frame.top;
        int right = frame.right;
        int bottom = frame.bottom;

        fillRectRound(left, top, right, bottom, 30, 30);


        canvas.drawColor(maskColor);
        canvas.drawPath(path, border);
        canvas.drawPath(path, eraser);

    }

    private void fillRectRound(float left, float top, float right, float bottom, float rx, float ry) {
        path.reset();

        float width = right - left;
        float height = bottom - top;

        float lineWidth = (width - (2 * rx));
        float lineHeight = (height - (2 * ry));

        path.moveTo(left, top + ry);
        path.rQuadTo(0, -ry, rx, -ry);
        path.rLineTo(lineWidth, 0);
        path.rQuadTo(rx, 0, rx, ry);
        path.rLineTo(0, lineHeight);
        path.rQuadTo(0, ry, -rx, ry);
        path.rLineTo(-lineWidth, 0);
        path.rQuadTo(-rx, 0, -rx, -ry);
        path.rLineTo(0, -lineHeight);

        path.close();

//        RectF roundRect = new RectF(left, top, right, bottom);
//        path.addRoundRect(roundRect, rx, ry, Path.Direction.CW);

    }

    public Rect getFrameRect() {
        return new Rect(frame);
    }
}
