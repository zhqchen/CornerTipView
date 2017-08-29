package com.zhqchen.cornertipview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


/**
 * 斜梯形角标的View，业务上见 PinJianHospitalChooseActivity
 * Created by CHENZHIQIANG247 on 2017-04-13.
 */
public class CornerTipView extends View {

    public static final int TIP_POSITION_TOP_LEFT = 0;//左上角
    public static final int TIP_POSITION_TOP_RIGHT= 1;//右上角
    public static final int TIP_POSITION_BOTTOM_LEFT = 2;//左下角
    public static final int TIP_POSITION_BOTTOM_RIGHT = 3;//右下角

    private int tipCornerPosition;//显示位置

    private int tipTextSize;//内容字体大小, 单位px, 默认为11sp的大小
    private int tipTextColor;//内容字颜色，默认白色
    private int textVPadding;//字在梯形内的上下间距，默认3dp
    private int tipBackgroundColor;//背景色, 默认透明

    private float startDisH;//初始值
    private float realStartDisH;//x方向上起始点与所在角落点的距离，默认20dp，可变

    private float waistDis;//梯形的腰长，自适应
    private Paint backPaint;//背景的Paint
    private Paint textPaint;//字体Paint

    private Path path;

    private String tipContent;

    public CornerTipView(Context context) {
        this(context, null);
    }

