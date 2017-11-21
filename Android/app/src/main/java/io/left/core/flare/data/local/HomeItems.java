package io.left.core.flare.data.local;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.mikhaellopez.circularimageview.CircularImageView;

import io.left.core.flare.R;
import io.left.core.utils.lib.Glider;

/**
 * Created by MIMO on 11/18/2017.
 */

public class HomeItems extends BaseObservable {

    private String imagePath, itemName, itemPrice;
    private boolean isFavourite;
    private int itemCount, itemId, itemDrawable;

    @Bindable
    public String getImagePath() {
        return imagePath;
    }

    public HomeItems setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    @Bindable
    public String getItemName() {
        return itemName;
    }

    public HomeItems setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    @Bindable
    public String getItemPrice() {
        return itemPrice;
    }

    public HomeItems setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
        return this;
    }

    @Bindable
    public boolean isFavourite() {
        return isFavourite;
    }

    public HomeItems setFavourite(boolean favourite) {
        isFavourite = favourite;
        return this;
    }

    @Bindable
    public int getItemCount() {
        return itemCount;
    }

    public HomeItems setItemCount(int itemCount) {
        this.itemCount = itemCount;
        return this;
    }

    @Bindable
    public int getItemId() {
        return itemId;
    }

    public HomeItems setItemId(int itemId) {
        this.itemId = itemId;
        return this;
    }

    @Bindable
    public int getItemDrawable() {
        return itemDrawable;
    }

    public HomeItems setItemDrawable(int itemDrawable) {
        this.itemDrawable = itemDrawable;
        return this;
    }

    @BindingAdapter({"android:src"})
    public static void setImageRes(ImageView imageView, HomeItems homeItems) {
        imageView.setImageResource(homeItems.getItemDrawable());
    }
}
