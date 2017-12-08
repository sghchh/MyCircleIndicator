package com.example.as.mycircleindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by as on 2017/12/7.
 */

public class CircleIndicatorView extends View implements ViewPager.OnPageChangeListener {

    private static final int WRAP_WIDTH=300;    //wrap_content下的默认宽度
    private static final int WRAP_HEIGHT=35;       //wrap_content下的默认高度
    private static final char[] letters=new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T'};

    private static final float DEF_INDICATORRADIUS=25;    //圆环半径的默认
    private static final float DEF_INDICATORBORDERWIDTH=6;    //圆环环宽的默认宽度
    private static final float DEF_INDICATORSPACE=10;    //圆环间距的默认值
    private static final int DEF_INDICATORCOLOR=Color.parseColor("#FFFF9988");   //圆环颜色的默认值
    private static final int DEF_TEXTCOLOR=Color.parseColor("#FF00FF55");   //圆环内容颜色的默认值
    private static final int DEF_SELECTCOLOR=Color.parseColor("#FF990078");   //显示当前内容的圆环的默认颜色

    private float indicatorRadius;    //圆环的半径
    private float indicatorBorderWidth;   //圆环的环宽
    private float indicatorSpace;    //圆环之间的距离
    private int indicatorTextColor;   //除了NONE风格下，里面字 的颜色
    private int indicatorColor;    //圆环的颜色
    private int indicatorSelectColor;    //指示当前的圆环的显示颜色

    private Paint indicatorPaint;   //画圆环的画笔
    private Paint textPaint;   //画文字的画笔
    private Paint currentPaint;   //专门用来画当前的圆的画笔

    private FillMode fillMode;    //显示的状态

    private ViewPager pager;     //被绑定的ViewPager

    private int num;    //圆圈的数量，由ViewPager的item数目确定

    private List<Circle> circles=new ArrayList<>();  //保存每一个画的圈的状态

    private int currentPosition=0;   //表示当前显示的是ViewPager哪个页，默认为0，即第一页

    public CircleIndicatorView(Context context) {
        this(context,null,0);
    }

    public CircleIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        //初始化参数
        initParams(attrs);

        //初始化画笔
        initPaints();
    }

    /*
    public CircleIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //初始化参数
        initParams(attrs);

        //初始化画笔
        initPaints();
    }
    */


    /**
     * 得到XML中的属性的值，并且初始化
     * @param attrs XML中设置的值
     */
    private void initParams(AttributeSet attrs)
    {
        TypedArray array=getContext().obtainStyledAttributes(attrs,R.styleable.CircleIndicatorView);
        indicatorRadius=array.getDimension(R.styleable.CircleIndicatorView_indicatorRadius,DEF_INDICATORRADIUS);
        indicatorBorderWidth=array.getDimension(R.styleable.CircleIndicatorView_indicatorBorderWidth,DEF_INDICATORBORDERWIDTH);
        indicatorSpace=array.getDimension(R.styleable.CircleIndicatorView_indicatorSpace,DEF_INDICATORSPACE);
        indicatorColor=array.getColor(R.styleable.CircleIndicatorView_indicatorColor,DEF_INDICATORCOLOR);
        indicatorSelectColor=array.getColor(R.styleable.CircleIndicatorView_indicatorSelectColor,DEF_SELECTCOLOR);
        indicatorTextColor=array.getColor(R.styleable.CircleIndicatorView_indicatorTextColor,DEF_TEXTCOLOR);
        switch (array.getInt(R.styleable.CircleIndicatorView_fill_mode,2))
        {
            case 0:
                fillMode=FillMode.LETTER;
                break;
            case 1:
                fillMode=FillMode.NUMBER;
                break;
            case 2:
                fillMode=FillMode.NONE;
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, WRAP_HEIGHT);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, WRAP_HEIGHT);
        }

*/
        int width=(int)((indicatorRadius+indicatorBorderWidth)*2*num+indicatorSpace*(num-1)+(indicatorRadius+indicatorBorderWidth)*2*0.3f);
        int height=(int)(indicatorRadius*2+indicatorSpace*2.6);

        setMeasuredDimension(width,height);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i=1;i<=num;i++)
        {
            //如果当前绘制的是显示的当前页的圆
            if((i-1)==currentPosition)
            {
                float cx=(indicatorRadius+indicatorBorderWidth)*1.3f+(i-1)*(indicatorBorderWidth*2+indicatorRadius*2+indicatorSpace);
                float cy=indicatorRadius+indicatorSpace;
                float rad=indicatorBorderWidth*1.3f+indicatorRadius*1.3f;
                canvas.drawCircle(cx,cy,rad,currentPaint);
                if(fillMode==FillMode.NUMBER)
                {
                    canvas.drawText(i+"",cx-(indicatorRadius+indicatorBorderWidth)*0.5f,cy*1.3f,currentPaint);
                }
                if(fillMode==FillMode.LETTER)
                    canvas.drawText(letters[i-1]+"",cx-(indicatorRadius+indicatorBorderWidth)*0.5f,cy*1.3f,currentPaint);
            }
            else
            {

                float cx=(indicatorRadius+indicatorBorderWidth)*1+(i-1)*(indicatorBorderWidth*2+indicatorRadius*2+indicatorSpace);
                //如果是在当前页之后的原点，则圆的圆心需要调一调
                cx=((i-1)>currentPosition)?cx+(indicatorRadius+indicatorBorderWidth)*0.3f*2:cx;
                float cy=indicatorRadius+indicatorSpace;
                float rad=indicatorBorderWidth+indicatorRadius;
                canvas.drawCircle(cx,cy,rad,indicatorPaint);
                if(fillMode==FillMode.NUMBER)
                    canvas.drawText(i+"",cx-(indicatorRadius+indicatorBorderWidth)*0.5f,cy*1.3f,textPaint);
                if(fillMode==FillMode.LETTER)
                    canvas.drawText(letters[i-1]+"",cx-(indicatorRadius+indicatorBorderWidth)*0.5f,cy*1.3f,textPaint);
            }
        }

    }

    /**
     * 初始化画笔
     */
    private void initPaints()
    {
        indicatorPaint=new Paint();
        indicatorPaint.setStrokeWidth(indicatorBorderWidth);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStyle(Paint.Style.STROKE);

        textPaint=new Paint();
        textPaint.setColor(indicatorTextColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(65f);

        currentPaint=new Paint();
        currentPaint.setColor(indicatorSelectColor);
        currentPaint.setAntiAlias(true);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(indicatorBorderWidth*1.3f);
        currentPaint.setTextSize(65f);
    }

    /**
     * 保存记录的ViewPager，获取当前ViewPager中item的数目
     * @param viewPager
     */
    public void attachViewPager(@NonNull ViewPager viewPager)
    {
        this.pager=viewPager;
        pager.addOnPageChangeListener(this);
        PagerAdapter adapter=pager.getAdapter();

        //如果adapter为空，则圆环的数目为0
        //否则为adapter中的item的数目

        num=adapter!=null?adapter.getCount():0;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition=position;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 枚举类
     * 三个值代表三种显示风格
     */
    public enum FillMode{
        LETTER,NUMBER,NONE;
    }



    /**
     * 画的圈
     */
    class Circle{
        float cx,cy,rad;
        Circle(float cx,float cy,float rad)
        {
            this.cx=cx;
            this.cy=cy;
            this.rad=rad;
        }
    }
}
