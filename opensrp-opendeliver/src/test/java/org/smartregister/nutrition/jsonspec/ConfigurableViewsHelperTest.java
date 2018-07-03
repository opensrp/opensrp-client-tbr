package org.smartregister.nutrition.jsonspec;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.nutrition.BaseUnitTest;
import org.smartregister.nutrition.R;

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

    private Context context = RuntimeEnvironment.application;

    private ConfigurableViewsHelper configurableViewsHelper;

    private JsonSpecHelper jsonSpecHelper = new JsonSpecHelper(context);

    private Activity activity;

    private android.view.View view;

    private ViewConfiguration viewConfiguration;

    private ViewConfiguration commonConfiguration;

    private String loginJson = "{\"_id\":\"92141b17040021a7ce326194ff0029f7\",\"_rev\":\"16-c66bc2d29747e7ddae866a22a7e0a728\",\"serverVersion\":25113420928,\"type\":\"ViewConfiguration\",\"identifier\":\"login\",\"metadata\":{\"type\":\"Login\",\"showPasswordCheckbox\":true,\"logoUrl\":null,\"background\":{\"orientation\":\"BOTTOM_TOP\",\"startColor\":\"#3949AB\",\"endColor\":\"#5C6BC0\"}}}";

    private String presumptiveRegisterJson = "{\"_id\":\"03be8d09bba527b1b39cb2b58c166def\",\"_rev\":\"51-f2865ca5a496246d5fb77e76ac849785\",\"serverVersion\":35113420958,\"type\":\"ViewConfiguration\",\"identifier\":\"presumptive_register\",\"metadata\":{\"type\":\"Register\",\"enableAdvancedSearch\":true,\"enableSortList\":true,\"enableFilterList\":true,\"searchBarText\":\"Name and Participant ID\"},\"views\":[{\"identifier\":\"patient\",\"visible\":true,\"label\":\"column_patient\",\"residence\":{\"position\":1,\"layout_weight\":3.4}},{\"identifier\":\"results\",\"visible\":true,\"label\":\"column_results\",\"residence\":{\"position\":0,\"layout_weight\":3.4}},{\"identifier\":\"diagnose\",\"visible\":false,\"label\":\"column_diagnose\",\"residence\":{\"position\":2,\"layout_weight\":2.6}},{\"identifier\":\"encounter\",\"visible\":false,\"label\":\"column_encounter\",\"residence\":{\"position\":2,\"layout_weight\":2.6}},{\"identifier\":\"xpert_results\",\"visible\":true,\"label\":\"column_xpert_results\",\"residence\":{\"position\":2,\"layout_weight\":3}},{\"identifier\":\"dropdown\",\"visible\":false,\"label\":\"column_dropdown\",\"residence\":{\"position\":2,\"layout_weight\":2.6}}]}";

    private String presumptiveRegisterJson2View = "{\"_id\":\"3a065d7c3354eb2bc23c8a3bc303dab7\",\"_rev\":\"3-a1828b43823b27f1f1c305250277d810\",\"serverVersion\":35113420985,\"type\":\"ViewConfiguration\",\"identifier\":\"presumptive_register_row\",\"jsonView\":{\"views\":[{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"diagnose_lnk\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"5dp\",\"type\":\"dimen\",\"name\":\"layout_marginBottom\"},{\"value\":\"Diagnose..\",\"type\":\"string\",\"name\":\"text\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"textAlignment\"},{\"value\":\"true\",\"type\":\"BOOLEAN\",\"name\":\"textAllCaps\"},{\"value\":\"#3269D4\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"24sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"TextView\"}],\"properties\":[{\"value\":\"diagnose_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"2.6\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"vertical\",\"type\":\"string\",\"name\":\"orientation\"}],\"widget\":\"LinearLayout\"},{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"encounter\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"5dp\",\"type\":\"dimen\",\"name\":\"layout_marginBottom\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"textAlignment\"},{\"value\":\"#757575\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"TextView\"}],\"properties\":[{\"value\":\"encounter_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"2.6\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"vertical\",\"type\":\"string\",\"name\":\"orientation\"},{\"value\":\"gone\",\"type\":\"string\",\"name\":\"visibility\"}],\"widget\":\"LinearLayout\"}],\"properties\":[{\"value\":\"register_columns\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"82dp\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"#eeeeee\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"horizontal\",\"type\":\"string\",\"name\":\"orientation\"},{\"value\":\"10\",\"type\":\"string\",\"name\":\"weightSum\"}],\"widget\":\"LinearLayout\"}}";

    private String commonRegisterJson2View = "{\"_id\":\"294b209b96229fe64b94b7430e039f83\",\"_rev\":\"27-6e2ea6c5f8ca5c8bd076bb80c21a54a3\",\"serverVersion\":35113420999,\"type\":\"ViewConfiguration\",\"identifier\":\"common_register_row\",\"jsonView\":{\"views\":[{\"views\":[{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"patient_name\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"true\",\"type\":\"BOOLEAN\",\"name\":\"layout_alignParentLeft\"},{\"value\":\"true\",\"type\":\"BOOLEAN\",\"name\":\"layout_alignParentTop\"},{\"value\":\"Patient Name::\",\"type\":\"string\",\"name\":\"text\"},{\"value\":\"18sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"org.smartregister.view.customcontrols.CustomFontTextView\"},{\"views\":[],\"properties\":[{\"value\":\"participant_id\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"patient_name\",\"type\":\"ref\",\"name\":\"layout_below\"},{\"value\":\"Participant ID\",\"type\":\"string\",\"name\":\"text\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"},{\"value\":\"visible\",\"type\":\"string\",\"name\":\"visibility\"}],\"widget\":\"org.smartregister.view.customcontrols.CustomFontTextView\"},{\"views\":[],\"properties\":[{\"value\":\"age\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"participant_id\",\"type\":\"ref\",\"name\":\"layout_below\"},{\"value\":\"Age\",\"type\":\"string\",\"name\":\"text\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"org.smartregister.view.customcontrols.CustomFontTextView\"},{\"views\":[],\"properties\":[{\"value\":\"comma_separator\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"participant_id\",\"type\":\"ref\",\"name\":\"layout_below\"},{\"value\":\"age\",\"type\":\"ref\",\"name\":\"layout_toRightOf\"},{\"value\":\",\",\"type\":\"string\",\"name\":\"text\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"org.smartregister.view.customcontrols.CustomFontTextView\"},{\"views\":[],\"properties\":[{\"value\":\"gender\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"participant_id\",\"type\":\"ref\",\"name\":\"layout_below\"},{\"value\":\"comma_separator\",\"type\":\"ref\",\"name\":\"layout_toRightOf\"},{\"value\":\"Gender\",\"type\":\"string\",\"name\":\"text\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"},{\"value\":\"5dp\",\"type\":\"dimen\",\"name\":\"padding_left\"}],\"widget\":\"org.smartregister.view.customcontrols.CustomFontTextView\"}],\"properties\":[{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"3dp\",\"type\":\"dimen\",\"name\":\"layout_marginBottom\"},{\"value\":\"3dp\",\"type\":\"dimen\",\"name\":\"layout_marginTop\"},{\"value\":\"10dp\",\"type\":\"dimen\",\"name\":\"padding_left\"}],\"widget\":\"RelativeLayout\"}],\"properties\":[{\"value\":\"patient_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"4\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"horizontal\",\"type\":\"string\",\"name\":\"orientation\"}],\"widget\":\"LinearLayout\"},{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"result_details\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"3dp\",\"type\":\"dimen\",\"name\":\"layout_marginTop\"},{\"value\":\"center_horizontal\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"#757575\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"},{\"value\":\"gone\",\"type\":\"string\",\"name\":\"visibility\"}],\"widget\":\"TextView\"},{\"views\":[],\"properties\":[{\"value\":\"result_lnk\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"3dp\",\"type\":\"dimen\",\"name\":\"layout_marginBottom\"},{\"value\":\"result\",\"type\":\"String\",\"name\":\"text\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"textAlignment\"},{\"value\":\"true\",\"type\":\"BOOLEAN\",\"name\":\"textAllCaps\"},{\"value\":\"#3269D4\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"18sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"TextView\"}],\"properties\":[{\"value\":\"results_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"3.4\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"vertical\",\"type\":\"string\",\"name\":\"orientation\"}],\"widget\":\"LinearLayout\"},{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"xpert_result_details\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"wrap_content\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"3dp\",\"type\":\"dimen\",\"name\":\"layout_marginTop\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"#757575\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"},{\"value\":\"gone\",\"type\":\"string\",\"name\":\"visibility\"}],\"widget\":\"TextView\"},{\"views\":[],\"properties\":[{\"value\":\"xpert_result_lnk\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"3dp\",\"type\":\"dimen\",\"name\":\"layout_marginBottom\"},{\"value\":\"result\",\"type\":\"String\",\"name\":\"text\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"textAlignment\"},{\"value\":\"true\",\"type\":\"BOOLEAN\",\"name\":\"textAllCaps\"},{\"value\":\"#3269D4\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"18sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"TextView\"}],\"properties\":[{\"value\":\"xpert_results_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"2.6\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"vertical\",\"type\":\"string\",\"name\":\"orientation\"},{\"value\":\"gone\",\"type\":\"string\",\"name\":\"visibility\"}],\"widget\":\"LinearLayout\"},{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"dropdown_btn\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"layout_gravity\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"}],\"widget\":\"ImageButton\"}],\"properties\":[{\"value\":\"dropdown_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"2.6\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"center_vertical\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"vertical\",\"type\":\"string\",\"name\":\"orientation\"},{\"value\":\"gone\",\"type\":\"string\",\"name\":\"visibility\"}],\"widget\":\"LinearLayout\"}],\"properties\":[{\"value\":\"register_columns\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"82dp\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"#eeeeee\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"horizontal\",\"type\":\"string\",\"name\":\"orientation\"},{\"value\":\"10\",\"type\":\"string\",\"name\":\"weightSum\"}],\"widget\":\"LinearLayout\"}}";

    private String presumptiveRegisterJson2ViewWithError = "{\"_id\":\"3a065d7c3354eb2bc23c8a3bc303dab7\",\"_rev\":\"3-a1828b43823b27f1f1c305250277d810\",\"serverVersion\":35113420985,\"type\":\"ViewConfiguration\",\"identifier\":\"presumptive_register_row\",\"jsonView\":{\"views\":[{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"diagnose_lnk\",\"type\":\"ref\",\"name\":\"id\"}{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"5dp\",\"type\":\"dimen\",\"name\":\"layout_marginBottom\"},{\"value\":\"Diagnose..\",\"type\":\"string\",\"name\":\"text\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"textAlignment\"},{\"value\":\"true\",\"type\":\"BOOLEAN\",\"name\":\"textAllCaps\"},{\"value\":\"#3269D4\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"24sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"TextView\"}],\"properties\":[{\"value\":\"diagnose_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"2.6\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"vertical\",\"type\":\"string\",\"name\":\"orientation\"}],\"widget\":\"LinearLayout\"},{\"views\":[{\"views\":[],\"properties\":[{\"value\":\"encounter\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"5dp\",\"type\":\"dimen\",\"name\":\"layout_marginBottom\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"textAlignment\"},{\"value\":\"#757575\",\"type\":\"color\",\"name\":\"textColor\"},{\"value\":\"16sp\",\"type\":\"dimen\",\"name\":\"textSize\"}],\"widget\":\"TextView\"}],\"properties\":[{\"value\":\"encounter_column\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"0dp\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"1dp\",\"type\":\"dimen\",\"name\":\"layout_marginRight\"},{\"value\":\"2.6\",\"type\":\"float\",\"name\":\"layout_weight\"},{\"value\":\"#ffffff\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"center\",\"type\":\"string\",\"name\":\"gravity\"},{\"value\":\"vertical\",\"type\":\"string\",\"name\":\"orientation\"},{\"value\":\"gone\",\"type\":\"string\",\"name\":\"visibility\"}],\"widget\":\"LinearLayout\"}],\"properties\":[{\"value\":\"register_columns\",\"type\":\"ref\",\"name\":\"id\"},{\"value\":\"match_parent\",\"type\":\"dimen\",\"name\":\"layout_width\"},{\"value\":\"82dp\",\"type\":\"dimen\",\"name\":\"layout_height\"},{\"value\":\"#eeeeee\",\"type\":\"color\",\"name\":\"background\"},{\"value\":\"horizontal\",\"type\":\"string\",\"name\":\"orientation\"},{\"value\":\"10\",\"type\":\"string\",\"name\":\"weightSum\"}],\"widget\":\"LinearLayout\"}}";

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
        mapping.put(XPERT_RESULTS, R.id.xpert_results_column);
        List<String> views = Arrays.asList("presumptive_register");
        when(configurableViewsRepository.getConfigurableViewJson("presumptive_register")).thenReturn(presumptiveRegisterJson);
        configurableViewsHelper.registerViewConfigurations(views);
        configurableViewsHelper.processRegisterColumns(mapping, view, configurableViewsHelper.getRegisterActiveColumns("presumptive_register"), R.id.register_columns);
        assertEquals(3, ((ViewGroup) view).getChildCount());
        assertEquals(R.id.results_column, ((ViewGroup) view).getChildAt(0).getId());
        assertEquals(R.id.patient_column, ((ViewGroup) view).getChildAt(1).getId());
        assertEquals(R.id.xpert_results_column, ((ViewGroup) view).getChildAt(2).getId());

    }

    private void setUpCommonObjects() {
        activity = Robolectric.buildActivity(Activity.class).create().get();
        view = LayoutInflater.from(activity).inflate(R.layout.register_presumptive_list_row, null);
        viewConfiguration = jsonSpecHelper.getConfigurableView(presumptiveRegisterJson2View);
        commonConfiguration = jsonSpecHelper.getConfigurableView(commonRegisterJson2View);
    }

    @Test
    public void testInflateDynamicView() {
        setUpCommonObjects();
        android.view.View dynamicView = configurableViewsHelper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
        assertEquals(24f, ((TextView) dynamicView.findViewById(R.id.diagnose_lnk)).getTextSize() / activity.getResources().getDisplayMetrics().scaledDensity, 0.001);
        assertEquals("Patient Name::", ((TextView) dynamicView.findViewById(R.id.patient_name)).getText());
    }

    @Test
    public void testInflateDynamicViewWithoutCommonJson() {
        setUpCommonObjects();
        android.view.View dynamicView = configurableViewsHelper.inflateDynamicView(viewConfiguration, null, view, R.id.register_columns, false);
        assertEquals(24f, ((TextView) dynamicView.findViewById(R.id.diagnose_lnk)).getTextSize(), 0.001);
        assertEquals("Diagnose..", ((TextView) dynamicView.findViewById(R.id.diagnose_lnk)).getText());

    }

    @Test
    public void testInflateDynamicViewWithErroneousJsonDefinition() {
        setUpCommonObjects();
        ViewConfiguration viewConfiguration = jsonSpecHelper.getConfigurableView(presumptiveRegisterJson2ViewWithError);
        android.view.View dynamicView = configurableViewsHelper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
        assertEquals(18f, ((TextView) dynamicView.findViewById(R.id.diagnose_lnk)).getTextSize(), 0.001);
        assertEquals("Diagnose", ((TextView) dynamicView.findViewById(R.id.diagnose_lnk)).getText());
    }
}


