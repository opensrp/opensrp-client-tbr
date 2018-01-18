package org.smartregister.tbr.repository;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartregister.tbr.application.TbrApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by samuelgithengi on 10/27/17.
 */

@RunWith(AndroidJUnit4.class)
public class ConfigurableViewsRepositoryTest {

    private ConfigurableViewsRepository configurableViewsRepository;
    private String expectedJsonFromAPI;
    private TbrRepositoryShadow repositoryShadow = new TbrRepositoryShadow(TbrApplication.getInstance().getApplicationContext(), TbrApplication.getInstance().getContext());

    @Before
    public void setUp() throws Exception {
        configurableViewsRepository = new ConfigurableViewsRepository(repositoryShadow);
        configurableViewsRepository.getWritableDatabase().delete(ConfigurableViewsRepository.TABLE_NAME, null, null);
        expectedJsonFromAPI = "[{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566670940,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"en\",\"applicationName\":\"TB Rich\",\"enableJsonViews\":false}}]";

    }

    @After
    public void tearDown() throws Exception {
        configurableViewsRepository.getWritableDatabase().close();
    }

    @Test
    @SmallTest
    public void testSaveViewConfiguration() throws Exception {
        JSONArray jsonArray = new JSONArray(expectedJsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("main"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArray);
        assertEquals(1515566670940l, lastSyncTimeStamp);
        assertTrue(configurableViewsRepository.configurableViewExists("main"));
    }

    @Test
    @SmallTest
    public void testGetConfigurableViewJson() throws Exception {
        JSONArray jsonArrayFromAPI = new JSONArray(expectedJsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("main"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArrayFromAPI);
        String jsonFromDB = configurableViewsRepository.getConfigurableViewJson("main");
        assertEquals(1515566670940l, lastSyncTimeStamp);
        assertEquals(jsonArrayFromAPI.get(0).toString(), new JSONObject(jsonFromDB).toString());
    }

    @Test
    @SmallTest
    public void testUpdateViewConfiguration() throws Exception {
        JSONArray jsonArrayFromAPI = new JSONArray(expectedJsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("main"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArrayFromAPI);
        assertEquals(1515566670940l, lastSyncTimeStamp);
        assertTrue(configurableViewsRepository.configurableViewExists("main"));
        String updatedJsonFromAPI = "[{\"_id\":\"19a2e8aa6739d77a2b780199c6122867\",\"_rev\":\"54-79e394bd5040770ee002eedff1ba360b\",\"type\":\"ViewConfiguration\",\"serverVersion\":1515566670998,\"identifier\":\"main\",\"metadata\":{\"type\":\"Main\",\"language\":\"fr\",\"applicationName\":\"TB REACH\",\"enableJsonViews\":true}}]";
        JSONArray updatedJsonArrayFromAPI = new JSONArray(updatedJsonFromAPI);
        lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(updatedJsonArrayFromAPI);
        String jsonFromDB = configurableViewsRepository.getConfigurableViewJson("main");
        assertEquals(1515566670998l, lastSyncTimeStamp);
        assertEquals(updatedJsonArrayFromAPI.get(0).toString(), new JSONObject(jsonFromDB).toString());
    }

}
