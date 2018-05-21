package org.smartregister.configurableviews;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by ndegwamartin on 15/02/2018.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public abstract class BaseUnitTest {
}
