package org.smartregister.tbr.repository;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.tbr.application.TbrApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 10/27/17.
 */

@RunWith(AndroidJUnit4.class)
public class ConfigurableViewsRepositoryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ConfigurableViewsRepository configurableViewsRepository;

    @Spy
    TbrApplication tbrApplication = TbrApplication.getInstance();


    @Before
    public void setUp() throws Exception {
        configurableViewsRepository = TbrApplication.getInstance().getConfigurableViewsRepository();
        configurableViewsRepository.getWritableDatabase().delete(ConfigurableViewsRepository.TABLE_NAME, null, null);
        when(tbrApplication.getPassword()).thenReturn("Simple Pass");
    }

    @After
    public void tearDown() throws Exception {
        configurableViewsRepository.getWritableDatabase().close();
    }

    @Test
    @SmallTest
    public void testSaveViewConfiguration() throws Exception {
        String expectedJsonFromAPI = "{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566670940,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"en\",\"applicationName\":\"TB Rich\",\"enableJsonViews\":false}}";
        JSONArray jsonArray = new JSONArray(expectedJsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("main"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArray);
        assertEquals(1515566670940l, lastSyncTimeStamp);
        assertTrue(configurableViewsRepository.configurableViewExists("main"));
    }

    @Test
    @SmallTest
    public void testGetConfigurableViewJson() throws Exception {
        String jsonFromAPI = "{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566670940,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"en\",\"applicationName\":\"TB Rich\",\"enableJsonViews\":false}}";
        JSONArray jsonArrayFromAPI = new JSONArray(jsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("main"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArrayFromAPI);
        String jsonFromDB = configurableViewsRepository.getConfigurableViewJson("main");
        assertEquals(1515566670940l, lastSyncTimeStamp);
        assertEquals(jsonArrayFromAPI.get(0).toString(), new JSONObject(jsonFromDB).toString());
    }

    @Test
    @SmallTest
    public void testUpdateViewConfiguration() throws Exception {
        String jsonFromAPI = "{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566670940,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"en\",\"applicationName\":\"TB Rich\",\"enableJsonViews\":false}}";
        JSONArray jsonArrayFromAPI = new JSONArray(jsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("main"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArrayFromAPI);
        assertEquals(1515566670940l, lastSyncTimeStamp);
        assertTrue(configurableViewsRepository.configurableViewExists("main"));
        String updatedJsonFromAPI = "{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566698723,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"fr\",\"applicationName\":\"TB Reach\",\"enableJsonViews\":true}}";
        JSONArray updatedJsonArrayFromAPI = new JSONArray(updatedJsonFromAPI);
        lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(updatedJsonArrayFromAPI);
        String jsonFromDB = configurableViewsRepository.getConfigurableViewJson("main");
        assertEquals(1515566698723l, lastSyncTimeStamp);
        assertEquals(updatedJsonArrayFromAPI.get(0).toString(), new JSONObject(jsonFromDB).toString());
    }

}
