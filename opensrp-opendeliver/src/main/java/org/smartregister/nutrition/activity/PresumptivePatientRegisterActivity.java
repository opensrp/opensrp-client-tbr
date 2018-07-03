package org.smartregister.nutrition.activity;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.nutrition.R;
import org.smartregister.nutrition.fragment.PresumptivePatientRegisterFragment;
import org.smartregister.nutrition.util.OtherFiltersEnum;

import java.util.Arrays;
import java.util.List;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static util.TbrConstants.ENKETO_FORMS.DIAGNOSIS;
import static util.TbrConstants.ENKETO_FORMS.NUTRITION_CASECLOSING;
import static util.TbrConstants.ENKETO_FORMS.NUTRITION_ENROLLMENT;
import static util.TbrConstants.ENKETO_FORMS.NUTRITION_FOLLOWUP;
import static util.TbrConstants.ENKETO_FORMS.SCREENING_FORM;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER;
import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_ROW;

/**
 * Created by samuelgithengi on 10/30/17.
 */

public class PresumptivePatientRegisterActivity extends BaseRegisterActivity {

    @Override
    protected Fragment getRegisterFragment() {
        return new PresumptivePatientRegisterFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewPatient:
                String entityId = generateRandomUUIDString();
                startFormActivity(NUTRITION_ENROLLMENT, entityId, null);
                return true;
            /*case R.id.sort_selection:
                super.onOptionsItemSelected(item);*/
            case R.id.filterList:
                Dialog dialog = super.getDialog(R.layout.layout_dialog_filter_presumptive);
                CheckBox checkBoxNotDiagOnePlusWeeks = (CheckBox) dialog.findViewById(R.id.chk_not_diag_one_plus_weeks);
                checkBoxNotDiagOnePlusWeeks.setTag(OtherFiltersEnum.NOT_DIAGNOSED_1PLUS_WEEKS);
                checkBoxNotDiagOnePlusWeeks.setOnCheckedChangeListener(this);
                if(!getFilterOtherResult().isEmpty() && getFilterOtherResult().contains(checkBoxNotDiagOnePlusWeeks.getTag())){
                    checkBoxNotDiagOnePlusWeeks.setOnCheckedChangeListener(null);
                    checkBoxNotDiagOnePlusWeeks.setChecked(true);
                    checkBoxNotDiagOnePlusWeeks.setOnCheckedChangeListener(this);
                }

                super.setCommonHandlers(dialog,PresumptivePatientRegisterActivity.this);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void savePartialFormData(String formData, String id, String formName, JSONObject fieldOverrides) {
        Toast.makeText(this, formName + " partially submitted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(PRESUMPTIVE_REGISTER, PRESUMPTIVE_REGISTER_HEADER, PRESUMPTIVE_REGISTER_ROW, COMMON_REGISTER_HEADER, COMMON_REGISTER_ROW);
    }

    @Override
    protected List<String> buildFormNameList() {
        formNames = super.buildFormNameList();
        formNames.add(0, NUTRITION_ENROLLMENT);
        formNames.add(NUTRITION_FOLLOWUP);
        formNames.add(NUTRITION_CASECLOSING);
        return formNames;
    }
}
