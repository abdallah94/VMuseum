package com.exalt.vmuseum.utilities.interfaces;

import android.view.View;

/**
 * Created by Abdallah on 7/3/2016.
 */
public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
