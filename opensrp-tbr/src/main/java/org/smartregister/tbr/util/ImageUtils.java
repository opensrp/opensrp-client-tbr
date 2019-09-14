package org.smartregister.tbr.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.tbr.R;


/**
 * Created by keyman on 22/02/2017.
 */
public class ImageUtils {

    public static int profileImageResourceByGender(String gender) {
        if (StringUtils.isNotBlank(gender)) {
            if (gender.equalsIgnoreCase("male")) {
                return R.drawable.child_boy_infant;
            } else if (gender.equalsIgnoreCase("female")) {
                return R.drawable.child_girl_infant;
            }
        }
        return R.drawable.child_boy_infant;
    }


}
