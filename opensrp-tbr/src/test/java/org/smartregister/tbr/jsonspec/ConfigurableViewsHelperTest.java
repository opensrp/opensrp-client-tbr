package org.smartregister.tbr.jsonspec;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.R;
import org.smartregister.tbr.jsonspec.model.View;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static util.TbrConstants.REGISTER_COLUMNS.DIAGNOSE;
import static util.TbrConstants.REGISTER_COLUMNS.DROPDOWN;
import static util.TbrConstants.REGISTER_COLUMNS.ENCOUNTER;
import static util.TbrConstants.REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.REGISTER_COLUMNS.RESULTS;
import static util.TbrConstants.REGISTER_COLUMNS.XPERT_RESULTS;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class ConfigurableViewsHelperTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ConfigurableViewsRepository configurableViewsRepository;

    @Mock
    private Context context;

    private ConfigurableViewsHelper configurableViewsHelper;

    private String loginJson = "{\"_id\":\"92141b17040021a7ce326194ff0029f7\",\"_rev\":\"16-c66bc2d29747e7ddae866a22a7e0a728\",\"serverVersion\":25113420928,\"type\":\"ViewConfiguration\",\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"showPasswordCheckbox\":true,\"logoUrl\":null,\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#3949AB\",\"endColor\":\"#5C6BC0\"}}}";

    private String presumptiveRegisterJson = "{\"_id\":\"03be8d09bba527b1b39cb2b58c166def\",\"_rev\":\"51-f2865ca5a496246d5fb77e76ac849785\",\"serverVersion\":35113420958,\"type\":\"ViewConfiguration\",\"identifier\":\"presumptive_register\",\"metadata\":{\"type\":\"Register\",\"enableAdvancedSearch\":true,\"enableSortList\":true,\"enableFilterList\":true,\"searchBarText\":\"Name and Participant ID\"},\"views\":[{\"identifier\":\"patient\",\"visible\":true,\"label\":\"column_patient\",\"residence\":{\"position\":1,\"layout_weight\":3.4}},{\"identifier\":\"results\",\"visible\":true,\"label\":\"column_results\",\"residence\":{\"position\":0,\"layout_weight\":3.4}},{\"identifier\":\"diagnose\",\"visible\":false,\"label\":\"column_diagnose\",\"residence\":{\"position\":2,\"layout_weight\":2.6}},{\"identifier\":\"encounter\",\"visible\":false,\"label\":\"column_encounter\",\"residence\":{\"position\":2,\"layout_weight\":2.6}},{\"identifier\":\"xpert_results\",\"visible\":true,\"label\":\"column_xpert_results\",\"residence\":{\"position\":2,\"layout_weight\":3}},{\"identifier\":\"dropdown\",\"visible\":false,\"label\":\"column_dropdown\",\"residence\":{\"position\":2,\"layout_weight\":2.6}}]}";


    @Before
    public void initializeTest() {
        configurableViewsHelper = new ConfigurableViewsHelper(configurableViewsRepository, new JsonSpecHelper(context), context);
    }

    @Test
    public void testRegisterViewConfigurations() {
        List<String> views = Arrays.asList("login", "presumptive_register");
        when(configurableViewsRepository.getConfigurableViewJson("login")).thenReturn(loginJson);
        when(configurableViewsRepository.getConfigurableViewJson("presumptive_register")).thenReturn(presumptiveRegisterJson);
        configurableViewsHelper.registerViewConfigurations(views);
        assertEquals("login", configurableViewsHelper.getViewConfiguration("login").getIdentifier());
        assertEquals("presumptive_register", configurableViewsHelper.getViewConfiguration("presumptive_register").getIdentifier());
    }


    @Test
    public void testRegisterViewConfigurationsWhenViewNotExists() {
        List<String> views = Arrays.asList("login");
        when(configurableViewsRepository.getConfigurableViewJson("login")).thenReturn(null);
        configurableViewsHelper.registerViewConfigurations(views);
        assertNull(configurableViewsHelper.getViewConfiguration("login"));
    }

    @Test
    public void testUnregisterViewConfigurations() {
        List<String> views = Arrays.asList("login");
        when(configurableViewsRepository.getConfigurableViewJson("login")).thenReturn(loginJson);
        configurableViewsHelper.registerViewConfigurations(views);
        configurableViewsHelper.unregisterViewConfiguration(views);
        assertNull(configurableViewsHelper.getViewConfiguration("login"));
    }


    @Test
    public void testGetViewConfiguration() {
        List<String> views = Arrays.asList("login");
        assertNull(configurableViewsHelper.getViewConfiguration("login"));
        when(configurableViewsRepository.getConfigurableViewJson("login")).thenReturn(loginJson);
        configurableViewsHelper.registerViewConfigurations(views);
        assertEquals("login", configurableViewsHelper.getViewConfiguration("login").getIdentifier());
    }

    @Test
    public void testGetRegisterActiveColumns() {
        List<String> views = Arrays.asList("presumptive_register");
        when(configurableViewsRepository.getConfigurableViewJson("presumptive_register")).thenReturn(presumptiveRegisterJson);
        configurableViewsHelper.registerViewConfigurations(views);
        Set<View> activeColumns = configurableViewsHelper.getRegisterActiveColumns("presumptive_register");
        assertEquals(3, activeColumns.size());
        //test ordering of register
        List<View> list = new ArrayList(activeColumns);
        assertEquals("results", list.get(0).getIdentifier());
        assertEquals("patient", list.get(1).getIdentifier());
        assertEquals("xpert_results", list.get(2).getIdentifier());
    }

    @Test
    public void testprocessRegisterColumns() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        android.view.View view = LayoutInflater.from(activity).inflate(R.layout.register_presumptive_list_row, null);
        Map<String, Integer> mapping = new HashMap();
        mapping.put(PATIENT, R.id.patient_column);
        mapping.put(RESULTS, R.id.results_column);
        mapping.put(DIAGNOSE, R.id.diagnose_column);
        mapping.put(ENCOUNTER, R.id.encounter_column);
        mapping.put(XPERT_RESULTS, R.id.xpert_results_column);
        mapping.put(DROPDOWN, R.id.dropdown_column);
        List<String> views = Arrays.asList("presumptive_register");
        when(configurableViewsRepository.getConfigurableViewJson("presumptive_register")).thenReturn(presumptiveRegisterJson);
        configurableViewsHelper.registerViewConfigurations(views);
        configurableViewsHelper.processRegisterColumns(mapping, view, configurableViewsHelper.getRegisterActiveColumns("presumptive_register"), R.id.register_columns);
        assertEquals(3, ((ViewGroup) view).getChildCount());
        assertEquals(R.id.results_column, ((ViewGroup) view).getChildAt(0).getId());
        assertEquals(R.id.patient_column, ((ViewGroup) view).getChildAt(1).getId());
        assertEquals(R.id.xpert_results_column, ((ViewGroup) view).getChildAt(2).getId());

    }
}
