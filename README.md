# MyCircleIndicator
学习网上的CircleIndicatorView实现圆点指示器
# Android自定义View实现圆点指示器  
> 本项目是学习网上的项目练习用的https://github.com/pinguo-zhouwei/CircleIndicatorView  

很少去自定义View，今天拿着个作为一个知识的巩固和应用。由于前一段时间有要求用这个圆点指示器的效果，但是谷歌官方没有对应的控件，所以不得已拿网上的一个开源的顶了上去，今天就来学习一个。  
答题的思路：  
* 首先要提供一个绑定ViewPager的方法，绑定一个ViewPager  
* 更具ViewPager的item数确定要绘制的圆圈的数目  
* 计算出各个圆圈的圆心，半径  
* 定义一个变量表示ViewPager当前显示的界面的position，对应的原点要比其他的大一圈  
* 实现OnPageChangeListener接口，用来监听ViewPager页面的切换，进而清新绘制控件，来指示正确的位置  

实现步骤：  
* 自定义可能使用到的属性的xml文件  
* 创建对应的类继承View  
* 实现构造器，onMeasure（），onDraw（）方法  
* 实现OnPageChangeListener接口  

## 1. 具体实现  
### 1.1 自定义属性  
```  
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="CircleIndicatorView">
        <attr name="indicatorRadius" format="dimension"/>
        <attr name="indicatorBorderWidth" format="dimension"/>
        <attr name="indicatorSpace" format="dimension"/>
        <attr name="indicatorTextColor" format="color"/>
        <attr name="indicatorColor" format="color"/>
        <attr name="indicatorSelectColor" format="color"/>
        <attr name="fill_mode">
            <enum name="letter" value="0"/>
            <enum name="number" value="1"/>
            <enum name="none" value="2"/>
        </attr>
    </declare-styleable>

```  

这些属性根据名字差不多都能知道是干什么的，就不做多余的讲解了。值得学习的是在fill_mode这个属性定义的时候使用的是枚举类型的数据，这样对于用户和开发者来说都是一件方便的事儿，以前用的少，以后要多学习学习。  

### 1.2 对应类的实现  
```
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
```

根据这些注释就可以看出那些属性的意思了。  
#### 1.2.1 drawText方法的坑  
我刚开始通过canvas.drawText(String text,int cx,int cy,Paint paint)方法绘制文字的时候直接把其所在的圆的圆心的坐标作为了该方法的第二三个参数，但是得到的效果吓死人，文字的左下角为圆心的坐标，文字整体重心根本不在圆心。想要了解为什么，就要搞清楚cx，cy这两个参数的意义到底是什么：  
> drawText的学习，参考这篇文章：http://hencoder.com/ui-1-3/  

drawText() 参数中的 y ，指的是文字的基线（ baseline ） 的位置。也就是这条线：
![image](https://ws3.sinaimg.cn/large/52eb2279ly1fig6137j5sj20a502rglw.jpg);  
众所周知，不同的语言和文字，每个字符的高度和上下位置都是不一样的。要让不同的文字并排显示的时候整体看起来稳当，需要让它们上下对齐。但这个对齐的方式，不能是简单的「底部对齐」或「顶部对齐」或「中间对齐」，而应该是一种类似于「重心对齐」的方式。就像电线上的小鸟一样：  
![image](https://ws3.sinaimg.cn/large/52eb2279ly1fig61bpsw5j20dv04o3yv.jpg);  
> 每只小鸟的最高点和最低点都不一样，但画面很平衡  

而这个用来让所有文字互相对齐的基准线，就是基线( baseline )。 drawText() 方法参数中的 y 值，就是指定的基线的位置。  
说完 y 值，再说说 x 值。从前面图中的标记可以看出来，「Hello HenCoder」绘制出来之后的 x 点并不是字母 "H" 左边的位置，而是比它的左边再往左一点点。那么这个「往左的一点点」是什么呢？

它是字母 "H" 的左边的空隙。绝大多数的字符，它们的宽度都是要略微大于实际显示的宽度的。字符的左右两边会留出一部分空隙，用于文字之间的间隔，以及文字和边框的间隔。就像这样：  
![](https://ws3.sinaimg.cn/large/52eb2279ly1fig61gild3j20x607iabs.jpg);  
根据大神的解答，我在绘制圆圈里面的文字的时候，于是改成了如下：  
```  
canvas.drawText(i+"",cx-(indicatorRadius+indicatorBorderWidth)*0.5f,cy*1.3f,currentPaint);  
其中，cx为该圆圈的圆心横坐标，cy为纵坐标；(indicatorRadius+indicatorBorderWidth)为圆环的内侧半径加上外环的宽。
```  

这么一改，文本基本被绘制到了圆心的中心。  
