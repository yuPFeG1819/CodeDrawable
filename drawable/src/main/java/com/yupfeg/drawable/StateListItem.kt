package com.yupfeg.drawable

/**
 * 状态集item项的通用方法抽象接口
 * @author yuPFeG
 * @date 2022/03/07
 */
interface StateListItem {

    /**
     * 快捷设置`android:state_pressed`的状态
     * - 如果同时设置多个属性，表示同时满足这些条件才会匹配对应的值
     * */
    var pressed : Boolean
    /**
     * 快捷设置`android:state_enabled`的状态
     * - 如果同时设置多个属性，表示同时满足这些条件才会匹配对应的值
     * */
    var enable : Boolean

    /**
     * 快捷设置`android:state_focused`的状态
     * - 如果同时设置多个属性，表示同时满足这些条件才会匹配对应的值
     * */
    var focused : Boolean

    /**
     * 快捷设置`android:state_selected`的状态
     * - 如果同时设置多个属性，表示同时满足这些条件才会匹配对应的值
     * */
    var selected : Boolean

    /**
     * 快捷设置`android:state_checked`的状态
     * */
    var checked : Boolean

    /**
     * 快捷设置`android:state_checkable`的状态
     * */
    var checkable : Boolean
}

/**
 * 状态集构造类的抽象接口
 * */
interface StateListBuilder<T>{

    /**
     * 快捷设置默认状态下，显示的图像或颜色
     * */
    var normal : T

    /**
     * 快捷设置`android:state_pressed = "true"`时对应的图像或颜色
     * */
    var pressed : T
    /**
     * 快捷设置`android:state_enabled`时对应的图像或颜色
     * - 如果同时设置多个属性，表示同时满足这些条件才会匹配对应的值
     * */
    var enable : T

    /**
     * 快捷设置`android:state_focused`时对应的图像或颜色
     * - 如果同时设置多个属性，表示同时满足这些条件才会匹配对应的值
     * */
    var focused : T

    /**
     * 快捷设置`android:state_selected`时对应的图像或颜色
     * - 如果同时设置多个属性，表示同时满足这些条件才会匹配对应的值
     * */
    var selected : T

    /**
     * 快捷设置`android:state_checked`时对应的图像或颜色
     * */
    var checked : T

    /**
     * 快捷设置`android:state_checkable`时对应的图像或颜色
     * */
    var checkable : T
}