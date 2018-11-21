package recycler.lzp.com.floatitemrecyclerview.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import recycler.lzp.com.floatitemrecyclerview.R;

/**
 * Created by li.zhipeng on 2018/11/21.
 * <p>
 * 管理浮层的Controller
 */
public class FloatViewController {

    private static FloatViewController instance;

    public synchronized static FloatViewController getInstance() {
        if (instance == null) {
            instance = new FloatViewController();
        }
        return instance;
    }

    private View floatView;

    private FloatViewController() {

    }

    public View getFloatView() {
        if (floatView == null) {
            return null;
        }
        // 如果floatView已经被其他列表添加了，先移除，防止报错
        if (floatView.getParent() != null) {
            ((ViewGroup) floatView.getParent()).removeView(floatView);
        }
        return floatView;
    }

    public void setFloatView(View floatView) {
        this.floatView = floatView;
    }
}
