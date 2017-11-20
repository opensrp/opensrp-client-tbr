package org.smartregister.tbr.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BaseActivity;
import org.smartregister.tbr.activity.PresumptivePatientDetailActivity;
import org.smartregister.tbr.activity.PresumptivePatientRegisterActivity;
import org.smartregister.tbr.adapter.RegisterArrayAdapter;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.model.Register;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;

/**
 * Created by ndegwamartin on 12/10/2017.
 */

public class RegisterFragment extends ListFragment {
    private static String TAG = RegisterFragment.class.getCanonicalName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);
            List<Register> values = new ArrayList<>();
            String jsonString = getConfigurableViewsRepository().getConfigurableViewJson(Constants.CONFIGURATION.HOME);
            if (jsonString == null) return;


            ViewConfiguration homeViewConfig = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
            if (homeViewConfig != null) {
                List<org.smartregister.tbr.jsonspec.model.View> views = homeViewConfig.getViews();
                for (org.smartregister.tbr.jsonspec.model.View view : views) {
                    if (view.isVisible()) {
                        values.add(new Register(view, RegisterDataRepository.getPatientCountByRegisterType(view.getIdentifier()),
                                RegisterDataRepository.getOverduePatientCountByRegisterType(view.getIdentifier())));
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
                    Utils.showDialogMessage(getActivity(), "Info", "You need to configure at least One Register as Visible on the Server Side...");
                }
                RegisterArrayAdapter adapter = new RegisterArrayAdapter(getActivity(), R.layout.register_row_view, values);
                setListAdapter(adapter);
            } else {

                Utils.showDialogMessage(getActivity(), "Info", "Missing Home View Configuration on server");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        TextView registerTitle = (TextView) view.findViewById(R.id.registerTitleView);
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        Utils.showToast(getActivity(), registerTitle.getText().toString() + " Register!");
        Register register = (Register) this.getListAdapter().getItem(position);
        if (register.getTitleToken().equals(Register.PRESUMPTIVE_PATIENTS)) {
            Intent intent = new Intent(this.getActivity(), PresumptivePatientRegisterActivity.class);
            intent.putExtra(TOOLBAR_TITLE, register.getTitle());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this.getActivity(), PresumptivePatientDetailActivity.class);
            intent.putExtra(Constants.INTENT_KEY.REGISTER_TITLE, register.getTitle());
            startActivity(intent);

        }
    }

    public static class RegisterDataRepository {
        public static int getPatientCountByRegisterType(String registerType) {
            if (registerType.equals(Register.PRESUMPTIVE_PATIENTS)) {
                return 12;
            } else if (registerType.equals(Register.POSITIVE_PATIENTS)) {
                return 13;
            } else if (registerType.equals(Register.IN_TREATMENT_PATIENTS)) {
                return 4;
            } else {
                return 0;
            }
        }

        public static int getOverduePatientCountByRegisterType(String registerType) {
            if (registerType.equals(Register.PRESUMPTIVE_PATIENTS)) {
                return 0;
            } else if (registerType.equals(Register.POSITIVE_PATIENTS)) {
                return 10;
            } else if (registerType.equals(Register.IN_TREATMENT_PATIENTS)) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    private ConfigurableViewsRepository getConfigurableViewsRepository() {
        return TbrApplication.getInstance().getConfigurableViewsRepository();
    }
}
