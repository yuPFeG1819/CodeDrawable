package com.yupfeg.drawable

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt

/**
 * 构建图形图像对象，
 * - 对于`RING`圆环形状的支持并不完善，圆环的多个多个属性需要API 29以上才开放使用
 *
 * 如Kotlin-dsl使用方式：
 * ``` kotlin
 * shapeDrawable{
 *      shapeType = ShapeType.RECTANGLE     //默认值，可以不设置
 *      solid = ...
 *      radius = 3.0f
 *      gradient = gradientColor{
 *          startColor = ...
 *          endColor = ...
 *      }
 *      size(100,100)
 * }
 *
 * ```
 *
 * 等价于：
 * ``` xml
 * <shape ...
 *      android:shape="rectangle"
 *      >
 *
 *      <solid android:color="..."/>
 *
 *      <corners
 *          android:radius="3dp"
 *          />
 *
 *      <size
 *          android:width = "100dp"
 *          android:height = "100dp"
 *          />
 *      <gradient
 *
 *          />
 * </shape>
 * ```
 * [GradientDrawable API使用指南](https://www.apiref.com/android-zh/android/graphics/drawable/GradientDrawable.html)
 * @param config kotlin-dsl配置图形属性
 * */
@Suppress("unused")
fun shapeDrawable(config : GradientDrawableBuilder.()->Unit) : GradientDrawable{
    return shapeDrawableBuilder(config).build()
}

/**
 * 创建图形图像的构建类实例
 * - 如果想要直接构建图形drawable，则直接使用[shapeDrawable]方法
 * @param config kotlin-dsl配置图形属性
 * */
fun shapeDrawableBuilder(config : GradientDrawableBuilder.()->Unit) : GradientDrawableBuilder {
    return GradientDrawableBuilder().apply(config)
}

/**
 * drawable绘制形状类型
 * */
@Suppress("unused")
enum class ShapeType{
    /**矩形*/RECTANGLE,/**圆形*/OVAL,/**直线*/LINE,/**圆环*/RING
}

