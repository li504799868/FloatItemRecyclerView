package recycler.lzp.com.videoplayrecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lzp.recycler.FloatItemRecyclerView;

/**
 * Created by li.zhipeng on 2018/10/10.
 */
public class MainActivity extends Activity
        implements FloatItemRecyclerView.FloatViewShowHook<RecyclerView>,
        FloatItemRecyclerView.OnFloatViewShowListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatItemRecyclerView<RecyclerView> recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setFloatView(getLayoutInflater().inflate(R.layout.float_view, (ViewGroup) getWindow().getDecorView(), false));
        recyclerView.setFloatViewShowHook(this);
        recyclerView.setOnFloatViewShowListener(this);
        recyclerView.setAdapter(new MyAdapter());
    }

    @Override
    public boolean needShowFloatView(View child, int position) {
        return child.getTop() > 0
                && child.getBottom() < ScreenUtils.getScreenHeight(this);
    }

    @Override
    public RecyclerView initVideoPlayRecyclerView() {
        return new RecyclerView(this);
    }

    @Override
    public void onShowFloatView(View floatView, int position) {
        Toast.makeText(this, "显示FloatView", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHideFloatView(View floatView) {
        Toast.makeText(this, "隐藏FloatView", Toast.LENGTH_SHORT).show();
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
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_view, viewGroup, false));
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
