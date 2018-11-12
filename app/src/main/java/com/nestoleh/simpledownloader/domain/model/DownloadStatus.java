package com.nestoleh.simpledownloader.domain.model;

import android.support.annotation.Nullable;

/**
 * Model for download status entity
 *
 * @author oleg.nestyuk
 */
public class DownloadStatus {
    private long id;
    private Status status;
    private int progress;
    @Nullable
    private String message;

    public DownloadStatus(long id, Status status, int progress, @Nullable String message) {
        this.id = id;
        this.status = status;
        this.progress = progress;
        this.message = message;
    }

    public DownloadStatus(long id, Status status, int progress) {
        this(id, status, progress, null);
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    public enum Status {
        STARTED, PROGRESS_CHANGED, SUCCESS, CANCELLED
    }
}