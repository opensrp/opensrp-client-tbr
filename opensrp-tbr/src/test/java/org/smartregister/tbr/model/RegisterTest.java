package org.smartregister.tbr.model;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.jsonspec.JsonSpecHelper;
import org.smartregister.tbr.jsonspec.model.Residence;
import org.smartregister.tbr.jsonspec.model.View;

/**
 * Created by ndegwamartin on 17/10/2017.
 */

public class RegisterTest extends BaseUnitTest {

    @Mock
    private View view;

    @Mock
    private Residence residence;

    @Mock
    private JsonSpecHelper jsonSpecHelper;

    private static final String TITlE = "title";
    private static final String TITlE_TOKEN = "title_token";
    private static final int INT_0 = 0;
    private static final int INT_1 = 1;
    private static final int INT_10 = 10;
    private static final int INT_20 = 20;


    @Before
    public void setUp() {

        org.mockito.MockitoAnnotations.initMocks(this);
    }

    @Test
    public void assertDefaultConstructorsCreateNonNullObjectOnInstantiation() {
        Mockito.when(view.getResidence()).thenReturn(residence);
        junit.framework.Assert.assertNotNull(new RegisterTestVersion(RuntimeEnvironment.application, view, INT_0, INT_1));
    }


    @Test
    public void assertTestGettersAndSetters() {
        Mockito.when(view.getResidence()).thenReturn(residence);
        Register register = new RegisterTestVersion(RuntimeEnvironment.application, view, INT_0, INT_1);

        register.setTitle(TITlE);
        junit.framework.Assert.assertEquals(TITlE, register.getTitle());

        register.setTitleToken(TITlE_TOKEN);
        junit.framework.Assert.assertEquals(TITlE_TOKEN, register.getTitleToken());

        register.setTotalPatients(INT_10);
        junit.framework.Assert.assertEquals(INT_10, register.getTotalPatients());

        register.setTotalPatientsWithDueOverdue(INT_20);
        junit.framework.Assert.assertEquals(INT_20, register.getTotalPatientsWithDueOverdue());
    }

    private class RegisterTestVersion extends Register {

        public RegisterTestVersion(Context context, View view, int totalPatients, int totalPatientsWithDueOverdue) {
            super(context, view, totalPatients, totalPatientsWithDueOverdue);
        }

        @Override
        public String getStringResource(Context context, View view) {
            return TITlE;
        }
    }
}