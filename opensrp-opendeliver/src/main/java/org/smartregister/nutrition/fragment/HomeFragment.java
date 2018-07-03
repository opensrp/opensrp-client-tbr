package org.smartregister.nutrition.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ListView;

import org.smartregister.configurableviews.model.Residence;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.nutrition.R;
import org.smartregister.nutrition.activity.InTreatmentPatientRegisterActivity;
import org.smartregister.nutrition.activity.PositivePatientRegisterActivity;
import org.smartregister.nutrition.activity.PresumptivePatientRegisterActivity;
import org.smartregister.nutrition.adapter.RegisterArrayAdapter;
import org.smartregister.nutrition.application.OpenDeliverApplication;
import org.smartregister.nutrition.model.Register;
import org.smartregister.nutrition.model.RegisterCount;
import org.smartregister.nutrition.util.Constants;
import org.smartregister.nutrition.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.smartregister.nutrition.activity.BaseRegisterActivity.TOOLBAR_TITLE;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class HomeFragment extends ListFragment {
    private static String TAG = HomeFragment.class.getCanonicalName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            List<Register> values = new ArrayList<>();

            String jsonString = getConfigurableViewsRepository().getConfigurableViewJson(Constants.CONFIGURATION.HOME);
            ViewConfiguration homeViewConfig = jsonString == null ? null : OpenDeliverApplication.getJsonSpecHelper().getConfigurableView(jsonString);

            if (homeViewConfig != null) {
                List<org.smartregister.configurableviews.model.View> views = homeViewConfig.getViews();
                for (org.smartregister.configurableviews.model.View view : views) {
                    if (view.isVisible()) {
                        values.add(new Register(getActivity(), view, getPatientCountByRegisterType(view.getIdentifier())));
                    }
                }
                if (values.size() > 0) {
                    Collections.sort(values, new Comparator<Register>() {
                        @Override
                        public int compare(Register registerA, Register registerB) {
                            return registerA.getPosition() - registerB.getPosition();
                        }
                    });
                } else {
                    values.addAll(getDefaultRegisterList());
                }
            } else {

                values.addAll(getDefaultRegisterList());
            }
            saveRegisterTitles(values);
            RegisterArrayAdapter adapter = new RegisterArrayAdapter(getActivity(), R.layout.row_register_view, values, homeViewConfig.getMetadata());
            setListAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void saveRegisterTitles(List<Register> registers) {
        for (Register register : registers)
            Utils.writePrefString(getActivity(), TOOLBAR_TITLE + register.getTitleToken(), register.getTitle());
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        Register register = (Register) this.getListAdapter().getItem(position);
        if(register.getDigest() != null && !(register.getDigest().isEmpty())){
            Uri uri = Uri.parse("https://oppia-pakistan.opendeliver.org/view?digest="+register.getDigest());
            final Intent intentDeviceTest = new Intent("org.digitalcampus.oppia.activity.ViewDigestActivity");
            intentDeviceTest.setData(uri);
            startActivity(intentDeviceTest);
        }
        else {
            if (register.getTitleToken().equals(Register.CHILD)) {
                initializeRegister(new Intent(this.getActivity(), PresumptivePatientRegisterActivity.class), register);
            } else if (register.getTitleToken().equals(Register.POSITIVE_PATIENTS)) {
                initializeRegister(new Intent(this.getActivity(), PositivePatientRegisterActivity.class), register);
            } else if (register.getTitleToken().equals(Register.IN_TREATMENT_PATIENTS)) {
                initializeRegister(new Intent(this.getActivity(), InTreatmentPatientRegisterActivity.class), register);
            }
        }
    }

    private void initializeRegister(Intent intent, Register register) {
        intent.putExtra(TOOLBAR_TITLE, register.getTitle());
        startActivity(intent);
    }

    public static RegisterCount getPatientCountByRegisterType(String registerType) {

        return OpenDeliverApplication.getInstance().getResultDetailsRepository().getRegisterCountByType(registerType);

    }

    private ConfigurableViewsRepository getConfigurableViewsRepository() {
        return OpenDeliverApplication.getInstance().getConfigurableViewsRepository();
    }

    private List<Register> getDefaultRegisterList() {
        List<Register> values = new ArrayList<>();
        //Render Default View if no configs exist
        org.smartregister.configurableviews.model.View view = new org.smartregister.configurableviews.model.View();
        Residence residence = new Residence();


        view.setIdentifier(Register.PRESUMPTIVE_PATIENTS);
        view.setLabel(Register.CHILD);
        residence.setPosition(0);
        view.setResidence(residence);

        values.add(new Register(getActivity(), view, getPatientCountByRegisterType(view.getIdentifier())));

        view = new org.smartregister.configurableviews.model.View();
        view.setIdentifier(Register.POSITIVE_PATIENTS);
        view.setLabel(Register.NUTRITION);
        residence.setPosition(1);
        view.setResidence(residence);

        values.add(new Register(getActivity(), view, getPatientCountByRegisterType(view.getIdentifier())));

        /*view = new org.smartregister.configurableviews.model.View();
        view.setIdentifier(Register.IN_TREATMENT_PATIENTS);
        view.setLabel(Register.IN_TREATMENT_PATIENTS);

        residence.setPosition(2);
        view.setResidence(residence);
        values.add(new Register(getActivity(), view, getPatientCountByRegisterType(view.getIdentifier())));*/
        return values;
    }
}
