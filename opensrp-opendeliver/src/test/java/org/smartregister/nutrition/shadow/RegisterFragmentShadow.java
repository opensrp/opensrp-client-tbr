package org.smartregister.nutrition.shadow;

import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.nutrition.fragment.HomeFragment;

/**
 * Created by ndegwamartin on 13/11/2017.
 */

@Implements(HomeFragment.class)
public class RegisterFragmentShadow extends Shadow {
}
