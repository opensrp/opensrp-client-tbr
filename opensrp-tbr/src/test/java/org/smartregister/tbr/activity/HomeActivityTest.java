package org.smartregister.tbr.activity;

import android.app.Activity;
import android.content.Intent;
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

/**
 * Created by ndegwamartin on 17/10/2017.
 */
public class HomeActivityTest extends BaseUnitTest {


    private ActivityController<HomeActivity> controller;
    private Activity activity;

    @Before
    public void setUp() {
        Intent intent = new Intent(RuntimeEnvironment.application, HomeActivity.class);
        controller = Robolectric.buildActivity(HomeActivity.class, intent);
        activity = controller.get();
        org.mockito.MockitoAnnotations.initMocks(this);
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
        junit.framework.Assert.assertEquals("NM", textView.getText());
    }
}
