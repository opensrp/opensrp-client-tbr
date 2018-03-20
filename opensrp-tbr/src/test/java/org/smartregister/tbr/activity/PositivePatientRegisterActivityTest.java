package org.smartregister.tbr.activity;

import android.content.Intent;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.tbr.BaseUnitTest;

import static junit.framework.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created by ndegwamartin on 20/03/2018.
 */

public class PositivePatientRegisterActivityTest extends BaseUnitTest {
    private ActivityController<PositivePatientRegisterActivity> controller;
    private PositivePatientRegisterActivity activity;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        Intent intent = new Intent(RuntimeEnvironment.application, PositivePatientRegisterActivity.class);
        controller = Robolectric.buildActivity(PositivePatientRegisterActivity.class, intent);
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
    public void activitySetUpCorrectlyWithNoException() {

        PositivePatientRegisterActivity spyActivity = spy(activity);
        assertNotNull(spyActivity);
    }
}
