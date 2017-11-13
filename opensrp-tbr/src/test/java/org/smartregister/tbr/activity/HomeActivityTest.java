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
import org.robolectric.annotation.Config;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.R;
import org.smartregister.tbr.mock.HomeActivityTestVersion;
import org.smartregister.tbr.shadow.RegisterFragmentShadow;

/**
 * Created by ndegwamartin on 17/10/2017.
 */
@Config(shadows = {RegisterFragmentShadow.class})
public class HomeActivityTest extends BaseUnitTest {


    private ActivityController<HomeActivityTestVersion> controller;
    private Activity activity;

    @Before
    public void setUp() {
        Intent intent = new Intent(RuntimeEnvironment.application, HomeActivityTestVersion.class);
        controller = Robolectric.buildActivity(HomeActivityTestVersion.class, intent);
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
