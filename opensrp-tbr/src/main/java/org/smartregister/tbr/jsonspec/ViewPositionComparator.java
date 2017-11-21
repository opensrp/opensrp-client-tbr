package org.smartregister.tbr.jsonspec;

import org.smartregister.tbr.jsonspec.model.View;

import java.util.Comparator;

/**
 * Created by samuelgithengi on 11/21/17.
 */

public class ViewPositionComparator implements Comparator<View> {

    @Override
    public int compare(org.smartregister.tbr.jsonspec.model.View v1, org.smartregister.tbr.jsonspec.model.View v2) {
        if (v1.getResidence() == null && v2.getResidence() == null)
            return 0;
        else if (v1.getResidence() == null && v2.getResidence() != null)
            return 1;
        else if (v1.getResidence() != null && v2.getResidence() == null)
            return -1;
        else
            return Integer.valueOf(v1.getResidence().getPosition()).compareTo(Integer.valueOf(v2.getResidence().getPosition()));
    }
}