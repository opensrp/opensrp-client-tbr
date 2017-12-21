package org.smartregister.tbr.activity;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.tbr.BaseUnitTest;

/**
 * Created by ndegwamartin on 18/12/2017.
 */

public class LoginActivityTest extends BaseUnitTest {

    private ActivityController<LoginActivity> controller;
    private LoginActivity activity;

    @Mock
    private Menu menu;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        Intent intent = new Intent(RuntimeEnvironment.application, LoginActivity.class);
        controller = Robolectric.buildActivity(LoginActivity.class, intent);
        activity = controller.get();
        controller.setup();
    }

    private void destroyController() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void onCreateOptionsMenuAddsItemToActionBarIfPresent() {
        junit.framework.Assert.assertTrue(activity.onCreateOptionsMenu(menu));
    }
}
