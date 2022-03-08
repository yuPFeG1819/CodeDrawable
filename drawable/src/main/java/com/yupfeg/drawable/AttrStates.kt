package com.yupfeg.drawable

/**
 * 颜色或图形的状态规格封装对象，包含常用状态规格属性
 * @param attrsId 系统定义attr标签id
 * @author yuPFeG
 * @date 2022/03/06
 */
@Suppress("unused")
sealed class AttrStates(val attrsId : Int) {
    /**
     * 等效于`android:state_checkable`
     * */
    object StateCheckable : AttrStates(android.R.attr.state_checkable)
    /**
     * 等效于`android:state_checked`
     * */
    object StateChecked : AttrStates(android.R.attr.state_checked)
    /**
     * 视图是否处于开启状态
     * 等效于`android:state_enabled`
     * */
    object StateEnable : AttrStates(android.R.attr.state_enabled)
    /**
     * 视图是否获取到焦点
     * 等效于`android:state_focused`
     * */
    object StateFocused : AttrStates(android.R.attr.state_focused)
    /**
     * 视图是否按下
     * 等效于`android:state_pressed`
     * */
    object StatePressed : AttrStates(android.R.attr.state_pressed)

    /**
     * 视图是否选中
     * 等效于`android:state_selected`
     * */
    object StateSelected : AttrStates(android.R.attr.state_selected)

    /**
     * 视图是否按下
     * 等效于`android:state_window_focused`
     * */
    object StateWindowFocused : AttrStates(android.R.attr.state_window_focused)

    /**
     * 当前视图或父视图，是否已活跃（激活），
     * 等效于`android:state_activated`
     * */
    object StateActivated : AttrStates(android.R.attr.state_activated)

    /**
     * 当前视图被视为活跃状态，
     * 等效于`android:state_active`
     * */
    object StateActive : AttrStates(android.R.attr.state_active)
}