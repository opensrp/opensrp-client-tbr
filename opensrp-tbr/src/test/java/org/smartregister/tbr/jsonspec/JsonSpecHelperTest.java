package org.smartregister.tbr.jsonspec;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.tbr.BaseUnitTest;

/**
 * Created by ndegwamartin on 17/10/2017.
 */

public class JsonSpecHelperTest extends BaseUnitTest {

    JsonSpecHelper jsonSpecHelper;

    @Before
    public void setUp() {
        jsonSpecHelper = new JsonSpecHelper(RuntimeEnvironment.application);
    }

    @Test(expected = IllegalStateException.class)
    public void instantiationWithDefaultConstructorThrowsIllegalArgumentException() {
        new JsonSpecHelper();
    }
}
