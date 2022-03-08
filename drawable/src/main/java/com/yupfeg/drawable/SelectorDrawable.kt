package com.yupfeg.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.StateListDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.math.abs

/**
 * 构建新的状态集图像，在一个图像中包含多个状态的图像
 *
 * 可以参考kotlin-dsl方式使用：
 * ``` kotlin
 * selectorDrawable{
 *      normal = ... //表示默认状态显示图像
 *      pressed = ... //表示在视图按下时显示该图像
 * }
 * ```
 *
 * 等效于：
 * ``` xml
 * <selector>
 *     <item android:drawable="..." android:state_pressed="true" />
 *     <item android:drawable="..." />
 * </selector>
 * ```
 * @param build kotlin-dsl配置图像参数
 * */
@Suppress("unused")
fun selectorDrawable(build : StateListDrawableBuilder.()->Unit) : StateListDrawable{
    return selectorDrawableBuilder(build).build()
}

/**
 * 创建状态集图像的构建类
 * - 如果需要直接创建[Drawable]，则直接使用[selectorDrawable]方法
 * @param build kotlin-dsl配置图像参数
 * */
fun selectorDrawableBuilder(build: StateListDrawableBuilder.() -> Unit) : StateListDrawableBuilder {
    return StateListDrawableBuilder().apply(build)
}

/**
 * 状态集图像的构建类
 * - 由一个图形图像包含多个状态的图形图像
 * @author yuPFeG
 * @date 2022/03/06
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class StateListDrawableBuilder internal constructor() : StateListBuilder<Drawable>,
    DrawableBuilder {

    internal var drawableList = mutableListOf<DrawableStateListItem>()

    /**
     * 是否在RTL（从右到左）布局下，自动镜像图像
     * - 仅在API 19以上生效
     * */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    var isAutoMirrored : Boolean = false

    /**
     * 进入新状态图形时淡入的时间，单位ms ， 默认为0
     * */
    var enterFadeDuration : Int = 0

    /**
     * 离开旧状态图像时淡入的时间，单位ms，默认为0
     * */
    var exitFadeDuration : Int = 0

    override var normal: Drawable
        //禁用外部调用getter
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem { drawable = value }
        }
    override var pressed: Drawable
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                pressed = true
                drawable = value
            }
        }
    override var enable: Drawable
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                enable = true
                drawable = value
            }
        }
    override var focused: Drawable
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                focused = true
                drawable = value
            }
        }
    override var selected: Drawable
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                pressed = true
                drawable = value
            }
        }
    override var checked: Drawable
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                checked = true
                drawable = value
            }
        }
    override var checkable: Drawable
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                checkable = true
                drawable = value
            }
        }

    /**
     * 添加状态item项的图像
     * @param config kotlin-dsl配置item项图像
     * */
    @Throws(NullPointerException::class)
    fun addItem(config : DrawableStateListItem.()->Unit){
        val item = DrawableStateListItem().apply(config)
        addItem(item)
    }

    /**
     * 添加状态item项的图像
     * @param item
     * */
    @Throws(NullPointerException::class)
    fun addItem(item : DrawableStateListItem){
        item.drawable?: throw NullPointerException("stateListDrawable item drawable is null")
        drawableList.add(item)
    }

    /**
     * 以当前配置，构建新的[StateListDrawable]
     * */
    override fun build() : StateListDrawable {
        val stateListDrawable = StateListDrawable()
        //遍历添加所有状态集对应的图像
        var defItemDrawable : Drawable? = null
        for (drawableItem in drawableList) {
            //如果没有设置状态值，则表示为默认状态，
            if (drawableItem.states.isEmpty()) {
                defItemDrawable = drawableItem.drawable
                continue
            }
            stateListDrawable.addState(drawableItem.states.toIntArray(),drawableItem.drawable)
        }

        //默认状态的值必须设置在最后，否则会导致其他状态Drawable显示异常
        defItemDrawable?.also {
            stateListDrawable.addState(IntArray(0),it)
        }

        (stateListDrawable.constantState as? DrawableContainer.DrawableContainerState)?.also {
            it.enterFadeDuration = enterFadeDuration
            it.exitFadeDuration = exitFadeDuration
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            stateListDrawable.isAutoMirrored = isAutoMirrored
        }
        return stateListDrawable
    }

}

/**
 * 图像状态集的item配置类
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class DrawableStateListItem : StateListItem {
    var drawable : Drawable? = null
    /**图形对应的状态集*/
    internal val states = mutableListOf<Int>()

    override var pressed : Boolean = false
        set(value) {
            addState(AttrStates.StatePressed,value)
        }
    override var enable: Boolean = false
        set(value) {
            addState(AttrStates.StateEnable,value)
        }
    override var focused: Boolean = false
        set(value) {
            field = value
            addState(AttrStates.StateFocused,value)
        }
    override var selected: Boolean = false
        set(value) {
            addState(AttrStates.StateSelected,value)
        }
    override var checked: Boolean = false
        set(value) {
            addState(AttrStates.StateChecked,value)
        }
    override var checkable: Boolean = false
        set(value) {
            addState(AttrStates.StateCheckable,value)
        }

    /**
     * 添加图像的匹配状态
     * - 如果已添加相同状态，会直接替换掉原有状态的值，在同一个item状态中，不可能出现有相同的匹配状态
     * @param state 当前图像的匹配状态，如`android:state_pressed`使用`StatePressed`表示
     * @param value 状态的开启关闭
     * */
    fun addState(state : AttrStates, value : Boolean){
        val stateValue = if (value) state.attrsId else -state.attrsId
        val absValue = abs(stateValue)
        if (states.contains(absValue)){
            states.remove(absValue)
        }
        states.add(stateValue)
    }
}
