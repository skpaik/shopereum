package io.left.core.flare.ui.homepage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.left.core.flare.data.local.HomeItems;
import io.left.core.flare.databinding.ItemProductBinding;
import io.left.core.flare.ui.base.BaseAdapter;
import io.left.core.flare.ui.base.BasePresenter;
import io.left.core.flare.ui.base.BaseViewHolder;

/**
 * Created by MIMO on 11/18/2017.
 */

public class HomeAdapter extends BaseAdapter<HomeItems> {

    private int layoutId;
    private HomePresenter mHomePresenter;

    public HomeAdapter(int layoutId, HomePresenter invitePresenter) {
        this.layoutId = layoutId;
        mHomePresenter = invitePresenter;
    }

    @Override
    public boolean isEqual(HomeItems left, HomeItems right) {
        return left.getItemId() == right.getItemId();
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding itemContactsBinding = ItemProductBinding.inflate(layoutInflater, parent, false);
        return new HomeItemViewHolder(itemContactsBinding);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return layoutId;
    }

    @Override
    protected HomeItems getObjForPosition(int position) {
        return getItem(position);
    }

    private class HomeItemViewHolder extends BaseViewHolder<HomeItems> {

        ItemProductBinding itemProductBinding;

        HomeItemViewHolder(ItemProductBinding itemProductBinding) {
            super(itemProductBinding.getRoot());
            this.itemProductBinding = itemProductBinding;
        }

        @Override
        public void bind(HomeItems homeItems) {

            itemProductBinding.setProduct(homeItems);
            itemProductBinding.setHomePresenter(mHomePresenter);
            itemProductBinding.textProductAmmount.setText(homeItems.getItemCount() + " available");
            itemProductBinding.executePendingBindings();
        }
    }
}
