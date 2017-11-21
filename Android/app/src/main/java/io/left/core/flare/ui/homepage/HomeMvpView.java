
/*
*  ****************************************************************************
*  * Created by : Azizul Islam process 13-Oct-17 at 4:02 PM.
*  * Email : azizul@w3engineers.com
*  *
*  * Last edited by : Azizul Islam process 13-Oct-17.
*  *
*  * Last Reviewed by : <Reviewer Name> process <mm/dd/yy>
*  ****************************************************************************
*/
package io.left.core.flare.ui.homepage;

import java.util.List;

import io.left.core.flare.data.local.HomeItems;
import io.left.core.flare.ui.base.MvpView;

public interface HomeMvpView extends MvpView {

    void getAllItems(List<HomeItems> homeItems);

}