/**
 * 替代xml内设置Shape标签的Drawable构建类
 * - padding属性，只有在API 29版本以上才能代码添加，暂不支持设置
 * - 当图形类型为ring圆环时，`innerRadius`、`innerRadiusRatio`、`thickness` 、
 * `thicknessRatio`属性都只有在API 29以上才能使用代码添加，目前暂不开放设置
 *
 * @author yuPFeG
 * @date 2022/03/06
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class GradientDrawableBuilder internal constructor() : DrawableBuilder {

    /**
     * 图形的类型[ShapeType]，默认为矩形
     * - 谨慎使用[ShapeType.RING],暂不提供圆环的专有属性
     * */
    var shapeType = ShapeType.RECTANGLE

    // <editor-fold desc="图形填充颜色属性">
    /**
     * 图形填充的颜色
     * - 在API 21 以下，只支持[ColorInt]的单个颜色值，注意版本适配
     * - 在API 21 以上，才开始支持[ColorStateList]的颜色状态集
     * - 优先级最低，如果设置了[gradient]，该值会被忽略
     * */
    var solid : Any
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            when(value){
                is Int -> solidColor = value
                is ColorStateList -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        solidColorStateList = value
                    }else{
                        throw IllegalArgumentException(
                            "color state list support after API 21 LOLLIPOP"
                        )
                    }
                }
                else -> {
                    throw IllegalArgumentException(
                        "solid property only support color int and ColorStateList"
                    )
                }
            }
        }

    /**
     * 图形填充颜色，会同步修改[solidColorStateList]
     * - 优先级最低
     * */
    @ColorInt
    private var solidColor : Int = Color.TRANSPARENT
        set(value) {
            field = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                solidColorStateList = ColorStateList.valueOf(value)
            }
        }

    /**
     * 图形填充的颜色状态集
     * - 优先级最低
     * - 如果使用单色填充，则直接修改[solidColor]
     * */
    private var solidColorStateList : ColorStateList? = null

    /**
     * 渐变颜色
     * - 优先级比[solidColor]与[solidColorStateList]要高
     * */
    var gradient : GradientColorConfig? = null

    /**图形的颜色过滤器*/
    var shapeColorFilter : ColorFilter? = null

    // </editor-fold>

    // <editor-fold desc="图形的四边圆角属性">

    /**
     * 图形的圆角半径
     * - 会同步设置[mRadiusArray]属性
     * - 仅在[shapeType]为[ShapeType.RECTANGLE]时生效
     * */
    var radius : Float = 0f
        set(value) {
            field = value
            mRadiusArray = floatArrayOf(value,value,value,value,value,value,value,value)
        }

    /**
     * 图形的四个角圆角半径
     * - 仅在[shapeType]为[ShapeType.RECTANGLE]时生效
     * - 最少设置8个值的数组，超出8个值不生效，每个角表示x,y两个值。
     * - 设置顺序为左上->右上->右下->左下，沿顺时针方向
     * */
    private var mRadiusArray : FloatArray? = null

    // </editor-fold>

    /**
     * 指定绘制形状的宽度，单位dp
     * - 最终大小还是按照视图大小
     * */
    internal var width : Int = -1
    /**
     * 指定绘制形状的宽度，单位dp
     * - 最终大小还是按照视图大小
     * */
    internal var height : Int = -1

    /**
     * 图形的描边属性配置
     * */
    var stroke : ShapeStrokeConfig? = null

    // <editor-fold desc="图形着色属性">

    /**
     * 图形着色颜色
     * - 需要配合[tintMode]使用
     * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @ColorInt
    var tintColor : Int = Color.TRANSPARENT

    /**
     * 图形着色颜色状态集，需要配合[tintMode]
     * - 如果设置单色，则直接赋值[tintColor]
     * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var tintColorStateList : ColorStateList? = null

    /**图形混合模式，默认为src_in*/
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var tintMode : PorterDuff.Mode? = null

    // </editor-fold>

    /**
     * 设置图形的四个角圆角半径
     * - 仅在[shapeType]为[ShapeType.RECTANGLE]时生效
     * - 如果需要设置相同的四个圆角半径，则直接赋值[radius]属性
     * @param topLeft 左上角圆角半径，dp
     * @param topRight 右上角圆角半径，dp
     * @param bottomRight 右下角圆角半径，dp
     * @param bottomLeft 左下角圆角半径，dp
     * */
    fun corner(topLeft : Float,topRight : Float,bottomRight : Float,bottomLeft : Float){
        val displayMetrics = Resources.getSystem().displayMetrics
        val topLeftPx = if (topLeft <= 0) 0f else{
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,topLeft,displayMetrics)
        }
        val topRightPx = if (topRight <= 0) 0f else{
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,topRight,displayMetrics)
        }
        val bottomLeftPx = if (bottomLeft <= 0) 0f else{
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,bottomLeft,displayMetrics)
        }
        val bottomRightPx = if (bottomRight <= 0) 0f else{
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,bottomRight,displayMetrics)
        }
        //设置顺序为左上->右上->右下->左下，沿顺时针方向
        mRadiusArray = floatArrayOf(
            topLeftPx,topLeftPx,topRightPx,topRightPx,
            bottomLeftPx,bottomLeftPx,bottomRightPx,bottomRightPx
        )
    }

    /**
     * 设置图形大小，最终尺寸由附加的视图决定
     * @param width 图形宽度，单位dp
     * @param height 图形高度，单位dp
     * */
    fun size(width : Int,height : Int){
        this.width = width
        this.height = height
    }

    /**
     * 构建图形drawable
     * */
    override fun build() : GradientDrawable{
        return GradientDrawable().apply{
            setShapeType()
            setShapeColor()
            setRadius()
            setShapeSize()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setTintColor()
                setShapeStroke()
            }
        }
    }

    /**
     * [GradientDrawable]的拓展函数，设置图形形状类型
     * */
    private fun GradientDrawable.setShapeType() {
        this.shape = when(shapeType){
            ShapeType.RECTANGLE -> GradientDrawable.RECTANGLE
            ShapeType.OVAL -> GradientDrawable.OVAL
            ShapeType.LINE -> GradientDrawable.LINE
            ShapeType.RING -> GradientDrawable.RING
        }
    }

    /**
     * [GradientDrawable]的拓展函数，设置图形颜色
     * */
    private fun GradientDrawable.setShapeColor(){
        shapeColorFilter?.also {
            this.colorFilter = it
        }

        gradient?.also { config->
            //存在渐变颜色
            this.colors = if (config.centerColor != 0) {
                intArrayOf(config.startColor, config.centerColor, config.endColor)
            }else {
                intArrayOf(config.startColor,config.endColor)
            }
            this.setGradientCenter(config.centerX,config.centerY)
            //渐变的方向
            this.orientation = config.orientation
            //渐变类型
            this.gradientType = config.type.value
            if (config.type.value == GradientDrawable.RADIAL_GRADIENT){
                this.gradientRadius = config.radius
            }
            return
        }

        //没有渐变色，使用单色填充
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            solidColorStateList?:throw NullPointerException("you should set solid color On Gradient Drawable")
            this.color = solidColorStateList
        }else{
            //在API 21以下，只支持单色
            setColor(solidColor)
        }
    }

    /**
     * [GradientDrawable]的拓展函数，设置图形的着色混合颜色
     * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun GradientDrawable.setTintColor(){
        tintColorStateList?:return
        tintMode?:return
        this.setTintList(tintColorStateList)
        this.setTintMode(tintMode)
    }

    /**
     * [GradientDrawable]的拓展函数，设置圆角半径属性
     * */
    private fun GradientDrawable.setRadius(){
        mRadiusArray?: return
        if (mRadiusArray?.size?:0 < 8) return
        this.cornerRadii = mRadiusArray
    }

    /**
     * [GradientDrawable]的拓展函数，设置图形的描边属性
     * */
    private fun GradientDrawable.setShapeStroke(){
        stroke?: return
        val strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,stroke?.width?:0f,Resources.getSystem().displayMetrics
        )
        var dashWidth = stroke?.dashWidth?:0f
        if (dashWidth > 0){
            dashWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,dashWidth,Resources.getSystem().displayMetrics
            )
        }
        var dashGap = (stroke?.dashGap?:0f)
        if (dashGap > 0){
            dashGap= TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,dashGap,Resources.getSystem().displayMetrics
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setStroke(strokeWidth.roundToInt(),stroke!!.colorStateList,dashWidth,dashGap)
        }else{
            this.setStroke(strokeWidth.roundToInt(),stroke!!.strokeColor)
        }
    }

    /**
     * [GradientDrawable]的拓展函数，设置图形的尺寸大小
     * */
    private fun GradientDrawable.setShapeSize(){
        if (width > 0 && height > 0){
            val widthPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,width.toFloat(),
                Resources.getSystem().displayMetrics
            )
            val heightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,height.toFloat(),
                Resources.getSystem().displayMetrics
            )
            setSize(widthPx.roundToInt(), heightPx.roundToInt())
        }
    }
}