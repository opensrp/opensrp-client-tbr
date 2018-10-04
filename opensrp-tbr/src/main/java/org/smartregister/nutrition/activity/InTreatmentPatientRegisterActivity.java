package org.smartregister.tbr.activity;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.CheckBox;

import org.smartregister.tbr.R;
import org.smartregister.tbr.fragment.InTreatmentPatientRegisterFragment;
import org.smartregister.tbr.util.OtherFiltersEnum;

import java.util.Arrays;
import java.util.List;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static util.TbrConstants.ENKETO_FORMS.ADD_IN_TREATMENT_PATIENT;
import static util.TbrConstants.ENKETO_FORMS.FOLLOWUP_VISIT;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.COMMON_REGISTER_ROW;
import static util.TbrConstants.VIEW_CONFIGS.INTREATMENT_REGISTER;
import static util.TbrConstants.VIEW_CONFIGS.INTREATMENT_REGISTER_HEADER;
import static util.TbrConstants.VIEW_CONFIGS.INTREATMENT_REGISTER_ROW;

/**
 * Created by samuelgithengi on 12/5/17.
 */

public class InTreatmentPatientRegisterActivity extends BaseRegisterActivity {
    @Override
    protected Fragment getRegisterFragment() {
        return new InTreatmentPatientRegisterFragment();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(INTREATMENT_REGISTER, INTREATMENT_REGISTER_HEADER, INTREATMENT_REGISTER_ROW, COMMON_REGISTER_HEADER, COMMON_REGISTER_ROW);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewPatient:
                String entityId = generateRandomUUIDString();
                startFormActivity(ADD_IN_TREATMENT_PATIENT, entityId, null);
                return true;
            case R.id.filterList:
                Dialog dialog = super.getDialog(R.layout.layout_dialog_filter_treatment);
                super.setCommonHandlers(dialog,InTreatmentPatientRegisterActivity.this);
                CheckBox checkBoxOverdueFU = (CheckBox) dialog.findViewById(R.id.chk_overdue_followup);
                checkBoxOverdueFU.setTag(OtherFiltersEnum.OVERDUE_FOLLOWUP);
                checkBoxOverdueFU.setOnCheckedChangeListener(this);

                CheckBox checkBoxOverdueSmear = (CheckBox) dialog.findViewById(R.id.chk_overdue_smear);
                checkBoxOverdueSmear.setTag(OtherFiltersEnum.OVERDUE_SMEAR);
                checkBoxOverdueSmear.setOnCheckedChangeListener(this);

                if(!getFilterOtherResult().isEmpty()){
                    if(getFilterOtherResult().contains(checkBoxOverdueFU.getTag())) {
                        checkBoxOverdueFU.setOnCheckedChangeListener(null);
                        checkBoxOverdueFU.setChecked(true);
                        checkBoxOverdueFU.setOnCheckedChangeListener(this);
                    }
                    if(getFilterOtherResult().contains(checkBoxOverdueSmear.getTag())) {
                        checkBoxOverdueSmear.setOnCheckedChangeListener(null);
                        checkBoxOverdueSmear.setChecked(true);
                        checkBoxOverdueSmear.setOnCheckedChangeListener(this);
                    }
                }
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected List<String> buildFormNameList() {
        formNames = super.buildFormNameList();
        formNames.add(FOLLOWUP_VISIT);
        formNames.add(ADD_IN_TREATMENT_PATIENT);
        return formNames;
    }
}
