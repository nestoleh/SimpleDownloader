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
    private double progress;
    @Nullable
    private String message;
    @Nullable
    private String filePath;


    public DownloadStatus(long id, Status status, double progress) {
        this(id, status, progress, null, null);
    }

    public DownloadStatus(long id, Status status, double progress, @Nullable String message) {
        this(id, status, progress, message, null);
    }

    public DownloadStatus(long id, Status status, double progress, @Nullable String message, @Nullable String filePath) {
        this.id = id;
        this.status = status;
        this.progress = progress;
        this.message = message;
        this.filePath = filePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Nullable
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(@Nullable String filePath) {
        this.filePath = filePath;
    }

    public enum Status {
        STARTED, PROGRESS_CHANGED, SUCCESS, CANCELLED
    }
}