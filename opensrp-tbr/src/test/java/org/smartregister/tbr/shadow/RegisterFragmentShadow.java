package org.smartregister.tbr.shadow;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.fragment.RegisterFragment;
import org.smartregister.tbr.model.Register;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;

/**
 * Created by ndegwamartin on 13/11/2017.
 */

@Implements(RegisterFragment.class)
public class RegisterFragmentShadow extends Shadow {
    public void onActivityCreated(Bundle savedInstanceState) {

    }

    public void onListItemClick(ListView listView, View view, int position, long id) {
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
