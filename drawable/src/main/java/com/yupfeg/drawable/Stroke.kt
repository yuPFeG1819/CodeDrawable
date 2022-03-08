package com.yupfeg.drawable

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt

/**
 * 构建图形drawable的描边配置, 需要配合`shapeDrawable`方法使用
 *
 * 使用kotlin-dsl方式：
 * ``` kotlin
 * shapeStroke{
 *      width = 1f
 *      color = ...
 * }
 * ```
 *
 * 等效于：
 * ``` xml
 * <shape ...>
 *     <stroke android:width="1dp" android:color="..."/>
 * </shape>
 *
 * ```
 * @param config dsl参数配置
 * */
@Suppress("unused")
inline fun shapeStroke(config : ShapeStrokeConfig.()->Unit) : ShapeStrokeConfig {
    return ShapeStrokeConfig().apply(config)
}

/**
 * 图形描边的配置类
 * @author yuPFeG
 * @date 2022/03/06
 */
@Suppress("unused")
class ShapeStrokeConfig {
    /**描边宽度，单位dp*/
    var width : Float = 0f

    /**
     * 虚线的长度，单位dp
     * */
    var dashWidth : Float = 0f

    /**
     * 虚线间的间隔距离，单位dp
     * */
    var dashGap : Float = 0f

    /**
     * 描边的颜色
     * - 支持[ColorInt]的单个颜色值
     * - 仅在API 21以上支持[ColorStateList]的颜色状态集
     * */
    var color : Any
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            when(value){
                is Int -> strokeColor = value
                is ColorStateList -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        colorStateList = value
                    }else{
                        throw IllegalArgumentException(
                            "color state list support after API 21 LOLLIPOP"
                        )
                    }
                }
                else -> throw IllegalArgumentException(
                    "stroke color property only support color int and ColorStateList"
                )
            }
        }

    /**
     * 描边的单色
     * */
    internal var strokeColor : Int = Color.TRANSPARENT
        set(value) {
            field = value
            colorStateList = ColorStateList.valueOf(value)
        }

    /**
     * 描边的颜色状态集
     * - 如果使用单色，则直接赋值[color]即可
     * */
    internal var colorStateList : ColorStateList? = null

}
