package org.smartregister.tbr.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.mocks.HomeActivityTestVersion;

/**
 * Created by ndegwamartin on 17/10/2017.
 */

public class HomeActivityTest extends BaseUnitTest {


    private ActivityController<HomeActivityTestVersion> controller;
    private Activity activity;
    private Context context;

    @Before
    public void setUp() {

        Intent intent = new Intent(RuntimeEnvironment.application, HomeActivity.class);
        intent.putExtra("location_name", "Nairobi");
        controller = Robolectric.buildActivity(HomeActivityTestVersion.class, intent);
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

        System.gc();
    }

    @Test
    public void homeActivityRendersCorrectUsernameInitialsOnCreate() {
        TextView textView = (TextView) activity.findViewById(R.id.custom_toolbar_logo_text);
        textView.getText();
    }

}
