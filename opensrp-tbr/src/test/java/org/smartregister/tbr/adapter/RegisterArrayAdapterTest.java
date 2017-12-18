package org.smartregister.tbr.adapter;


import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.tbr.BaseUnitTest;
import org.smartregister.tbr.R;
import org.smartregister.tbr.jsonspec.model.Residence;
import org.smartregister.tbr.jsonspec.model.View;
import org.smartregister.tbr.model.Register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ndegwamartin on 18/10/2017.
 */

public class RegisterArrayAdapterTest extends BaseUnitTest {

    private static final String TITlE = "title";
    private static final String TITlE_TOKEN = "title_token";
    private static final int INT_0 = 0;
    private static final int INT_10 = 10;
    private static final int INT_20 = 20;


    @Mock
    private View view;

    @Mock
    private Residence residence;

    @Mock
    private ViewGroup viewGroup;

    @Mock
    private Register register;

    @Before
    public void setUp() {

        org.mockito.MockitoAnnotations.initMocks(this);
    }

    @Test
    public void instantiationWithDefaultConstructorCreatesNonNullInstance() {
        junit.framework.Assert.assertNotNull(new RegisterArrayAdapter(RuntimeEnvironment.application, 0, Collections.EMPTY_LIST));
    }


    @Test
    public void callingGetItemReturnsCorrectValue() {
        List<Register> registers = new ArrayList<>();
        Mockito.when(view.getResidence()).thenReturn(residence);
        Mockito.when(register.getStringResource(RuntimeEnvironment.application, view)).thenReturn(TITlE);
        registers.add(register);
        RegisterArrayAdapter arrayAdapter = new RegisterArrayAdapter(RuntimeEnvironment.application, 0, registers);
        junit.framework.Assert.assertEquals(register, arrayAdapter.getItem(0));

    }

    @Test
    public void callingGetViewReturnsCorrectView() {

        List<Register> registers = new ArrayList<>();
        Mockito.when(view.getResidence()).thenReturn(residence);
        Mockito.when(register.getTitle()).thenReturn(TITlE);
        Mockito.when(register.getTitleToken()).thenReturn(TITlE_TOKEN);
        Mockito.when(register.getPosition()).thenReturn(INT_0);
        Mockito.when(register.getTotalPatients()).thenReturn(INT_20);
        Mockito.when(register.getTotalPatientsWithDueOverdue()).thenReturn(INT_10);
        registers.add(register);
        RegisterArrayAdapter arrayAdapter = new RegisterArrayAdapter(RuntimeEnvironment.application, 0, registers);

        android.view.View view = arrayAdapter.getView(0, null, viewGroup);
        TextView textView = (TextView) view.findViewById(R.id.registerTitleView);
        junit.framework.Assert.assertEquals(TITlE, textView.getText());

        textView = (TextView) view.findViewById(R.id.patientCountView);
        junit.framework.Assert.assertEquals("(" + INT_20 + ")", textView.getText().toString().trim());

        textView = (TextView) view.findViewById(R.id.patientDueOverdueCountView);
        junit.framework.Assert.assertEquals(String.valueOf(INT_10), textView.getText().toString().trim());

        ImageView imageView = (ImageView) view.findViewById(R.id.registerIconView);
        junit.framework.Assert.assertEquals(RuntimeEnvironment.application.getResources().getDrawable(R.drawable.ic_presumptive_patients), imageView.getDrawable());

    }
}
