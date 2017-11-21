package io.left.core.flare.ui.homepage;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.List;

import io.left.core.flare.R;
import io.left.core.flare.data.local.HomeItems;
import io.left.core.flare.databinding.ActivityMainBinding;
import io.left.core.flare.ui.base.BaseActivity;
import io.left.core.utils.helper.ViewUtils;

public class MainActivity extends BaseActivity<HomeMvpView, HomePresenter> implements HomeMvpView{

    private ActivityMainBinding activityMainBinding;

    ImageView imageView;
    int[] sliderImage = {R.drawable.sl1, R.drawable.sl2, R.drawable.sl3};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected void startUI() {
        super.startUI();

        initView();
        setTitle("Shopereum");
        presenter.loadAllData();
        imageSlider();
    }

    @Override
    protected HomePresenter initPresenter() {
        return new HomePresenter();
    }

    private void initView() {
        RecyclerView recyclerView = getHomeRecyclerView();
        recyclerView.setLayoutManager(ViewUtils.newGridLayoutManager(this, 2));
        recyclerView.setAdapter(new HomeAdapter(R.layout.item_product, presenter));
        imageView = findViewById(R.id.slider_image);
    }

    private RecyclerView getHomeRecyclerView() {
        return ViewUtils.getRecyclerView(this, R.id.recycler_view);
    }

    private HomeAdapter getHomeAdapter() {
        RecyclerView.Adapter adapter = ViewUtils.getAdapter(getHomeRecyclerView());
        if (adapter != null) {
            return (HomeAdapter) adapter;
        }
        return null;
    }

    @Override
    public void getAllItems(List<HomeItems> homeItems) {
        HomeAdapter homeAdapter = getHomeAdapter();

        if (homeAdapter != null) {
            homeAdapter.addItems(homeItems);
        }
    }

    int i = 0;
    Handler handler = new Handler(Looper.getMainLooper());
    private void imageSlider() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(R.drawable.sl1);
                i++;
                if (i == 3)
                    i = 0;

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        handler.post(runnable);
    }
}
