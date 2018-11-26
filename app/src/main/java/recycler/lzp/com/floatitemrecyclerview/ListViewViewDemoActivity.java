package recycler.lzp.com.floatitemrecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzp.floatitem.OnFloatViewShowListener;
import com.lzp.floatitem.list.FloatItemListView;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by li.zhipeng on 2018/10/10.
 * <p>
 * FloatItemListView演示demo
 */
public class ListViewViewDemoActivity extends AppCompatActivity
        implements FloatItemListView.ListViewFloatShowHook<ListView>,
        OnFloatViewShowListener {

    private FloatItemListView<ListView> floatItemListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        PtrFrameLayout mPtrFrame = findViewById(R.id.ptr_frame);
        mPtrFrame.setHeaderView(new PtrClassicDefaultHeader(this));
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, floatItemListView.getListView(), header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 2000);
            }
        });

        floatItemListView = findViewById(R.id.recycler_view);
        floatItemListView.setFloatViewShowHook(this);
        floatItemListView.setFloatView(getLayoutInflater().inflate(R.layout.float_view, (ViewGroup) getWindow().getDecorView(), false));
        floatItemListView.setOnFloatViewShowListener(this);

        floatItemListView.getListView().addHeaderView(getLayoutInflater().inflate(R.layout.header, floatItemListView.getListView(), false));
        floatItemListView.setAdapter(new MyAdapter());
    }

    @Override
    public boolean needShowFloatView(View child, int position) {
        if (position < floatItemListView.getListView().getHeaderViewsCount()) {
            return false;
        }
        return child.getTop() >= 0
                && child.getBottom() < ScreenUtils.getScreenHeight(this);
    }

    @Override
    public ListView initFloatItemListView() {
        return new ListView(this);
    }

    @Override
    public void onShowFloatView(View floatView, View child, int position) {
        Toast.makeText(this, "显示FloatView" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHideFloatView(View floatView, View child) {
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

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ListViewViewDemoActivity.this).inflate(R.layout.item_view, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText("item" + position);
            return convertView;
        }
    }

}
