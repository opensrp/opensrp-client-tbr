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

    @Before
    public void setUp() throws Exception {
        configurableViewsRepository = TbrApplication.getInstance().getConfigurableViewsRepository();
        configurableViewsRepository.getWritableDatabase().delete(ConfigurableViewsRepository.TABLE_NAME, null, null);
    }

    @After
    public void tearDown() throws Exception {
        configurableViewsRepository.getWritableDatabase().close();
    }

    @Test
    @SmallTest
    public void testSaveViewConfiguration() throws Exception {
        String expectedJsonFromAPI = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}]";
        JSONArray jsonArray = new JSONArray(expectedJsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("login"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArray);
        assertEquals(1508765191, lastSyncTimeStamp);
        assertTrue(configurableViewsRepository.configurableViewExists("login"));
    }

    @Test
    @SmallTest
    public void testGetConfigurableViewJson() throws Exception {
        String jsonFromAPI = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}]";
        JSONArray jsonArrayFromAPI = new JSONArray(jsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("login"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArrayFromAPI);
        String jsonFromDB = configurableViewsRepository.getConfigurableViewJson("login");
        assertEquals(1508765191, lastSyncTimeStamp);
        assertEquals(jsonArrayFromAPI.get(0).toString(), new JSONObject(jsonFromDB).toString());
    }

    @Test
    @SmallTest
    public void testUpdateViewConfiguration() throws Exception {
        String jsonFromAPI = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765191,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":false,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51260\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}]";
        JSONArray jsonArrayFromAPI = new JSONArray(jsonFromAPI);
        assertFalse(configurableViewsRepository.configurableViewExists("login"));
        long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(jsonArrayFromAPI);
        assertEquals(1508765191, lastSyncTimeStamp);
        assertTrue(configurableViewsRepository.configurableViewExists("login"));
        String updatedJsonFromAPI = "[{\"type\":\"ViewConfiguration\",\"serverVersion\":1508765254,\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"language\":null,\"applicationName\":null,\"showPasswordCheckbox\":true,\"logoUrl\":\"http://10.20.25.51:8080/assets/icon3.png\",\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#a51262\",\"endColor\":\"#ea62ca\"}},\"_id\":\"19a2e8aa6739d77a2b780199c6122866\",\"_rev\":\"7-25b620fe5a397976b163add92d133d92\"}]";
        JSONArray updatedJsonArrayFromAPI = new JSONArray(updatedJsonFromAPI);
        lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(updatedJsonArrayFromAPI);
        String jsonFromDB = configurableViewsRepository.getConfigurableViewJson("login");
        assertEquals(1508765254, lastSyncTimeStamp);
        assertEquals(updatedJsonArrayFromAPI.get(0).toString(), new JSONObject(jsonFromDB).toString());
    }

}