    public CornerTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        tipCornerPosition = TIP_POSITION_TOP_LEFT;//默认左上角
        startDisH = dp2px(context, 20);//20dp
        textVPadding = dp2px(context, 2);//3dp
        tipTextSize = sp2px(context, 11);//11sp
        tipTextColor = Color.WHITE;//默认字体白色
        tipBackgroundColor = Color.TRANSPARENT;//默认透明背景

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerTipView);
        tipCornerPosition = typedArray.getInt(R.styleable.CornerTipView_tip_corner_position, tipCornerPosition);
        realStartDisH = typedArray.getDimensionPixelSize(R.styleable.CornerTipView_tip_start_dis_x, (int) startDisH);
        textVPadding = typedArray.getDimensionPixelSize(R.styleable.CornerTipView_tip_text_v_padding, textVPadding);
        tipTextSize = typedArray.getDimensionPixelSize(R.styleable.CornerTipView_tip_text_size, tipTextSize);//默认12sp
        tipTextColor = typedArray.getColor(R.styleable.CornerTipView_tip_text_color, tipTextColor);
        tipBackgroundColor = typedArray.getColor(R.styleable.CornerTipView_tip_background, tipBackgroundColor);
        tipContent = typedArray.getString(R.styleable.CornerTipView_tip_text);
        typedArray.recycle();

        path = new Path();
        backPaint = new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setColor(tipBackgroundColor);

        textPaint = new TextPaint();
        textPaint.setColor(tipTextColor);
        textPaint.setTextSize(tipTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(TextUtils.isEmpty(tipContent)) {
            return;
        }
        if(waistDis == 0) {
            Rect rect = new Rect();
            textPaint.getTextBounds(tipContent, 0, tipContent.length(), rect);
            waistDis = (float) ((rect.height() + textVPadding * 2) * Math.sqrt(2f));//根据字体的高度来自适应梯形的高
        }
        float realWaist = waistDis;
        if(realStartDisH == 0) {
            realWaist = realWaist + startDisH;//若设置了起始位置为0，说明直接从角落点开始，梯形变为三角形，腰长变大
        }
        switch (tipCornerPosition) {
            case TIP_POSITION_TOP_LEFT:
                drawTopLeftTip(canvas, realWaist);
                break;
            case TIP_POSITION_TOP_RIGHT:
                drawTopRight(canvas, realWaist);
                break;
            case TIP_POSITION_BOTTOM_LEFT:
                drawBottomLeft(canvas, realWaist);
                break;
            case TIP_POSITION_BOTTOM_RIGHT:
                drawBottomRight(canvas, realWaist);
                break;
            default:
                drawTopLeftTip(canvas, realWaist);//默认左上角
                break;
        }
    }

    /**
     * 左上角画梯形
     * @param canvas
     * @param realWaist 腰长
     */
    private void drawTopLeftTip(Canvas canvas, float realWaist) {
        path.moveTo(this.realStartDisH, 0);
        path.lineTo(this.realStartDisH + realWaist, 0);
        path.lineTo(0, this.realStartDisH + realWaist);
        path.lineTo(0, this.realStartDisH);
        path.close();
        canvas.drawPath(path, backPaint);
        canvas.save();

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        float dis = (waistDis /2  + this.startDisH) /2;
        float disY = dis - (fontMetrics.bottom + fontMetrics.top) / 2f;
        canvas.rotate(-45, dis, dis);
        canvas.drawText(tipContent, dis, disY, textPaint);
        canvas.rotate(45, dis, dis);
        canvas.restore();
    }

    /**
     * 右上角梯形
     * @param canvas
     */
    private void drawTopRight(Canvas canvas, float realWaist) {
        int width = getMeasuredWidth();
        path.moveTo(width - this.realStartDisH, 0);
        path.lineTo(width - this.realStartDisH - realWaist, 0);
        path.lineTo(width, this.realStartDisH + realWaist);
        path.lineTo(width, this.realStartDisH);
        path.close();
        canvas.drawPath(path, backPaint);
        canvas.save();

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        float dis = (waistDis /2  + this.startDisH) /2;
        float disY = dis - (fontMetrics.bottom + fontMetrics.top) / 2;
        canvas.rotate(45, width - dis, dis);
        canvas.drawText(tipContent, width - dis, disY, textPaint);
        canvas.rotate(-45, width - dis, dis);
        canvas.restore();
    }

    /**
     * 左下角梯形
     * @param canvas
     */
    private void drawBottomLeft(Canvas canvas, float realWaist) {
        int height = getMeasuredHeight();
        path.moveTo(this.realStartDisH, height);
        path.lineTo(this.realStartDisH + realWaist, height);
        path.lineTo(0, height - this.realStartDisH - realWaist);
        path.lineTo(0, height - this.realStartDisH);
        path.close();
        canvas.drawPath(path, backPaint);
        canvas.save();

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        float dis = (waistDis /2  + this.startDisH) /2;
        float disY = dis + (fontMetrics.bottom + fontMetrics.top) / 2;
        canvas.rotate(45, dis, height - dis);
        canvas.drawText(tipContent, dis, height - disY, textPaint);
        canvas.rotate(-45, dis, height - dis);
        canvas.restore();
    }

    /**
     * 右下角梯形
     * @param canvas
     */
    private void drawBottomRight(Canvas canvas, float realWaist) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        path.moveTo(width - this.realStartDisH, height);
        path.lineTo(width - this.realStartDisH - realWaist, height);
        path.lineTo(width, height - this.realStartDisH - realWaist);
        path.lineTo(width, height - this.realStartDisH);
        path.close();
        canvas.drawPath(path, backPaint);
        canvas.save();

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        float dis = (waistDis /2  + this.startDisH) /2;
        float disY = dis + (fontMetrics.bottom + fontMetrics.top) / 2;
        canvas.rotate(-45, width - dis, height - dis);
        canvas.drawText(tipContent, width - dis, height - disY, textPaint);
        canvas.rotate(45, width - dis, height - dis);
        canvas.restore();
    }

    /**
     * 设置x方向上，起始点与所在角落点的距离
     * @param dis px
     */
    public void setRealStartDisH(int dis) {
        this.realStartDisH = dis;
    }

    /**
     * 设置Tip的显示位置
     * @param position 见 TIP_POSITION_FLAGS[]
     */
    public void setTipCornerPosition(int position) {
        this.tipCornerPosition = position;
    }

    /**
     * 设置提示文案, 会自动刷新View
     * @param content 文本
     */
    public void setTipContent(String content) {
        this.tipContent = content;
        invalidate();
    }


    /**
     * 设置提示文案和字距离梯形的上下边的间距，会自动刷新View
     * @param vPadding px
     */
    public void setTipTextVPadding(int vPadding) {
        this.textVPadding =vPadding;
        waistDis = 0;//在下一次dispatchDraw时需重新计算waistDis
        invalidate();
    }

    /**
     * 设置字体大小, 会自动刷新View
     * @param tipTextSize  px
     */
    public void setTipTextSize(int tipTextSize) {
        this.tipTextSize = tipTextSize;
        textPaint.setTextSize(tipTextSize);
        waistDis = 0;//字体大小改变了，需重新计算waistDis
        invalidate();
    }

    /**
     * 设置字体颜色
     * @param tipTextColor 颜色的值
     */
    public void setTipTextColor(int tipTextColor) {
        this.tipTextColor = tipTextColor;
        textPaint.setColor(tipTextColor);
    }

    /**
     * 设置梯形条颜色
     * @param tipBackgroundColor 颜色的值
     */
    public void setTipBackgroundColor(int tipBackgroundColor) {
        this.tipBackgroundColor = tipBackgroundColor;
        backPaint.setColor(tipBackgroundColor);
    }
}
