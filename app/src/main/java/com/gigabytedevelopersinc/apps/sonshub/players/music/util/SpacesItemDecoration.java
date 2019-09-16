package com.gigabytedevelopersinc.apps.sonshub.players.music.util;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 12 Feb, 2019
 * Time: 1:47 PM
 * Desc: SpacesItemDecoration
 **/

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
  private final int space;

  public SpacesItemDecoration(int space) {
    this.space = space;
  }

  @Override
  public void getItemOffsets(
      @NotNull Rect outRect,
      @NotNull View view,
      @NotNull RecyclerView parent,
      @NotNull RecyclerView.State state) {
    outRect.left = space;
    outRect.right = space;
    outRect.bottom = space;

    // Add top margin only for the first item to avoid double space between items
    //    if (parent.getChildLayoutPosition(view) == 0) {
    //        outRect.top = space;
    //    } else {
    //        outRect.top = 0;
    //    }
  }
}
