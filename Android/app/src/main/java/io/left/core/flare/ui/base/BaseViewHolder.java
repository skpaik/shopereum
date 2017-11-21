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
package io.left.core.flare.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void bind(T item);
}
