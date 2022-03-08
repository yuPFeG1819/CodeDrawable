package com.yupfeg.drawable

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi

/**
 * 构建新的支持水波纹的Drawable
 * - 只在API 21以上才生效，在API 21以下，直接返回原始drawable
 *
 * kotlin-dsl使用方式：
 * ``` kotlin
 * rippleDrawable{
 *      color = ...
 *      drawable = shapeDrawable{
 *         ...
 *      }
 * }
 * ```
 *
 * 等效于：
 * <ripple color = "...">
 *   <item>
 *       <shape>...</shape>
 *   <item>
 * </ripple>
 *
 * @param config kotlin-dsl配置方法
 * */
fun rippleDrawable(config : RippleDrawableBuilder.()->Unit) : Drawable{
    return rippleDrawableBuilder(config).build()
}

/**
 * 创建水波纹Drawable的构建类实例
 * - 如直接创建Drawable，可以使用[rippleDrawable]
 * @param config kotlin-dsl配置方法
 * */
fun rippleDrawableBuilder(config: RippleDrawableBuilder.() -> Unit) : RippleDrawableBuilder{
    return RippleDrawableBuilder().apply(config)
}

/**
 * API 21以上生效的水波纹drawable构建类
 * [RippleDrawable的API指南](https://www.apiref.com/android-zh/android/graphics/drawable/RippleDrawable.html)
 *
 * [RippleDrawable的详细使用分析](https://cloud.tencent.com/developer/article/1677324)
 * ![水波纹作用范围](https://ask.qcloudimg.com/http-save/yehe-7652309/gnsl2n01q3.jpeg?imageView2/2/w/1620)
 * @author yuPFeG
 * @date 2022/03/07
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class RippleDrawableBuilder internal constructor(): DrawableBuilder{

    /**
     * 水波纹颜色
     * - 支持[ColorInt]的单个颜色值
     * - 支持[ColorStateList]的颜色状态集
     * */
    var color : Any
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            when(value){
                is Int -> mColor = value
                is ColorStateList -> mColorStateList = value
                else -> {
                    throw IllegalArgumentException(
                        "solid property only support color int and ColorStateList"
                    )
                }
            }
        }

    /**
     * 水波纹的单色颜色
     * */
    @ColorInt
    private var mColor : Int = Color.TRANSPARENT
        set(value) {
            field = value
            mColorStateList = ColorStateList.valueOf(value)
        }

    /**
     * 水波纹的颜色状态集
     * */
    private var mColorStateList : ColorStateList? = null

    /**
     * 原始绘制Drawable
     * - `RippleDrawable`是`LayerDrawable`的子类，
     * 如果[rippleDrawable]方法的[item]属性直接引用`layerDrawable`或者`selectorDrawable`方法构建的[Drawable]实例，
     * 而且该[Drawable]实例在其他视图复用了，可能会同步触发其他视图上的[Drawable]的状态显示，造成不可预期的问题。
     * */
    var item : Drawable? = null

    /**
     * 顶层遮罩Drawable，只在视图触控后，触发水波纹时绘制，限制水波纹的范围与形状
     * */
    var mask : Drawable? = null

    /**
     * 水波纹完全展开时的波纹半径，默认为[RippleDrawable.RADIUS_AUTO]，由视图尺寸自动计算展开半径
     */
    @RequiresApi(Build.VERSION_CODES.M)
    var radius : Int = RippleDrawable.RADIUS_AUTO

    /**
     * 水波纹产生的热点x轴坐标，px
     * */
    private var mHotPointX : Float = 0f

    /**
     * 水波纹产生的热点y轴坐标，px
     * */
    private var mHotPointY : Float = 0f

    /**
     * 设置水波纹的产生点中心位置
     * - 默认在限制范围（视图）的中心位置
     * @param x 水波纹起始点x轴，px
     * @param y 水波纹起始点y轴，px
     * */
    fun setHotsPot(x : Float,y : Float){
        mHotPointX = x
        mHotPointY = y
    }

    /**
     * 构建水波纹图层Drawable
     * - 如果当前API小于21，则直接返回原始Drawable
     * */
    @SuppressLint("ObsoleteSdkInt")
    @Throws(NullPointerException::class)
    override fun build(): Drawable {
        item?: throw NullPointerException("RippleDrawable item must no null")
        mColorStateList ?: throw NullPointerException("Ripple color must no null")
        val newRadius = radius

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            return item!!
        }

        return RippleDrawable(mColorStateList!!,item,mask).apply {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && newRadius > 0){
                this.radius = newRadius
            }
            if (mHotPointX > 0 && mHotPointY > 0){
                this.setHotspot(mHotPointX,mHotPointY)
            }
        }
    }

}
