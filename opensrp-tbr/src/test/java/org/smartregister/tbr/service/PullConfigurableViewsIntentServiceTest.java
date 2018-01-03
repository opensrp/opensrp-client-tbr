package org.smartregister.tbr.service;

import android.content.Context;

import org.json.JSONArray;
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
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.tbr.sync.ECSyncHelper;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Spy
    private ECSyncHelper syncHelper = ECSyncHelper.getInstance(RuntimeEnvironment.application);

    private PullConfigurableViewsServiceHelper helper;

    private String expectedJsonFromAPI = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}]";


    @Before
    public void start() {
        helper = new PullConfigurableViewsServiceHelper(context, configurableViewsRepository, httpAgent, "", syncHelper);
    }

    @After
    public void stop() {
        helper = null;
    }

    @Test
    public void testFetchConfigurableViewDontSavewhenAPIReturnsNothing() throws Exception {
        expectedJsonFromAPI = "[]";
        when(httpAgent.fetch(anyString())).thenReturn(new Response(ResponseStatus.success, expectedJsonFromAPI));
        helper.processIntent();
        verify(configurableViewsRepository, never()).saveConfigurableViews(any(JSONArray.class));
    }

    @Test
    public void testConfigurableViewsSavesWhenAPIReturnsViews() throws Exception {
        when(httpAgent.fetch(anyString())).thenReturn(new Response(ResponseStatus.success, expectedJsonFromAPI));
        doNothing().when(syncHelper).updateLastSyncTimeStamp(anyLong());
        helper.processIntent();
        verify(configurableViewsRepository).saveConfigurableViews(any(JSONArray.class));
    }


    @Test
    public void testConfigurableViewsOnException() {
        when(httpAgent.fetch(anyString())).thenReturn(new Response(ResponseStatus.success, expectedJsonFromAPI));
        doNothing().when(syncHelper).updateLastSyncTimeStamp(anyLong());
        try {
            helper.processIntent();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
