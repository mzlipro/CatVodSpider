package com.github.catvod.spider;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 真液态玻璃效果容器
 * 实现高斯模糊 + 渐变折射 + 边缘高光 + 内阴影
 */
public class LiquidGlassView extends FrameLayout {

    // 效果参数
    private float mBlurRadius = 18f;
    private float mGlassAlpha = 0.55f;
    private int mTintColor = 0xFFFFFFFF;
    private float mCornerRadius = 24f;

    // 画笔
    private Paint mTintPaint;
    private Paint mGradientPaint;
    private Paint mEdgePaint;
    private Paint mInnerShadowPaint;
    private Paint mShimmerPaint;

    // 模糊缓存
    private Bitmap mBlurCache;
    private boolean mNeedsBlur = true;

    public LiquidGlassView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setClipChildren(false);

        mTintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTintPaint.setColor(mTintColor);
        mTintPaint.setAlpha((int) (mGlassAlpha * 255));

        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setStrokeWidth(dp(2));

        mInnerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerShadowPaint.setStyle(Paint.Style.STROKE);
        mInnerShadowPaint.setStrokeWidth(dp(16));

        mShimmerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        RectF rect = new RectF(0, 0, w, h);

        // ====== 1. 高斯模糊背景 ======
        drawBlurredBackground(canvas, w, h);

        // ====== 2. 玻璃底色 ======
        mTintPaint.setAlpha((int) (mGlassAlpha * 255));
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mTintPaint);

        // ====== 3. 光线折射渐变（顶部亮→底部暗）======
        drawRefractionGradient(canvas, rect);

        // ====== 4. 顶部高光线 ======
        drawTopHighlight(canvas, rect);

        // ====== 5. 内阴影（深度感）======
        drawInnerShadow(canvas, rect);

        // ====== 6. 边缘高光 ======
        drawEdgeGlow(canvas, rect);

        // ====== 7. 玻璃光泽斜线 ======
        drawShimmer(canvas, rect);

        // 绘制子视图
        super.onDraw(canvas);
    }

    /**
     * 高斯模糊背景：多级缩放实现真实模糊感
     */
    private void drawBlurredBackground(Canvas canvas, int w, int h) {
        try {
            if (getParent() instanceof View) {
                View parent = (View) getParent();
                if (mBlurCache == null || mNeedsBlur) {
                    // 截取父视图区域进行缩放模糊
                    Bitmap src = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas srcCanvas = new Canvas(src);
                    parent.draw(srcCanvas);

                    // 多级降采样模糊（scale越大越模糊）
                    int scale = Math.max(2, (int) (mBlurRadius / 3));
                    int sw = Math.max(1, w / scale);
                    int sh = Math.max(1, h / scale);

                    Bitmap small = Bitmap.createScaledBitmap(src, sw, sh, true);
                    Bitmap blurred = Bitmap.createScaledBitmap(small, w, h, true);

                    if (mBlurCache != null && !mBlurCache.isRecycled()) {
                        mBlurCache.recycle();
                    }
                    mBlurCache = blurred;
                    mNeedsBlur = false;

                    src.recycle();
                    small.recycle();
                }

                if (mBlurCache != null && !mBlurCache.isRecycled()) {
                    Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
                    p.setAlpha(200);
                    canvas.drawBitmap(mBlurCache, 0, 0, p);
                }
            }
        } catch (Exception e) {
            // 模糊失败时用纯色兜底
            canvas.drawColor(0xCCEEF2F7);
        }
    }

    /**
     * 光线折射渐变：模拟光线穿过玻璃的明暗变化
     */
    private void drawRefractionGradient(Canvas canvas, RectF rect) {
        LinearGradient gradient = new LinearGradient(
                rect.left, rect.top, rect.right * 0.3f, rect.bottom,
                new int[]{
                        0x44FFFFFF,   // 左上角高光
                        0x22FFFFFF,   // 上部
                        0x00000000,   // 中部透明
                        0x11000000,   // 下部微暗
                        0x33000000    // 右下阴影
                },
                new float[]{0f, 0.15f, 0.45f, 0.75f, 1f},
                Shader.TileMode.CLAMP
        );
        mGradientPaint.setShader(gradient);
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mGradientPaint);
    }

    /**
     * 顶部高光线：模拟玻璃顶部反光
     */
    private void drawTopHighlight(Canvas canvas, RectF rect) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(0xAAFFFFFF);

        RectF topArc = new RectF(rect.left + dp(4), rect.top + dp(1),
                rect.right - dp(4), rect.top + dp(8));
        canvas.drawRoundRect(topArc, dp(6), dp(6), p);
    }

    /**
     * 内阴影：玻璃厚度感
     */
    private void drawInnerShadow(Canvas canvas, RectF rect) {
        LinearGradient shadow = new LinearGradient(
                rect.left, rect.top, rect.left, rect.top + dp(20),
                0x22000000,
                0x00000000,
                Shader.TileMode.CLAMP
        );
        mInnerShadowPaint.setShader(shadow);
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mInnerShadowPaint);
    }

    /**
     * 边缘高光：玻璃边缘折射光
     */
    private void drawEdgeGlow(Canvas canvas, RectF rect) {
        // 外边缘
        mEdgePaint.setColor(0x55FFFFFF);
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mEdgePaint);

        // 内边缘（更亮，模拟折射）
        Paint innerEdge = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerEdge.setStyle(Paint.Style.STROKE);
        innerEdge.setStrokeWidth(dp(1));
        innerEdge.setColor(0x88FFFFFF);
        RectF innerRect = new RectF(rect.left + dp(2), rect.top + dp(2),
                rect.right - dp(2), rect.bottom - dp(2));
        canvas.drawRoundRect(innerRect, dp((int) mCornerRadius - 2), dp((int) mCornerRadius - 2), innerEdge);
    }

    /**
     * 玻璃光泽斜线：模拟光线在玻璃表面的反射
     */
    private void drawShimmer(Canvas canvas, RectF rect) {
        LinearGradient shimmer = new LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom * 0.6f,
                new int[]{0x00000000, 0x18FFFFFF, 0x00000000},
                new float[]{0.2f, 0.4f, 0.6f},
                Shader.TileMode.CLAMP
        );
        mShimmerPaint.setShader(shimmer);
        canvas.drawRoundRect(rect, dp((int) mCornerRadius), dp((int) mCornerRadius), mShimmerPaint);
    }

    // ========== 参数设置 ==========

    public void setBlurRadius(float radius) {
        mBlurRadius = Math.max(4, Math.min(30, radius));
        mNeedsBlur = true;
        invalidate();
    }

    public void setGlassAlpha(float alpha) {
        mGlassAlpha = Math.max(0.1f, Math.min(0.95f, alpha));
        invalidate();
    }

    public void setTintColor(int color) {
        mTintColor = color;
        mTintPaint.setColor(color);
        invalidate();
    }

    public void setCornerRadius(float radius) {
        mCornerRadius = radius;
        invalidate();
    }

    public void updateBlur() {
        mNeedsBlur = true;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mNeedsBlur = true;
    }

    private int dp(int d) {
        return (int) (d * getContext().getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBlurCache != null && !mBlurCache.isRecycled()) {
            mBlurCache.recycle();
            mBlurCache = null;
        }
    }
}