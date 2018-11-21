package recycler.lzp.com.floatitemrecyclerview.viewpager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lzp.floatitem.OnFloatViewShowListener;
import com.lzp.floatitem.recycler.FloatItemRecyclerView;

import recycler.lzp.com.floatitemrecyclerview.R;
import recycler.lzp.com.floatitemrecyclerview.ScreenUtils;

/**
 * Created by li.zhipeng on 2018/11/21.
 * <p>
 * 显示浮层的Fragment，内部使用FloatItemRecyclerView，用于ViewPager中
 */
public class RecyclerViewFragment extends BaseFragment implements FloatItemRecyclerView.RecyclerViewFloatShowHook<RecyclerView>,
        OnFloatViewShowListener {

    private FloatItemRecyclerView<RecyclerView> recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recycler;
    }

    @Override
    protected void onVisible() {
        recyclerView.setFloatView(FloatViewController.getInstance().getShowFloatView());
        recyclerView.findChildToPlay();
    }

    @Override
    protected void onInVisible() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setFloatViewShowHook(this);
        recyclerView.setOnFloatViewShowListener(this);
        // 如果创建Fragment的时候，已经对用户可见了，直接添加FloatView
        if (isVisible) {
            recyclerView.setFloatView(FloatViewController.getInstance().getShowFloatView());
        }
        recyclerView.setAdapter(new MyAdapter());
    }

    @Override
    public boolean needShowFloatView(View child, int position) {
        return child.getTop() >= 0
                && child.getBottom() < ScreenUtils.getScreenHeight(requireContext());
    }

    @Override
    public RecyclerView initFloatItemRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        return recyclerView;
    }

    @Override
    public void onShowFloatView(View floatView, int position) {
        Toast.makeText(requireContext(), "显示FloatView", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHideFloatView(View floatView) {
        Toast.makeText(requireContext(), "隐藏FloatView", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScrollFloatView(View floatView) {

    }

    @Override
    public void onScrollFlingFloatView(View floatView) {

    }

    @Override
    public void onScrollStopFloatView(View floatView) {

    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(requireContext()).inflate(R.layout.item_view, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
