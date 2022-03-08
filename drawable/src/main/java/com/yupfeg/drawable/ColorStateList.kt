package com.yupfeg.drawable

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.abs

/**
 * 构建颜色状态集对象，在一个颜色属性内包含多种状态的颜色显示
 *
 * kotlin-dsl方式使用
 * ``` kotlin
 * selectorColor{
 *     normal = ... //默认状态下显示颜色
 *     pressed = ... //用户按下时显示颜色
 * }
 * ```
 *
 * 等效于
 * ``` xml
 * <selector>
 *     <item color="..." android:state_pressed="true" />
 *     <item color="..."/>
 * </selector>
 * ```
 * @param config dsl参数配置
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
fun selectorColor(config: ColorStateListBuilder.() -> Unit) : ColorStateList{
    return selectorColorBuilder(config).build()
}

/**
 * 创建颜色状态集的构建类实例
 * - 如果需要直接构建[ColorStateList],则使用[selectorColor]方法
 * @param config kotlin-dsl配置颜色状态集
 * */
fun selectorColorBuilder(config: ColorStateListBuilder.() -> Unit) : ColorStateListBuilder {
    return ColorStateListBuilder().apply(config)
}

/**
 * 颜色状态集合构造类
 * 等效于
 * ``` xml
 * <selector>
 *     <item color="..." ... />
 *     <item color="..." ... />
 * </selector>
 * ```
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ColorStateListBuilder internal constructor() : StateListBuilder<Int> {
    private var mSelectorItems = mutableListOf<ColorStateListItem>()
    /**
     * 最大的item项颜色包含的状态数量，避免构建二级数组赋值时，出现数组越界问题
     * */
    private var mMaxItemAttrStateSize : Int = 0

    override var normal: Int
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem { color = value }
        }
    override var pressed: Int
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                pressed = true
                color = value
            }
        }
    override var enable: Int
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                enable = true
                color = value
            }
        }
    override var focused: Int
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                focused = true
                color = value
            }
        }
    override var selected: Int
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                pressed = true
                color = value
            }
        }
    override var checked: Int
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                checked = true
                color = value
            }
        }
    override var checkable: Int
        @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
        get() = error("")
        set(value) {
            addItem {
                checkable = true
                color = value
            }
        }

    /**
     * 添加颜色选择器的item
     * - item项的状态必须设置至少一个才能使状态生效
     * @param config dsl方式进行item配置
     * */
    fun addItem(config : ColorStateListItem.()->Unit){
        addItem(ColorStateListItem().apply(config))
    }

    /**
     * 添加颜色选择器的item
     * @param itemConfig 配置类
     * */
    fun addItem(itemConfig: ColorStateListItem){
        if (itemConfig.states.isEmpty()) {
            itemConfig.states.add(0) //如果没有设置状态值，则表示为默认状态
        }
        mSelectorItems.add(itemConfig)
        //更新到item项状态的最大数量
        mMaxItemAttrStateSize = mMaxItemAttrStateSize.coerceAtLeast(itemConfig.states.size)
    }

    /**构建颜色状态集合*/
    fun build() : ColorStateList{
        val stateNum = mSelectorItems.size
        if (stateNum <= 0) throw NullPointerException("color selector item is empty")
        val colors = IntArray(stateNum)
        //构建保存颜色索引对应状态的二维数组
        val stateArray = Array(stateNum){
            IntArray(mMaxItemAttrStateSize){ 0 }
        }

        //遍历选择器item集合，添加颜色值，及与其对应的每个状态值
        mSelectorItems.forEachIndexed { index, itemConfig ->
            colors[index] = itemConfig.color
            itemConfig.states.forEachIndexed{stateIndex,stateValue->
                stateArray[index][stateIndex] = stateValue
            }
        }
        return ColorStateList(stateArray,colors)
    }

}

/**
 * 颜色状态集的item项配置
 * 类比到xml文件，可以做到等效于
 * ``` xml
 * <item color="..." android:state_pressed="true">
 * ```
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ColorStateListItem : StateListItem {
    /**状态集颜色*/
    @ColorInt
    var color : Int = Color.TRANSPARENT

    /**颜色匹配的状态集*/
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
     * 添加颜色匹配状态
     * - 如果已添加相同状态，会直接替换掉原有状态，在同一个item状态中，不可能出现有相同的匹配状态
     * @param state 当前颜色的匹配状态，如`android:state_pressed`使用`StatePressed`表示
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


