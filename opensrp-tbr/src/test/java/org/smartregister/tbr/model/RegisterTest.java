package org.smartregister.tbr.model;

import org.junit.Test;
import org.smartregister.tbr.BaseUnitTest;

/**
 * Created by ndegwamartin on 17/10/2017.
 */

public class RegisterTest extends BaseUnitTest {
    private static final String TITlE = "title";
    private static final String TEST_TITlE = "testTitle";
    private static final String TITlE_TOKEN = "title_token";
    private static final String TEST_TITlE_TOKEN = "title_token";
    private static final int INT_0 = 0;
    private static final int INT_1 = 1;
    private static final int INT_10 = 10;
    private static final int INT_20 = 20;

    @Test
    public void assertDefaultConstructorsCreateNonNullObjectOnInstantiation() {
        junit.framework.Assert.assertNotNull(new Register(TITlE, "", INT_0, INT_1));
    }


    @Test
    public void assertTestGettersAndSetters() {
        Register register = new Register(TITlE, TITlE_TOKEN, INT_0, INT_1);

        register.setTitle(TEST_TITlE);
        junit.framework.Assert.assertEquals(TEST_TITlE, register.getTitle());

        register.setTitleToken(TEST_TITlE_TOKEN);
        junit.framework.Assert.assertEquals(TEST_TITlE_TOKEN, register.getTitleToken());

        register.setTotalPatients(INT_10);
        junit.framework.Assert.assertEquals(INT_10, register.getTotalPatients());

        register.setTotalPatientsWithDueOverdue(INT_20);
        junit.framework.Assert.assertEquals(INT_20, register.getTotalPatientsWithDueOverdue());
    }
}