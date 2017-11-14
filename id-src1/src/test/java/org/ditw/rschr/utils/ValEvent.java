package org.ditw.rschr.utils;

import com.lmax.disruptor.EventFactory;

/**
 * Created by dev on 2017-11-14.
 */
public class ValEvent {
    private long v;
    public final static EventFactory EVT_FACTORY = ValEvent::new;

    public long getv() {
        return v;
    }

    public void setv(long v) {
        this.v = v;
    }

}
