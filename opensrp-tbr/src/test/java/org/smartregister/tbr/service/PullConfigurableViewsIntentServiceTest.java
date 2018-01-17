package org.smartregister.tbr.service;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.tbr.sync.ECSyncHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.tbr.util.Constants.CONFIGURATION.LOGIN;
import static util.TbrConstants.VIEW_CONFIGURATION_PREFIX;

/**
 * Created by samuelgithengi on 10/27/17.
 */

public class PullConfigurableViewsIntentServiceTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ConfigurableViewsRepository configurableViewsRepository;

    @Mock
    private Context context;

    @Mock
    private HTTPAgent httpAgent;

    private
    SharedPreferences sharedPreferences = RuntimeEnvironment.application
            .getSharedPreferences(TbrApplication.class.getName(), Context.MODE_PRIVATE);


    @Spy
    private ECSyncHelper syncHelper = ECSyncHelper.getInstance(RuntimeEnvironment.application);

    @Spy
    private TbrApplication tbrApplication = TbrApplication.getInstance();

    private PullConfigurableViewsServiceHelper helper;

    private String expectedJsonFromAPI = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}]";

    private String loginJsonFromAPI = "{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}";

    @Before
    public void start() {
        helper = new PullConfigurableViewsServiceHelper(context, configurableViewsRepository,
                httpAgent, "", syncHelper, sharedPreferences);
    }

    @After
    public void stop() {
        helper = null;
    }

    @Test
    public void testDontSavewhenAPIReturnsNothing() throws Exception {
        expectedJsonFromAPI = "[]";
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString()))
                .thenReturn(new Response(ResponseStatus.success, expectedJsonFromAPI));
        helper.processIntent();
        verify(configurableViewsRepository, never()).saveConfigurableViews(any(JSONArray.class));
    }

    @Test
    public void testLoginConfigurationSavedToPreferences() throws Exception {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString())).
                thenReturn(new Response(ResponseStatus.success, expectedJsonFromAPI));
        helper.processIntent();
        verify(configurableViewsRepository, never()).saveConfigurableViews(any(JSONArray.class));
        verify(syncHelper, never()).updateLastViewsSyncTimeStamp(anyLong());
        assertEquals(new JSONObject(sharedPreferences.getString(VIEW_CONFIGURATION_PREFIX + LOGIN,
                "")).toString(), new JSONObject(loginJsonFromAPI).toString());
    }


    @Test
    public void testConfigurableViewsOnException() {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString()))
                .thenReturn(new Response(ResponseStatus.success, expectedJsonFromAPI));
        when(tbrApplication.getPassword()).thenReturn(null);
        doNothing().when(syncHelper).updateLastSyncTimeStamp(anyLong());
        try {
            helper.processIntent();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
