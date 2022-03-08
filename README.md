# CodeDrawableDsl

使用kotlin-dsl方式在代码中动态创建`Drawable`的库

- kotlin-dsl方式利用`build`设计模式，简化`Drawable`创建逻辑
- 尽可能覆盖了大部分常见的`xml`文件构建`Drawable`的标签，命名方式也贴近`xml`文件的使用，并提供多种常用的快捷方式
- 通过合理的代码管理复用机制，可以对`Drawable`的属性配置进行复用，相比xml文件更便于复用管理
- 目前**无法在原生布局`xml`文件中进行预览**，可能造成不熟悉的人比较疑惑。



## 实现背景

在日常开发过程中，会经常遇到一些UI设计的背景图像，需要在`res/drawable`的目录下自定义`shape`标签或者`selector`标签的`Drawable`。

- 随着业务逻辑的堆积，页面需要的更改等因素，经常会导致在`res/drawable`目录下的`drawable xml`文件迅速膨胀。

- 很多时候还只是圆角角度的变化，甚至只是修改一个颜色，导致很多通用的样式无法进行复用
- 当项目资源文件的管理不太规范时，面对一大堆`xml`文件，只能默默继续添加一个新的`xml`进行使用



## 集成与使用

### 依赖方式

[![](https://jitpack.io/v/com.gitee.yupfeg/CodeDrawableDsl.svg)](https://jitpack.io/#com.gitee.yupfeg/CodeDrawableDsl)

``` groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

//module build.gradle
dependencies {
    implementation 'com.gitee.yupfeg:CodeDrawableDsl:0.1.0'
}
```



目前支持代码创建的`xml`标签 ：

### `<shape>`标签

对应`GradientDrawableBuilder`类，使用`shapeDrawable`方法进行构建`Drawable`

``` kotlin
shapeDrawable{
    shapeType = ShapeType.RECTANGLE //默认值，矩形
    soild = ... //支持`ColorInt`值与`ColorStateList`对象
    radius = 5f //快捷四个圆角半径
    corner(5f,5f,5f,5f) //分别设置4个圆角的半径
    gradient = ... //渐变属性
    size(50,50) //对应xml的size属性
    stroke = ... //描边属性
}
```

> - 对于`shapeType`设置为`ShapeType.RING`时，绘制的圆环图形，其专有的`innerRadius`、`innerRadiusRatio`、`thickness`、`thicknessRatio`属性都只有在API 29以上才能使用代码添加，目前暂不开放设置
> - 对于`padding`属性，只有在API 29版本以上才能代码添加，暂不支持设置

### `<stroke>`标签

仅在`<shape>`标签对应`shapeDrawable`方法内使用，对应`ShapeStrokeConfig`类，可由`shapeStroke`方法构建。

``` kotlin
shapeStroke{
    width = 1f //描边宽度，dp
    color = ... //描边颜色，支持`ColorInt`值与`ColorStateList`对象
    dashWidth = 5f  //虚线的长度，dp
    dashGap = 4f //虚线间的间距，dp
 }
```

### `<gradient>`标签

仅在`<shape>`标签对应`shapeDrawable`方法内使用，对应`GradientColorConfig`类，可由`gradientColor`方法配置构建。

``` kotlin
gradientColor{
    startColor = ... //渐变起始颜色
    endColor = ... //渐变结束颜色
    centerColor = ... //渐变中间颜色
    type = ... //渐变类型
    angle = ... //渐变的方向角度，快捷设置同xml的angle ，0-360
    orientation = ... //渐变的方向，angle实际修改的属性
 }
```

### `<selector>`标签

分为两种情况：

1. 当设置在`res/colors`时，解析为`ColorStateList`，对应为`ColorStateListBuilder`。

使用`selectorColor`方法创建颜色状态集`ColorStateList`。

``` kotlin
selectorColor{
    addItem{ //添加指定状态时显示的颜色
        color = ... //只支持单个颜色值
        addState(...) //添加对应状态
    } 
}
```

`addItem`方法添加的是`ColorStateListItem`，同样支持`kotlin-dsl`方式，在密封类`AttrStates`内部封装了常用的`attr`状态属性，可设置满足多种的状态条件的颜色

2. 当设置在`res/drawable`时，解析为`DrawableStateList`，对应`StateListDrawableBuilder`。

使用`selectorDrawable`方法创建图像`Drawable`

``` kotlin
selectorDrawable{
     addItem{ //添加指定状态时显示的图像
         drawable = ...
         addState(...) //添加对应状态
     } 
}
```

`addItem`方法添加的是`DrawableStateListItem`，同样支持`kotlin-dsl`方式，在密封类`AttrStates`内部封装了常用的`attr`状态属性，可设置满足多种的状态条件的颜色

> 针对`<selector>`标签，提供了多种快捷设置属性，比如：
>
> ``` kotlin
> selectorDrawable{
>     normal = ... //表示默认状态显示图像
>     pressed = ... //表示在视图按下时显示该图像
>     selected = ... //表示在视图选中时显示的图像
> }
> ```
>
> 在`ColorStateListBuilder`与`StateListDrawableBuilder`类中都提供了大部分常用的状态设置

### `<layer-list>`标签

图层标签，可以叠加多个`Drawable`实例，使用`layerDrawable`方法构建

``` kotlin
layerDrawable{
     addItem{
        id = ... //可给图层图像设置id 
        drawable = ...
     }
     addItem{
        drawable = ...
     }
}
```

### `<clip>`标签

剪裁标签，通常配合`<layer-list>`标签内使用，可使用`clipDrawable`方法构建，比如在原生`ProgressBar`内设置`progressDrawable`属性时使用，限定在底层图层内显示

``` kotlin
clipDrawable{
    drawable = ... //参与剪裁的原始drawable
}
```

### `<ripple>`标签

水波纹标签，只在API 21以上生效，使用`rippleDrawable`方法构建

> 已进行版本适配，只在API 21以上创建新的`RippleDrawable`实例，否则返回`item`变量内的`Drawable`实例

``` kotlin
rippleDrawable{
    color = ... //水波纹的颜色
    item = ... //原始的drawable
    mask = ... //遮罩drawable
}
```

### `<rotate>`标签

旋转图形标签，可使用`rotateDrawable`方法构建，比如在原生`ProgressBar`内设置`indeterminateDrawable`属性时使用，设置旋转进度

``` kotlin
rotateDrawable{
    drawable = ... //参与旋转的drawable
    fromDegree = 0f //默认值，旋转起始角度
    toDegree = 360f //默认值，旋转结束角度
    pivot(0.5f,0.5f) //默认值，旋转的中心点
}
```

> `<rotate>`标签内并没有`duration`属性，网上有些文章上强行解释加上`android:duration`属性设置动画时间，实际上并没有这个属性，设置了也不会生效。





> - 对于`<animated-selector>`、`<animation-list>`等逐帧动画`xml`标签不常用甚至已经基本废弃的，暂不提供支持
>
> - `<vector>`标签通常都是利用`AndroidStdio`将`svg`图片自动转换生成的，里面`path`属性自己手动创建的可能性很低，同样不提供支持
> - `<bitmap>`、`<nine-patch>`、`<inset>`这些很少会使用到的标签也暂不提供支持



## 注意事项

- 所有`xxxDrawable`方法，都会创建一个**新的`Drawable`实例**，在进行性能优化时需要考虑

- 对于同一个`Drawable`，特别是`selectorXXX`方法进行创建的，能够响应视图状态的`Drawable`或`Color`，

  如果置于`RecyclerView`等进行视图复用的控件视图时，需要在**每一个ItemViewHolder创建时**赋值新的`Drawable`对象实例，否则在Item视图复用时，同一个`Drawable`对象对应的状态会在另一个Item视图内响应状态，从而造成界面错乱等不可预知的问题。



## 实现思路

要实现动态代码创建`drawable`的操作，首先来看看在`res/drawable`文件中定义的`xml`文件是如何被转换生成`Drawable`的。

在系统文件中存在一个`DrawableInflater`类，专门负责将`xml`提取转化为`Drawable`对象实例的

```java
//android.graphics.drawable.DrawableInflater.java
public final class DrawableInflater {
	//负责从xml中构建drawable
    private Drawable inflateFromTag(@NonNull String name) {
        
         switch (name) {
             //省略部分标签类型
           case "selector":
             return new StateListDrawable();
           case "layer-list":
             return new LayerDrawable();
           case "ripple":
             return new RippleDrawable();
           case "shape":
             return new GradientDrawable();
           case "clip":
             return new ClipDrawable();
           case "rotate":
             return new RotateDrawable();
         }
    }
}
```

在其中可以很多熟悉的标签，这里的工作就是将对应的标签转化为`Drawable`，比如最常用的`<shape>`标签，其会创建为`GradientDrawable`对象实例

而对其内部子标签的识别设置，就会交由`GradientDrawable`内部来导入。

``` java
//android.graphics.drawable.GradientDrawable.java
private void inflateChildElements(Resources r, XmlPullParser parser, AttributeSet attrs,
            Theme theme) throws XmlPullParserException, IOException {
    TypedArray a;
    final int innerDepth = parser.getDepth() + 1;
    int depth;
    while ((type=parser.next()) != XmlPullParser.END_DOCUMENT
           && ((depth=parser.getDepth()) >= innerDepth
               || type != XmlPullParser.END_TAG)) {
        ...
        String name = parser.getName();
        if (name.equals("size")) {
            a = obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableSize);
            updateGradientDrawableSize(a);
            a.recycle();
        } else if (name.equals("gradient")) {
            a = obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableGradient);
            updateGradientDrawableGradient(r, a);
            a.recycle();
        } else if (name.equals("solid")) {
            a = obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableSolid);
            updateGradientDrawableSolid(a);
            a.recycle();
        } else if (name.equals("stroke")) {
            a = obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableStroke);
            updateGradientDrawableStroke(a);
            a.recycle();
        } else if (name.equals("corners")) {
            a = obtainAttributes(r, theme, attrs, R.styleable.DrawableCorners);
            updateDrawableCorners(a);
            a.recycle();
        } else if (name.equals("padding")) {
            a = obtainAttributes(r, theme, attrs, R.styleable.GradientDrawablePadding);
            updateGradientDrawablePadding(a);
            a.recycle();
        }
        ...
    }
}
```

至此就将`<shape>`标签内的所有支持的子标签都识别导入完成，进而完成一个完整`Drawable`的创建。

所以剩下的工作就相当简单了，把`xml`标签上对应创建的`drawable`对象实例上，所有支持的属性利用`builder`设计模式，创建一个`DrawableBuilder`类，来设置需要配置的属性，最后通过`build`方法创建对应`Drawable`的对象即可。

而所谓的`kotlin-dsl`方式，就是利用`Kotlin`语法中支持**函数类型参数**的特性，利用以`DrawableBuilder`作为接收者的函数类型参数，配合范围函数`apply`，即可实现直接在`{}`内赋值属性的`kotlin`风格语法。

``` kotlin
shapeDrawable {
    solid = selectorColor {
        normal = Color.parseColor("#EDEDED")
        pressed = Color.parseColor("#B0BEC5")
    }
    radius = 15f
    stroke = shapeStroke {
        width = 1f
        color = Color.parseColor("#DCDCDC")
    }
}
```



