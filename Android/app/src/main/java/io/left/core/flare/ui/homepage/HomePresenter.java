/*
*  ****************************************************************************
*  * Created by : Azizul Islam on 13-Oct-17 at 4:02 PM.
*  * Email : azizul@w3engineers.com
*  *
*  * Last edited by : Azizul Islam on 13-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
*  ****************************************************************************
*/

package io.left.core.flare.ui.homepage;

import java.util.ArrayList;
import java.util.List;

import io.left.core.flare.R;
import io.left.core.flare.data.local.HomeItems;
import io.left.core.flare.ui.base.BasePresenter;

public class HomePresenter extends BasePresenter<HomeMvpView> {

    int imageDrawable[] = {R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5};
    String name[] = {"Brown Wood Chair", "Wood Bed", "Steel Bed", "Blue Wood Chair", "Steel Corner Sofa"};
    String price[] = {"24", "25", "26", "27", "28"};
    int amount[] = {3, 4, 5, 6, 7};

    public HomePresenter() {

    }

    public void loadAllData() {
        List<HomeItems> homeItems = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            HomeItems homeItem = new HomeItems().setItemName(name[i]).setItemId(i)
                    .setItemDrawable(imageDrawable[i]).setItemCount(amount[i]).setItemPrice(price[i] + " $");

            homeItems.add(homeItem);
        }

        getMvpView().getAllItems(homeItems);
    }
}
