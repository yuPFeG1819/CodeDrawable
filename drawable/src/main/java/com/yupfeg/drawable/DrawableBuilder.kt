package com.yupfeg.drawable

import android.graphics.drawable.Drawable

/**
 * 替代xml构建drawable构建类的抽象接口
 * @author yuPFeG
 * @date 2022/03/07
 */
interface DrawableBuilder {
    /**
     * 构建新的drawable
     * */
    fun build() : Drawable
}