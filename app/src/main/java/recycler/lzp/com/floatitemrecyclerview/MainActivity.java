package recycler.lzp.com.floatitemrecyclerview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import recycler.lzp.com.floatitemrecyclerview.viewpager.ViewPagerDemoActivity;

/**
 * Created by li.zhipeng on 2018/10/10.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.recycler_view).setOnClickListener(this);
        findViewById(R.id.list_view).setOnClickListener(this);
        findViewById(R.id.viewpager).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recycler_view:
                startActivity(new Intent(this, RecyclerViewDemoActivity.class));
                break;
            case R.id.list_view:
                startActivity(new Intent(this, ListViewViewDemoActivity.class));
                break;
            case R.id.viewpager:
                startActivity(new Intent(this, ViewPagerDemoActivity.class));
                break;
        }
    }

}
