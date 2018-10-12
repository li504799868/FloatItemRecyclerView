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
                currentState = newState;
                switch (newState) {
                    // 停止滑动
                    case 0:
                        updateFloatScrollStopTranslateY();
                        break;
                    // 开始滑动
                    case 1:
                        // 保存第一个child
                        firstChild = getFirstChild();
                        floatView.setVisibility(View.VISIBLE);
                        break;
                    // Fling
                    case 2:
                        floatView.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (floatView == null) {
                    return;
                }
                switch (currentState) {
                    case -1:
//                        View child = recyclerView.getChildAt(0);
//                        child.setTag(R.id.float_tag, tag);
                        break;
                    // 停止滑动
                    case 0:
                        updateFloatScrollStopTranslateY();
                        break;
                    // 开始滑动
                    case 1:
                        updateFloatScrollStartTranslateY();
                        break;
                    // Fling
                    // 这里有一个bug，如果手指在屏幕上快速滑动，但是手指并未离开，仍然有可能触发Fling
                    // 所以这里不对Fling状态进行处理
//                    case 2:
//                        floatView.setVisibility(View.GONE);
//                        break;
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
                    floatView.setVisibility(View.GONE);
                }
            }
        });
    }

    private View getFirstChild() {
        if (firstChild != null) {
            return firstChild;
        }
        View child = recyclerView.getChildAt(0);
        int translateY = child.getTop();
        if (translateY < -floatView.getHeight() / 2) {
            child = recyclerView.getChildAt(1);
        }
        return child;
    }

    private void updateFloatScrollStartTranslateY() {
        if (firstChild != null) {
            int translateY = firstChild.getTop();
            floatView.setTranslationY(translateY);
        }
    }

    private void updateFloatScrollStopTranslateY() {
        if (firstChild == null) {
            firstChild = getFirstChild();
        }
        int translateY = firstChild.getTop();
        floatView.setTranslationY(translateY);
        floatView.setVisibility(View.VISIBLE);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

}
