package com.yupfeg.drawable

import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.RequiresApi

/**
 * 创建图层叠加的图像
 *
 * 使用kotlin-dsl方式：
 * ``` kotlin
 * layerDrawable{
 *     addItem{
 *        drawable = ...
 *     }
 *     addItem{
 *        drawable = ...
 *     }
 * }
 * ```
 *
 * 等效于：
 * ``` xml
 * <layer-list>
 *      <item>
 *          ...
 *      </item>
 *
 *      <item>
 *          ...
 *      </item>
 * </layer-list>
 * ```
 * [LayerDrawable的api使用指南](https://www.apiref.com/android-zh/android/graphics/drawable/LayerDrawable.html)
 * @param build kotlin-dsl的图层属性配置
 * */
@Suppress("unused")
fun layerDrawable(build : LayerDrawableBuilder.()->Unit) : LayerDrawable{
    return layerDrawableBuilder(build).build()
}

/**
 * 创建图层叠加图像构建类的实例
 * - 如果要直接构建图层叠加Drawable，则直接使用[layerDrawable]
 * @param build kotlin-dsl的图层属性配置
 * */
fun layerDrawableBuilder(build: LayerDrawableBuilder.() -> Unit) : LayerDrawableBuilder {
    return LayerDrawableBuilder().apply(build)
}

/**
 * 图层叠加图像的构建类
 * @author yuPFeG
 * @date 2022/03/06
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class LayerDrawableBuilder internal constructor() : DrawableBuilder {

    private val mChildDrawables = mutableListOf<LayerDrawableItemConfig>()

    /**
     * 图层的不透明度，默认为半透明
     * - 用于系统启动绘制优化
     * - 取值范围：[PixelFormat.OPAQUE]-不透明，[PixelFormat.TRANSPARENT]-透明，
     * [PixelFormat.TRANSLUCENT]-半透明
     * */
    var opacity : Int = PixelFormat.TRANSLUCENT

    /**
     * 是否在RTL（从右到左）布局下，自动镜像图像
     * */
    var isAutoMirrored : Boolean = false

    /**
     * 填充模式，取值为[LayerDrawable.PADDING_MODE_NEST]和[LayerDrawable.PADDING_MODE_STACK]
     * - 仅在API 21以上生效
     * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var layerPaddingMode : Int = LayerDrawable.PADDING_MODE_NEST

    /**
     * 图层左侧的绝对内边距，单位dp，如果设置-1，则根据[layerPaddingMode]值进行计算
     * - 仅在API 23以上生效
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    var paddingLeft : Int = 0
    /**
     * 图层顶侧的绝对内边距，单位dp，如果设置-1，则根据[layerPaddingMode]值进行计算
     * - 仅在API 23以上生效
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    var paddingTop : Int = 0
    /**
     * 图层右侧的绝对内边距，单位dp，如果设置-1，则根据[layerPaddingMode]值进行计算
     * - 仅在API 23以上生效
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    var paddingRight : Int = 0
    /**
     * 图层底侧的绝对内边距，单位dp，如果设置-1，则根据[layerPaddingMode]值进行计算
     * - 仅在API 23以上生效
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    var paddingBottom : Int = 0

    /**
     * 添加图层上层的图像
     * @param itemConfig 子图形的配置
     * */
    fun addItem(itemConfig : LayerDrawableItemConfig.()->Unit){
        addItem(LayerDrawableItemConfig().apply(itemConfig))
    }

    /**
     * 添加图层上层的图像
     * @param itemConfig 子图形的配置
     * */
    fun addItem(itemConfig : LayerDrawableItemConfig){
        mChildDrawables.add(itemConfig)
    }

    override fun build() : LayerDrawable{
        val drawables = Array(mChildDrawables.size){ index->
            val childDrawable = mChildDrawables[index].drawable
            childDrawable?: throw NullPointerException("LayerDrawableItem drawable is null")
            childDrawable
        }
        return LayerDrawable(drawables).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                this.paddingMode = layerPaddingMode
            }

            //遍历更新没有item项图像
            for (index in mChildDrawables.indices){
                val config = mChildDrawables[index]
                if (config.id != LayerDrawableItemConfig.DEF_ID){
                    setId(index,config.id)
                }
                //设置子图像的偏移量
                val left = getChildDrawableOffsetPx(config.left)
                val top = getChildDrawableOffsetPx(config.top)
                val right = getChildDrawableOffsetPx(config.right)
                val bottom = getChildDrawableOffsetPx(config.bottom)
                setLayerInset(index,left,top,right,bottom)
            }
        }
    }

    private fun getChildDrawableOffsetPx(origin : Int) : Int{
        if (origin <= 0) return 0

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,origin.toFloat(),Resources.getSystem().displayMetrics
        ).toInt()
    }
}

class LayerDrawableItemConfig{

    companion object{
        internal const val DEF_ID = -1
    }

    /**
     * 图层的标记id，
     * - 在原生视图的背景图层内，会需要指定特殊id，比如原生`ProgressBar`设置多层进度背景
     * */
    var id : Int = DEF_ID

    var drawable : Drawable? = null

    /**当前item图像在左侧的偏移量，单位dp ，默认为0*/
    var left : Int = 0

    /**当前item图像在顶部的偏移量，单位dp，默认为0*/
    var top : Int = 0

    /**当前item图像在右侧的偏移量，单位dp，默认为0*/
    var right : Int = 0

    /**当前item图像在底部的偏移量，单位dp，默认为0*/
    var bottom : Int = 0
}