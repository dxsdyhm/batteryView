package com.example.user.batteryview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Locale;

/**
 * Created by dxs on 2018/4/9.
 */

public class BatteryView extends View {
    private RectF batteryRecf;
    private RectF batterCenterRecf;
    private Paint mPaint;
    private Paint mTextPaint;
    private float ratio=0.5f;
    private int lineW=1;
    private float lineRatio=0.5f;
    private int textSize=10;
    private int[][] col=new int[][]{{20,100},{Color.parseColor("#ff0000"),Color.parseColor("#4ec200")}};
    private int currentValue=100;
    PorterDuffXfermode modeTx=new PorterDuffXfermode(PorterDuff.Mode.XOR);
    public BatteryView(Context context) {
        this(context,null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        lineW=dip2px(context,lineW);
        batteryRecf=new RectF();
        batterCenterRecf=new RectF();
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#4ec200"));
        mPaint.setStrokeWidth(lineW);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint=new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(dip2px(context, textSize));
        mTextPaint.setXfermode(modeTx);
        mTextPaint.setColor(Color.parseColor("#4ec200"));

        //关闭硬件加速（神坑）
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heighMode=MeasureSpec.getMode(heightMeasureSpec);
        int heighSize=MeasureSpec.getSize(heightMeasureSpec);
        int mesW,mesH;
        if(widthMode==MeasureSpec.EXACTLY){
            mesW=widthSize;
        }else{
            mesW=dip2px(getContext(),100);
        }

        if(heighMode==MeasureSpec.EXACTLY){
            mesH=heighSize;
        }else{
            mesH= (int) (mesW*ratio);
        }
        setMeasuredDimension(mesW,mesH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float batterW=w-2*lineW;
        float batterH=batterW*ratio;
        batteryRecf.set(lineW/2,(h-batterH)/2,batterW, (h+batterH)/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        resetTextPaint();
        drawBattery(canvas);
        drawBatteryCennter(canvas);
        mTextPaint.setXfermode(modeTx);
        drawText(canvas);
        mTextPaint.setXfermode(null);
    }

    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (batteryRecf.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        String text=getFormatText(currentValue);
        canvas.drawText(text,batteryRecf.centerX(),baseLineY,mTextPaint);
    }

    private void resetTextPaint(){
        for(int i=0;i<col[0].length;i++){
            if(currentValue>=0&&currentValue<=col[0][i]){
                mTextPaint.setColor(col[1][i]);
                mPaint.setColor(col[1][i]);
                break;
            }
        }
    }

    public void setCol(int[][] col){
        this.col=col;
    }

    private void drawBattery(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(batteryRecf,4,4,mPaint);
        float H=batteryRecf.height();
        float line=H*lineRatio;
        float top=batteryRecf.top;
        canvas.drawLine(batteryRecf.right+lineW,top+(H-line)/2,batteryRecf.right+lineW,top+(H+line)/2,mPaint);
    }

    private void drawBatteryCennter(Canvas canvas){
        batterCenterRecf.set(batteryRecf);
        batterCenterRecf.right=batteryRecf.width()*currentValue/100;
        canvas.drawRect(batterCenterRecf,mTextPaint);
    }

    public void setCurrentValue(int value){
        this.currentValue=value;
        invalidate();
    }

    public void setCurrentValue(String text){
        //empty
    }

    private static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private String getFormatText(int value){
        return ""+value+"%";
    }
}
