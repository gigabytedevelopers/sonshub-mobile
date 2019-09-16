package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;
import com.gigabytedevelopersinc.apps.sonshub.R;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 14 Feb, 2019
 * Time: 10:58 PM
 * Desc: ExpandedControlsActivity
 **/

public class ExpandedControlsActivity extends ExpandedControllerActivity {

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu_expanded_controller, menu);
    CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
    return true;
  }

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
  }
}
