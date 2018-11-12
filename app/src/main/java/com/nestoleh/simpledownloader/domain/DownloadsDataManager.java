package com.nestoleh.simpledownloader.domain;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Data manager to simplify files downloading
 *
 * @author oleg.nestyuk
 */
public class DownloadsDataManager {
    private Context context;

    public DownloadsDataManager(@NonNull Context context) {
        this.context = context;
    }
}
