package com.yupfeg.drawable

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt

/**
 * 构建渐变颜色属性配置，需要配合`shapeDrawable`方法使用
 * 使用kotlin-dsl方式：
 * ``` kotlin
 * gradientColor{
 *      startColor = ...
 *      endColor = ...
 * }
 * ```
 *
 * 等效于：
 * ``` xml
 * <shape ...>
 *     <gradient
 *          android:startColor="..."
 *          android:endColor="..."
 *          />
 * </shape>
 * ```
 * @param config dsl参数配置
 * */
@Suppress("unused")
fun gradientColor(config : GradientColorConfig.()->Unit) : GradientColorConfig {
    return GradientColorConfig().apply(config)
}

/**
 * 渐变颜色属性的配置类
 * @author yuPFeG
 * @date 2022/03/06
 */
@Suppress("unused")
class GradientColorConfig internal constructor(){

    /**
     * 渐变起始颜色
     * */
    @ColorInt
    var startColor : Int = Color.TRANSPARENT

    /**
     * 可选的渐变的中间颜色
     * - 只在线性渐变时生效
     * */
    @ColorInt
    var centerColor : Int = Color.TRANSPARENT

    /**
     * 渐变的结束颜色
     * */
    @ColorInt
    var endColor : Int = Color.TRANSPARENT

    /**
     * 圆点在图形的x轴位置，0-1，
     * -默认为0.5f，圆点在图形的中心
     * - 对于线性渐变，控制[centerColor]
     * */
    var centerX : Float = 0.5f
        set(value) {
            if (value > 1) field = 1f
            else if (value < 0) field = 0f
            field = value
        }

    /**
     * 圆点在图形的x轴位置，0-1，
     * -默认为0.5f，圆点在图形的中心
     * */
    var centerY : Float = 0.5f
        set(value) {
            if (value > 1) field = 1f
            else if (value < 0) field = 0f
            field = value
        }

    /**渐变类型*/
    var type : GradientColorType = GradientColorType.Linear

    /**
     * 渐变的方向，默认为从左到右
     * - 只对线性渐变[GradientColorType.Linear]生效
     * */
    var orientation : GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT

    /**
     * 渐变的半径
     * - 仅在径向渐变[GradientColorType.Radial]时生效
     * */
    var radius : Float = 0f

    /**
     * 渐变角度
     * 便于从xml文件转换过来的快捷属性，内部实际还是控制[orientation]属性
     * */
    var angle : Int = 0
        set(value) {
            field = value
            if (value < 0 ) return
            orientation = when(value){
                in (0 until 45) -> GradientDrawable.Orientation.LEFT_RIGHT
                in (45 until 90) -> GradientDrawable.Orientation.BL_TR
                in (90 until 135) -> GradientDrawable.Orientation.BOTTOM_TOP
                in (135 until 180) -> GradientDrawable.Orientation.BR_TL
                in (180 until 225) -> GradientDrawable.Orientation.RIGHT_LEFT
                in (225 until 270) -> GradientDrawable.Orientation.TR_BL
                in (270 until 315) -> GradientDrawable.Orientation.TOP_BOTTOM
                in (315 until 360) -> GradientDrawable.Orientation.TL_BR
                else -> GradientDrawable.Orientation.LEFT_RIGHT
            }
        }

}

/**渐变颜色类型*/
@Suppress("unused")
enum class GradientColorType{
    /**线性渐变*/
    Linear {
        override val value: Int
            get() =  GradientDrawable.LINEAR_GRADIENT
    },
    /**从中心点向外延伸的径向渐变*/
    Radial {
        override val value: Int
            get() = GradientDrawable.RADIAL_GRADIENT
    },
    /**从中心点沿逆时针扫描的渐变*/
    Sweep {
        override val value: Int
            get() = GradientDrawable.SWEEP_GRADIENT
    };

    abstract val value : Int
}