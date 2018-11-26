package com.lzp.floatitem;


import android.view.View;

/**
 * 显示状态的回调监听器
 */
public interface OnFloatViewShowListener {

    /**
     * FloatView被显示
     *
     * @param floatView 浮层
     * @param child     显示浮层的child
     * @param position  child在RecyclerView中的位置
     */
    void onShowFloatView(View floatView, View child, int position);

    /**
     * FloatView被隐藏
     */
    void onHideFloatView(View floatView, View child);

    /**
     * FloatView被移动
     */
    void onScrollFloatView(View floatView);

    /**
     * FloatView被处于Fling状态
     */
    void onScrollFlingFloatView(View floatView);

    /**
     * FloatView由滚动变为静止状态
     */
    void onScrollStopFloatView(View floatView);

}
