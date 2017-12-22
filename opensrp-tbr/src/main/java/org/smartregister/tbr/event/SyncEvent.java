package org.smartregister.tbr.event;

import org.smartregister.domain.FetchStatus;

/**
 * Created by samuelgithengi on 12/18/17.
 */

public class SyncEvent extends BaseEvent {

    public SyncEvent(FetchStatus fetchStatus) {
        this.fetchStatus = fetchStatus;
    }

    private FetchStatus fetchStatus;

    public FetchStatus getFetchStatus() {
        return fetchStatus;
    }
}


