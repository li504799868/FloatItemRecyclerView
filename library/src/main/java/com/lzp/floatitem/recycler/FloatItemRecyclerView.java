package com.lzp.floatitem.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lzp.floatitem.OnFloatViewShowListener;


/**
 * Created by li.zhipeng on 2018/10/10.
 * <p>
 * RecyclerView包装类
 */
public class FloatItemRecyclerView<V extends RecyclerView> extends FrameLayout {

    /**
     * 要悬浮的View
     */
    private View floatView;

    /**
     * recyclerView
     */
    private V recyclerView;

    /**
     * 当前的滑动状态
     */
    private int currentState = -1;

    private View needFloatChild = null;

    /**
     * 悬浮View的显示状态监听器
     */
    private OnFloatViewShowListener onFloatViewShowListener;

    /**
     * 控制每一个item是否要显示floatView
     */
    private RecyclerViewFloatShowHook<V> floatViewShowHook;

    public FloatItemRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public FloatItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置悬浮的View
     */
    public void setFloatView(View floatView) {
        this.floatView = floatView;
        if (floatView.getLayoutParams() == null) {
            floatView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        addView(this.floatView);
        this.floatView.setVisibility(View.GONE);
    }

    /**
     * 必须设置FloatViewShowHook，完成View的初始化操作
     */
    public void setFloatViewShowHook(RecyclerViewFloatShowHook<V> floatViewShowHook) {
        this.floatViewShowHook = floatViewShowHook;
        recyclerView = floatViewShowHook.initFloatItemRecyclerView();
        addRecyclerView();
        // 移动到前台
        if (floatView != null) {
            bringChildToFront(floatView);
            updateViewLayout(floatView, floatView.getLayoutParams());
        }
    }


    @SuppressWarnings("unchecked")
    private void addRecyclerView() {
        addView(recyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // 设置滚动监听
        initOnScrollListener();
        // 设置布局监听，当adapter数据发生改变的时候，需要做一些处理
        initOnLayoutChangedListener();
        // 监听recyclerView的item滚动情况，判断正在悬浮item是否已经移出了屏幕
        initOnChildAttachStateChangeListener();

    }

    private void initOnScrollListener() {
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
                    case RecyclerView.SCROLL_STATE_IDLE:
                        // 对正在显示的浮层的child做个副本，为了判断显示浮层的child是否发现了变化
                        View tempFloatChild = needFloatChild;
                        // 更新浮层的位置，覆盖child
                        updateFloatScrollStopTranslateY();
                        // 如果firstChild没有发生变化，回调floatView滑动停止的监听
                        if (tempFloatChild == needFloatChild) {
                            if (onFloatViewShowListener != null) {
                                onFloatViewShowListener.onScrollStopFloatView(floatView);
                            }
                        }
                        break;
                    // 开始滑动
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        // 保存第一个child
                        // 更新浮层的位置
                        updateFloatScrollStartTranslateY();
                        break;
                    // Fling
                    // 这里有一个bug，如果手指在屏幕上快速滑动，但是手指并未离开，仍然有可能触发Fling
                    // 所以这里不对Fling状态进行处理
//                    case RecyclerView.SCROLL_STATE_SETTLING:
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
                    case RecyclerView.SCROLL_STATE_IDLE:
                        updateFloatScrollStopTranslateY();
                        break;
                    // 开始滑动
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        updateFloatScrollStartTranslateY();
                        break;
                    // Fling
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        updateFloatScrollStartTranslateY();
                        if (onFloatViewShowListener != null) {
                            onFloatViewShowListener.onScrollFlingFloatView(floatView);
                        }

                        break;
                }
            }
        };
        recyclerView.addOnScrollListener(myScrollerListener);
    }

    private void initOnChildAttachStateChangeListener() {
        // 监听item的移除情况
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                // 判断child是否被移除
                // 请注意：回调onChildViewDetachedFromWindow时并没有真正移除这个child
                // 所以这里增加一个判断：floatChildInScreen是否正在被adapter使用，防止浮层闪烁
                if (view == needFloatChild && floatChildInScreen()) {
                    clearFloatChild();
                }
            }
        });
    }

    private void initOnLayoutChangedListener() {
        // 设置OnLayoutChangeListener监听，会在设置adapter和adapter.notifyXXX的时候回调
        // 所以我们要这里做一些处理
        recyclerView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (recyclerView.getAdapter() == null) {
                    return;
                }
                if (floatView == null) {
                    return;
                }
                // 数据已经刷新，找到需要显示悬浮的Item
                clearFloatChild();
                // 找到第一个child
                getFirstChild();
                updateFloatScrollStartTranslateY();
                showFloatView();
            }
        });
    }

    /**
     * 手动计算应该播放视频的child
     */
    public void findChildToPlay() {
        if (needFloatChild == null) {
            updateFloatScrollStopTranslateY();
            // 回调显示状态的监听器
            if (onFloatViewShowListener != null) {
                onFloatViewShowListener.onShowFloatView(floatView,
                        recyclerView.getChildAdapterPosition(needFloatChild));
            }
            return;
        }
        // 获取fistChild在列表中的位置
        int position = recyclerView.getChildAdapterPosition(needFloatChild);
        // 判断是否允许播放
        if (floatViewShowHook.needShowFloatView(needFloatChild, position)) {
            updateFloatScrollStartTranslateY();
            showFloatView();
            // 回调显示状态的监听器
            if (onFloatViewShowListener != null) {
                onFloatViewShowListener.onShowFloatView(floatView,
                        recyclerView.getChildAdapterPosition(needFloatChild));
            }
        } else {
            // 回调隐藏状态的监听器
            if (onFloatViewShowListener != null) {
                onFloatViewShowListener.onHideFloatView(floatView);
            }
        }
    }

    /**
     * 判断item是否正在显示内容
     */
    private boolean floatChildInScreen() {
        return recyclerView.getChildAdapterPosition(needFloatChild) != -1;
    }

    /**
     * 找到第一个要显示悬浮item的
     */
    private void getFirstChild() {
        if (needFloatChild != null) {
            return;
        }
        int childPos = calculateShowFloatViewPosition();
        if (childPos != -1) {
            needFloatChild = recyclerView.getChildAt(childPos);
            // 回调显示状态的监听器
            if (onFloatViewShowListener != null) {
                onFloatViewShowListener.onShowFloatView(floatView,
                        recyclerView.getChildAdapterPosition(needFloatChild));
            }
        }
    }

    /**
     * 计算需要显示floatView的位置
     *
     * @return 如果找到RecyclerView中对应的child，返回child的位置，否则发挥-1，表示没有要显示浮层的child
     */
    private int calculateShowFloatViewPosition() {
        // 如果没有设置floatViewShowHook，默认返回第一个Child
        if (floatViewShowHook == null) {
            return -1;
        }
        int firstVisiblePosition;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else {
            throw new IllegalArgumentException("only support LinearLayoutManager!!!");
        }
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            // 判断这个child是否需要显示
            if (child != null && floatViewShowHook.needShowFloatView(child, firstVisiblePosition + i)) {
                return i;
            }
        }
        // -1 表示没有需要显示floatView的item
        return -1;
    }

    private void showFloatView() {
        if (needFloatChild != null) {
            floatView.post(new Runnable() {
                @Override
                public void run() {
                    floatView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void hideFloatView() {
        if (needFloatChild != null) {
            floatView.setVisibility(View.GONE);
        }
    }

    private void updateFloatScrollStartTranslateY() {
        if (needFloatChild != null) {
            int translateY = needFloatChild.getTop();
            floatView.setTranslationY(translateY);
            if (onFloatViewShowListener != null) {
                onFloatViewShowListener.onScrollFloatView(floatView);
            }
        }
    }

    private void updateFloatScrollStopTranslateY() {
        if (needFloatChild == null) {
            getFirstChild();
        }
        updateFloatScrollStartTranslateY();
        showFloatView();
    }

    public V getRecyclerView() {
        return recyclerView;
    }

    /**
     * 清除floatView依赖的item，并隐藏floatView
     */
    public void clearFloatChild() {
        if (floatView.getVisibility() == View.VISIBLE) {
            hideFloatView();
            // 回调监听器
            if (onFloatViewShowListener != null) {
                onFloatViewShowListener.onHideFloatView(floatView);
            }
        }
        needFloatChild = null;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public void setOnFloatViewShowListener(OnFloatViewShowListener onFloatViewShowListener) {
        this.onFloatViewShowListener = onFloatViewShowListener;
    }

    /**
     * 根据item设置是否显示浮动的View
     */
    public interface RecyclerViewFloatShowHook<V extends RecyclerView> {

        /**
         * 当前item是否要显示floatView
         *
         * @param child    itemView
         * @param position 在列表中的位置
         */
        boolean needShowFloatView(View child, int position);

        V initFloatItemRecyclerView();

    }


}
