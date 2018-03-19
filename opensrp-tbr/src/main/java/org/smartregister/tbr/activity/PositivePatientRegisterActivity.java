package org.smartregister.tbr.activity;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.CheckBox;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.PositivePatientRegisterFragment;
import org.smartregister.tbr.util.OtherFiltersEnum;

import java.util.Arrays;
import java.util.List;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static util.TbrConstants.ENKETO_FORMS.ADD_POSITIVE_PATIENT;
import static util.TbrConstants.ENKETO_FORMS.TREATMENT_INITIATION;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_ROW;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterActivity extends BaseRegisterActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewPatient:
                String entityId = generateRandomUUIDString();
                startFormActivity(ADD_POSITIVE_PATIENT, entityId, null);
                return true;
            case R.id.filterList:
                Dialog dialog = super.getDialog(R.layout.layout_dialog_filter_positive);
                super.setCommonHandlers(dialog,PositivePatientRegisterActivity.this);
                CheckBox checkBoxNotStartTreatOnePlusWeeks = (CheckBox) dialog.findViewById(R.id.chk_not_started_treat_one_plus_weeks);
                checkBoxNotStartTreatOnePlusWeeks.setTag(OtherFiltersEnum.NOT_STARTED_TREATMENT_1PLUS_WEEKS);
                checkBoxNotStartTreatOnePlusWeeks.setOnCheckedChangeListener(this);

                if(!getFilterOtherResult().isEmpty()){
                    if(getFilterOtherResult().contains(checkBoxNotStartTreatOnePlusWeeks.getTag())) {
                        checkBoxNotStartTreatOnePlusWeeks.setOnCheckedChangeListener(null);
                        checkBoxNotStartTreatOnePlusWeeks.setChecked(true);
                        checkBoxNotStartTreatOnePlusWeeks.setOnCheckedChangeListener(this);
                    }
                }
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Fragment getRegisterFragment() {
        return new PositivePatientRegisterFragment();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(POSITIVE_REGISTER, POSITIVE_REGISTER_HEADER, POSITIVE_REGISTER_ROW, COMMON_REGISTER_HEADER, COMMON_REGISTER_ROW);
    }

    @Override
    protected List<String> buildFormNameList() {
        formNames = super.buildFormNameList();
        formNames.add(0, ADD_POSITIVE_PATIENT);
        formNames.add(TREATMENT_INITIATION);
        return formNames;
    }
}
