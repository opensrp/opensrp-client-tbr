package org.smartregister.tbr.jsonspec;

import org.junit.Test;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.tbr.BaseUnitTest;

/**
 * Created by ndegwamartin on 17/10/2017.
 */

public class JsonSpecHelperTest extends BaseUnitTest {

    @Test(expected = IllegalStateException.class)
    public void instantiationWithDefaultConstructorThrowsIllegalArgumentException() {
        new JsonSpecHelper();
    }
}
