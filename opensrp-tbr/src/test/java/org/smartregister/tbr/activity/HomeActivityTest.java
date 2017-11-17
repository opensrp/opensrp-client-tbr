package org.smartregister.tbr.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.smartregister.Context;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.R;
import org.smartregister.tbr.event.BaseEvent;
import org.smartregister.tbr.event.LanguageConfigurationEvent;
import org.smartregister.tbr.event.TriggerViewConfigurationSyncEvent;
import org.smartregister.tbr.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.tbr.mock.HomeActivityTestVersion;
import org.smartregister.tbr.shadow.RegisterFragmentShadow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created by ndegwamartin on 17/10/2017.
 */
@Config(shadows = {RegisterFragmentShadow.class})
public class HomeActivityTest extends BaseUnitTest {

    private ActivityController<HomeActivityTestVersion> controller;
    private HomeActivityTestVersion activity;

    @Mock
    private LanguageConfigurationEvent languageConfigurationEvent;
    @Mock
    private ViewConfigurationSyncCompleteEvent viewConfigurationSyncCompleteEvent;
    @Mock
    Context context;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        Intent intent = new Intent(RuntimeEnvironment.application, HomeActivityTestVersion.class);
        controller = Robolectric.buildActivity(HomeActivityTestVersion.class, intent);
        activity = controller.get();
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

    @Test
    public void refreshViewNotInvokedWhenRefreshViewFromConfigurationChangeCalledWithNullParameterSyncCompleteEvent() throws Exception {
        ViewConfigurationSyncCompleteEvent viewConfigurationSyncCompleteEvent = null;
        HomeActivityTestVersion spyActivity = spy(activity);
        spyActivity.refreshViewFromConfigurationChange(viewConfigurationSyncCompleteEvent);

        PowerMockito.verifyPrivate(spyActivity, times(0)).invoke("refreshView");
    }

    @Test
    public void refreshViewInvokedWhenRefreshViewFromConfigurationChangeCalledWithNonNullParameterSyncCompleteEvent() throws Exception {

        HomeActivityTestVersion spyActivity = spy(activity);

        PowerMockito.doNothing().when(spyActivity).refreshView();
        spyActivity.refreshViewFromConfigurationChange(viewConfigurationSyncCompleteEvent);

        PowerMockito.verifyPrivate(spyActivity, times(1)).invoke("refreshViewFromConfigurationChange", viewConfigurationSyncCompleteEvent);
    }

    @Test
    public void refreshViewNotInvokedWhenRefreshViewFromLanguageChangeCalledWithNullParameterSyncCompleteEvent() throws Exception {
        LanguageConfigurationEvent languageConfigurationEvent = null;
        HomeActivityTestVersion spyActivity = spy(activity);
        spyActivity.refreshViewFromLanguageChange(languageConfigurationEvent);

        PowerMockito.verifyPrivate(spyActivity, times(0)).invoke("refreshView");
    }

    @Test
    public void refreshViewInvokedWhenRefreshViewFromLanguageChangeCalledWithNonNullParameterSyncCompleteEvent() throws Exception {

        HomeActivityTestVersion spyActivity = spy(activity);

        PowerMockito.doNothing().when(spyActivity).refreshView();
        spyActivity.refreshViewFromLanguageChange(languageConfigurationEvent);

        PowerMockito.verifyPrivate(spyActivity, times(1)).invoke("refreshViewFromLanguageChange", languageConfigurationEvent);
    }

    @Test
    public void callingManualSyncTriggersViewConfigurationSyncEvent() throws Exception {


        HomeActivityTestVersion spyActivity = spy(activity);

        TriggerViewConfigurationSyncEvent triggerViewConfigurationSyncEvent = new TriggerViewConfigurationSyncEvent();
        triggerViewConfigurationSyncEvent.setManualSync(true);

        PowerMockito.doReturn(null).when(spyActivity);
        View view = spyActivity.findViewById(R.id.refreshSyncButton);
        spyActivity.manualSync(view);

        PowerMockito.verifyPrivate(spyActivity, times(1)).invoke("postEvent", any(BaseEvent.class));

    }

    @After
    public void validate() {
        validateMockitoUsage();
    }
}
