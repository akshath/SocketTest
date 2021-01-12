package net.sf.sockettest.callbacks;

import net.sf.sockettest.vo.KeepAliveConfig;

/**
 * Created by Srikanth on 2/23/2016.
 */
public interface OnConfigFetch {
    void onRetrieveCOnfig(KeepAliveConfig config);
}
