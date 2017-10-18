package org.smartregister.tbr.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.R;
import org.smartregister.tbr.shadows.MenuItemTestVersion;
import org.smartregister.tbr.util.Constants;

import java.util.ArrayList;

/**
 * Created by ndegwamartin on 17/10/2017.
 */
public class HomeActivityTest extends BaseUnitTest {


    private ActivityController<HomeActivity> controller;
    private Activity activity;
    private Context context;

    @Before
    public void setUp() {

        Intent intent = new Intent(RuntimeEnvironment.application, HomeActivity.class);
        intent.putExtra(Constants.INTENT_KEY.FULL_NAME, "Test Guy");
        controller = Robolectric.buildActivity(HomeActivity.class, intent);
        activity = controller.get();
        org.mockito.MockitoAnnotations.initMocks(this);
        context = RuntimeEnvironment.application;
        controller.setup();
    }

    @After
    public void tearDown() {
        destroyController();
    }

    private void destroyController() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }
    }

    @Test
    public void homeActivityRendersCorrectUsernameInitialsOnCreate() {
        TextView textView = (TextView) activity.findViewById(R.id.custom_toolbar_logo_text);
        junit.framework.Assert.assertEquals("TG", textView.getText());
    }

    @Test
    public void shouldDisplayOnOptionsItemsMenuOnCreate() {
        MenuItemTestVersion menuItem = new MenuItemTestVersion();
        menuItem.setItemId(R.id.action_logout);
        activity.onOptionsItemSelected(menuItem);
        ArrayList<View> outViews = new ArrayList<>();
        outViews.size();
    }
}
