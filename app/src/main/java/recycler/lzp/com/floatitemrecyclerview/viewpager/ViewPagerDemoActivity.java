package recycler.lzp.com.floatitemrecyclerview.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.flyco.tablayout.SlidingTabLayout;

import recycler.lzp.com.floatitemrecyclerview.R;

/**
 * Created by li.zhipeng on 2018/10/10.
 *
 * ViewPager中使用FloatItemRecyclerView优化方案演示demo
 *
 */
public class ViewPagerDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return new RecyclerViewFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Fragment" + position;
            }
        });

        SlidingTabLayout tabLayout = findViewById(R.id.sliding_tab_layout);
        tabLayout.setViewPager(viewPager);

    }

}
