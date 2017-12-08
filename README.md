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
