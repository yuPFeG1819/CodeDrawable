package com.yupfeg.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 构建新的旋转Drawable，需要配合`layerDrawable`方法使用
 * - 仅在API 21以上才能正常工作
 *
 * kotlin-dsl使用方式：
 * ``` kotlin
 * rotateDrawable{
 *      drawable = ...
 *      fromDegrees = 0f
 *      toDegrees = 360f
 *      pivot(0.5f,0.5f)
 * }
 * ```
 *
 * 等效于：
 * <rotate>
 *     <item>
 *         <rotate
 *              android:drawable="..."
 *
 *              />
 *     </item>
 * </rotate>
 *
 * @param build kotlin-dsl配置参数
 * */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun rotateDrawable(build : RotateDrawableBuilder.()->Unit) : Drawable{
    return RotateDrawableBuilder().apply(build).build()
}

/**
 * 可旋转的Drawable构建类，等价于xml的<rotate>标签构建
 * - 仅在API 21以上，才支持设置drawable
 * @author yuPFeG
 * @date 2022/03/07
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class RotateDrawableBuilder internal constructor() : DrawableBuilder{

    /**需要旋转的drawable*/
    var drawable : Drawable? = null

    /**
     * 旋转动画的起始角度，默认为0
     * */
    var fromDegrees : Float = 0f

    /**
     * 旋转动画的结束角度，默认为360,旋转一圈
     * */
    var toDegrees : Float = 360f

    /**旋转中心点X轴，默认为图形的中心点*/
    private var mPivotX : Float = 0.5f

    /**旋转中心点Y，默认在图形的中心点*/
    private var mPivotY : Float = 0.5f

    /**
     * 设置旋转中心点位置
     * @param x 默认为0.5
     * @param y 默认为0.5
     * */
    fun pivot(x : Float,y : Float){
        mPivotX = x
        mPivotY = y
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(NullPointerException::class)
    override fun build(): Drawable {
        drawable?: throw NullPointerException("rotate drawable must no null")
        val newDrawable = RotateDrawable().also {
            it.drawable = drawable
            it.fromDegrees = fromDegrees
            it.toDegrees = toDegrees
            it.pivotX = mPivotX
            it.pivotY = mPivotY
        }
        return newDrawable
    }

}
