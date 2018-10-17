package com.lzp.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by li.zhipeng on 2018/10/10.
 * <p>
 * 列表播放视频的自定义RecyclerView
 */
public class VideoPlayRecyclerView extends FrameLayout {

    /**
     * 要悬浮的View
     */
    private View floatView;

    /**
     * recyclerView
     */
    private RecyclerView recyclerView;

    /**
     * 当前的滑动状态
     */
    private int currentState = -1;

    private View firstChild = null;

    /**
     * 悬浮View的显示状态监听器
     */
    private OnFloatViewShowListener onFloatViewShowListener;

    /**
     * 控制每一个item是否要显示floatView
     */
    private FloatViewShowHook floatViewShowHook;

    private LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

    public VideoPlayRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public VideoPlayRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addRecyclerView();
    }

    public void setFloatView(View floatView) {
        this.floatView = floatView;
        addView(this.floatView);
    }

    private void addRecyclerView() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(layoutManager);
        addView(recyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        RecyclerView.OnScrollListener myScrollerListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (floatView == null) {
                    return;
                }
                currentState = newState;
                switch (newState) {
                    // 停止滑动
                    case 0:
                        updateFloatScrollStopTranslateY();
                        break;
                    // 开始滑动
                    case 1:
                        // 保存第一个child
                        getFirstChild();
                        updateFloatScrollStartTranslateY();
                        showFloatView();
                        break;
                    // Fling
                    // 这里有一个bug，如果手指在屏幕上快速滑动，但是手指并未离开，仍然有可能触发Fling
                    // 所以这里不对Fling状态进行处理
//                    case 2:
//                        hideFloatView();
//                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (floatView == null) {
                    return;
                }
                switch (currentState) {
                    // 停止滑动
                    case 0:
                        updateFloatScrollStopTranslateY();
                        break;
                    // 开始滑动
                    case 1:
                        // Fling
                    case 2:
                        updateFloatScrollStartTranslateY();
                        break;
                }
            }
        };
        recyclerView.addOnScrollListener(myScrollerListener);

        // 监听item的移除情况
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (view == firstChild) {
                    firstChild = null;
                    hideFloatView();
                    // 回调监听器
                    if (onFloatViewShowListener != null) {
                        onFloatViewShowListener.onHideFloatView();
                    }
                }
            }
        });
    }

    private void getFirstChild() {
        if (firstChild != null) {
            return;
        }
        int childPos = calculateShowFloatViewPosition();
        if (childPos != -1) {
            firstChild = recyclerView.getChildAt(childPos);
        }
    }

    /**
     * 计算需要显示floatView的位置
     */
    private int calculateShowFloatViewPosition() {
        // 如果没有设置floatViewShowHook，默认返回第一个Child
        if (floatViewShowHook == null) {
            return 0;
        }

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            if (floatViewShowHook.needShowFloatView(child, firstVisiblePosition + i)) {
                return i;
            }
        }
        // -1 表示没有需要显示floatView的item
        return -1;
    }

    private void showFloatView() {
        if (firstChild != null) {
            floatView.setVisibility(View.VISIBLE);
        }
    }

    private void hideFloatView() {
        if (firstChild != null) {
            floatView.setVisibility(View.GONE);
        }
    }

    private void updateFloatScrollStartTranslateY() {
        if (firstChild != null) {
            int translateY = firstChild.getTop();
            floatView.setTranslationY(translateY);
        }
    }

    private void updateFloatScrollStopTranslateY() {
        if (firstChild == null) {
            getFirstChild();
            // 回调显示状态的监听器
            // 回调监听器
            if (firstChild != null && onFloatViewShowListener != null) {
                onFloatViewShowListener.onShowFloatView();
            }
        }
        updateFloatScrollStartTranslateY();
        showFloatView();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public void setOnFloatViewShowListener(OnFloatViewShowListener onFloatViewShowListener) {
        this.onFloatViewShowListener = onFloatViewShowListener;
    }

    public void setFloatViewShowHook(FloatViewShowHook floatViewShowHook) {
        this.floatViewShowHook = floatViewShowHook;
    }

    /**
     * 显示状态的回调监听器
     */
    public interface OnFloatViewShowListener {

        void onShowFloatView();

        void onHideFloatView();

    }

    /**
     * 根据item设置是否显示浮动的View
     */
    public interface FloatViewShowHook {

        /**
         * 当前item是否要显示floatView
         *
         * @param child    itemView
         * @param position 在列表中的位置
         */
        boolean needShowFloatView(View child, int position);

    }


}
