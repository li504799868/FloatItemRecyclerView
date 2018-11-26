# FloatItemRecyclerView and #FloatItemListView
实现RecyclerView和ListView的item添加悬浮层的效果

效果图：</br>
<img src="https://img-blog.csdnimg.cn/20181115111726932.gif"/>

比较常见的使用场景：</br>
<img src="https://img-blog.csdnimg.cn/20181115114931803.gif"/>

<h1>使用步骤</h1>

1、实现FloatViewShowHook接口

    public interface FloatViewShowHook<V extends RecyclerView> {

        /**
         * 当前item是否要显示floatView
         *
         * @param child    itemView
         * @param position 在列表中的位置
         */
        boolean needShowFloatView(View child, int position);

        V initVideoPlayRecyclerView();

    }

2、通过设置OnFloatViewShowListener，可以监听悬浮层的显示状态和滑动状态

    public interface OnFloatViewShowListener {

        /**
         * FloatView被显示
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
    
3.设置Hook和Listener：
    
    FloatItemRecyclerView<RecyclerView> recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setFloatViewShowHook(this);
    recyclerView.setFloatView(getLayoutInflater().inflate(R.layout.float_view, (ViewGroup) getWindow().getDecorView(), false));
    recyclerView.setOnFloatViewShowListener(this);
    recyclerView.setAdapter(new MyAdapter());
    
    FloatItemListView floatItemListView = findViewById(R.id.recycler_view);
    floatItemListView.setFloatViewShowHook(this);
    floatItemListView.setFloatView(getLayoutInflater().inflate(R.layout.float_view, (ViewGroup) getWindow().getDecorView(), false));
    floatItemListView.setOnFloatViewShowListener(this);
    floatItemListView.setAdapter(new MyAdapter());
    
## FloatItemListView扩展滑动监听：

    public void addOnScrollListener(AbsListView.OnScrollListener listener) {
        if (!onScrollListeners.contains(listener)) {
            onScrollListeners.add(listener);
        }
    }

    public void removeOnScrollListener(AbsListView.OnScrollListener listener) {
        onScrollListeners.remove(listener);
    }

    public void clearOnScrollListener(AbsListView.OnScrollListener listener) {
        onScrollListeners.clear();
    }
    
如果您觉得不错，感谢打赏一个猪蹄：

<img width=400 height=400 src="https://camo.githubusercontent.com/9a9587578e25bb3bc917c25cd772ab3ae554e4c7/68747470733a2f2f696d672d626c6f672e6373646e2e6e65742f323031383036313931383539343333343f77617465726d61726b2f322f746578742f6148523063484d364c7939696247396e4c6d4e7a5a473475626d56304c3355774d54457a4d5455354e6a413d2f666f6e742f3561364c354c32542f666f6e7473697a652f3430302f66696c6c2f49304a42516b46434d413d3d2f646973736f6c76652f3730"/>

如果在使用过程中遇到问题或者有更好的建议，欢迎发送邮件到：</br>

lzp-541@163.com

    

