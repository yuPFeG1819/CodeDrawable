package com.yupfeg.drawable

import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity

/**
 * 创建剪裁图像Drawable
 *
 * kotlin-dsl使用方式：
 * ``` kotlin
 * clipDrawable{
 *      drawable = shapeDrawable{
 *          ...
 *      }
 * }
 *
 * ```
 *
 * 等效于使用<clip>标签
 * ``` xml
 * <clip>
 *     <shape>...</shape>
 * </clip>
 * ```
 * [ClipDrawable api使用](https://www.apiref.com/android-zh/android/graphics/drawable/ClipDrawable.html)
 * @param build kotlin-dsl配置方法
 * */
@Suppress("unused")
fun clipDrawable(build : ClipDrawableBuilder.()->Unit) : ClipDrawable{
    return clipDrawableBuilder(build).build()
}

/**
 * 创建<clip>标签的剪裁图像构建类
 * - 如果需要直接构建Drawable，则直接使用[clipDrawable]方法
 * @param build kotlin-dsl配置方法
 * */
fun clipDrawableBuilder(build : ClipDrawableBuilder.()->Unit) : ClipDrawableBuilder {
    return ClipDrawableBuilder().apply(build)
}


/**
 * 动态设置<clip>标签的剪裁图像构建类
 * - 通常在设置原生`progressBar`的背景时使用，与<layer-list>标签配合使用
 * @author yuPFeG
 * @date 2022/03/06
 */
@Suppress("MemberVisibilityCanBePrivate","unused")
class ClipDrawableBuilder internal constructor() : DrawableBuilder {

    var drawable : Drawable? = null

    /**
     * 剪裁作用的位置，默认为起始位置
     * 取值来自于[Gravity]
     * */
    var gravity : Int = Gravity.START

    /**剪裁所用的方向，默认为水平方向*/
    var orientation : Int = ClipDrawable.HORIZONTAL

    override fun build() : ClipDrawable{
        drawable?:throw NullPointerException("ClipDrawable drawable is null")
        return ClipDrawable(drawable, gravity, orientation)
    }
}