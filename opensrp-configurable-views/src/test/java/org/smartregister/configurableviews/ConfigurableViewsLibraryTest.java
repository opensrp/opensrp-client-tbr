package org.smartregister.configurableviews;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.Context;
import org.smartregister.repository.Repository;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by ndegwamartin on 05/02/2018.
 */

public class ConfigurableViewsLibraryTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private Repository repository;

    private Context context = Context.getInstance();

    @Test
    public void callingGetInstanceOfConfigurableViewsLibraryDoesNotReturnNull() {
        ConfigurableViewsLibrary.init(context, repository);
        ConfigurableViewsLibrary library = ConfigurableViewsLibrary.getInstance();
        assertNotNull(library);

    }

    @Test(expected = IllegalStateException.class)
    public void callingGetInstanceOfConfigurableViewsLibraryWithoutInitFirstThrowsIllegalStateException() {
        ConfigurableViewsLibrary library = ConfigurableViewsLibrary.getInstance();
        assertNull(library);

    }
}
