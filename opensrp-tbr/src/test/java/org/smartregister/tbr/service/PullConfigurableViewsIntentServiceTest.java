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

    private Context context = RuntimeEnvironment.application;

    @Mock
    private HTTPAgent httpAgent;

    @Spy
    private
    SharedPreferences sharedPreferences = context
            .getSharedPreferences(TbrApplication.class.getName(), Context.MODE_PRIVATE);
    @Spy
    private ECSyncHelper syncHelper = ECSyncHelper.getInstance(context);

    @Mock
    private TbrApplication tbrApplication;

    private PullConfigurableViewsServiceHelper helper;

    private String loginandMainJSON = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}," +
            "{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566670940,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"en\",\"applicationName\":\"TB Rich\",\"enableJsonViews\":false}}]";


    private String mainConfigJSon = "[{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566670940,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"en\",\"applicationName\":\"TB Rich\",\"enableJsonViews\":false}}]";

    private String loginJson = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}]";

    @Before
    public void start() {
        when(tbrApplication.getApplicationContext()).thenReturn(context);
        when(tbrApplication.getContext()).thenReturn(org.smartregister.Context.getInstance());
        when(tbrApplication.getConfigurableViewsRepository()).thenReturn(configurableViewsRepository);
        helper = new PullConfigurableViewsServiceHelper(tbrApplication,
                httpAgent, syncHelper, sharedPreferences);
    }

    @After
    public void stop() {
        helper = null;
    }

    @Test
    public void testDontSavewhenAPIReturnsNothing() throws Exception {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString()))
                .thenReturn(new Response(ResponseStatus.success, "[]"));
        helper.processIntent();
        verify(configurableViewsRepository, never()).saveConfigurableViews(any(JSONArray.class));
        assertEquals(sharedPreferences.getString(VIEW_CONFIGURATION_PREFIX + LOGIN, ""), "");
    }

    @Test
    public void testLoginConfigurationSavedToPreferences() throws Exception {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString())).
                thenReturn(new Response(ResponseStatus.success, loginJson));
        helper.processIntent();
        verify(configurableViewsRepository, never()).saveConfigurableViews(any(JSONArray.class));
        verify(syncHelper, never()).updateLastViewsSyncTimeStamp(anyLong());
        assertEquals(new JSONObject(sharedPreferences.getString(VIEW_CONFIGURATION_PREFIX + LOGIN,
                "")).toString(), new JSONArray(loginJson).get(0).toString());
    }

    @Test
    public void testMainConfigNotSavedWhenDatabaseNotCreated() throws Exception {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString())).
                thenReturn(new Response(ResponseStatus.success, loginandMainJSON));
        helper.processIntent();
        verify(configurableViewsRepository, never()).saveConfigurableViews(any(JSONArray.class));
        verify(syncHelper, never()).updateLastViewsSyncTimeStamp(anyLong());
        assertEquals(new JSONObject(sharedPreferences.getString(VIEW_CONFIGURATION_PREFIX + LOGIN,
                "")).toString(), new JSONArray(loginandMainJSON).get(0).toString());
    }

    @Test
    public void testMainConfigSavedWhenDatabaseExists() throws Exception {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString())).
                thenReturn(new Response(ResponseStatus.success, loginandMainJSON));
        when(tbrApplication.getPassword()).thenReturn("Sample_Pass");
        helper = new PullConfigurableViewsServiceHelper(tbrApplication,
                httpAgent, syncHelper, sharedPreferences);
        helper.processIntent();
        verify(configurableViewsRepository).saveConfigurableViews(any(JSONArray.class));
        verify(syncHelper).updateLastViewsSyncTimeStamp(anyLong());
        assertEquals(new JSONObject(sharedPreferences.getString(VIEW_CONFIGURATION_PREFIX + LOGIN,
                "")).toString(), new JSONArray(loginandMainJSON).get(0).toString());
    }

    @Test
    public void testOnlyMainConfigSavedIfLoginNotUpdated() throws Exception {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString())).
                thenReturn(new Response(ResponseStatus.success, mainConfigJSon));
        when(tbrApplication.getPassword()).thenReturn("Sample_Pass");
        helper = new PullConfigurableViewsServiceHelper(tbrApplication,
                httpAgent, syncHelper, sharedPreferences);
        helper.processIntent();
        verify(configurableViewsRepository).saveConfigurableViews(any(JSONArray.class));
        verify(syncHelper).updateLastViewsSyncTimeStamp(anyLong());
        verify(sharedPreferences, never()).edit();
        assertEquals(sharedPreferences.getString(VIEW_CONFIGURATION_PREFIX + LOGIN, ""), "");
    }


    @Test
    public void testNoExceptionRaised() {
        when(httpAgent.fetchWithCredentials(anyString(), anyString(), anyString()))
                .thenReturn(new Response(ResponseStatus.success, loginJson));
        try {
            helper.processIntent();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
