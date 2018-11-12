package com.nestoleh.simpledownloader.domain.model;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * File download configuration
 *
 * @author oleg.nestyuk
 */
public class FileDownloadConfig {
    private Uri uri;
    private String fileName;
    private String destinationDirectoryPath;
    @Nullable
    private String description;
    @Nullable
    private String title;
    @Nullable
    private List<Pair<String, String>> headers;
    private boolean showNotificationAfterDownload;

    private FileDownloadConfig(Uri uri, String fileName, String destinationDirectoryPath,
                               @Nullable String description, @Nullable String title,
                               @Nullable List<Pair<String, String>> headers, boolean showNotificationAfterDownload) {
        this.uri = uri;
        this.fileName = fileName;
        this.destinationDirectoryPath = destinationDirectoryPath;
        this.description = description;
        this.title = title;
        this.headers = headers;
        this.showNotificationAfterDownload = showNotificationAfterDownload;
    }

    public Uri getUri() {
        return uri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDestinationDirectoryPath() {
        return destinationDirectoryPath;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public List<Pair<String, String>> getHeaders() {
        return headers;
    }

    public boolean isShowNotificationAfterDownload() {
        return showNotificationAfterDownload;
    }

    public static class Builder {
        private Uri uri;
        private String fileName;
        private String destinationDirectoryPath = Environment.DIRECTORY_DOWNLOADS;
        private String description;
        private String title;
        private List<Pair<String, String>> headers;
        private boolean showNotificationAfterDownload = false;

        public Builder(Uri uri, String fileName) {
            this.uri = uri;
            this.fileName = fileName;
        }

        public Builder(String url, String fileName) {
            this(Uri.parse(url), fileName);
        }

        public Builder setUrl(String url) {
            this.uri = Uri.parse(url);
            return this;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setDestinationDirectoryPath(@NonNull String destinationDirectoryPath) {
            this.destinationDirectoryPath = destinationDirectoryPath;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setHeaders(List<Pair<String, String>> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (this.headers == null) {
                this.headers = new ArrayList<>();
            }
            this.headers.add(new Pair<>(key, value));
            return this;
        }

        public Builder setShowNotificationAfterDownload(boolean showNotificationAfterDownload) {
            this.showNotificationAfterDownload = showNotificationAfterDownload;
            return this;
        }

        public FileDownloadConfig build() {
            return new FileDownloadConfig(uri, fileName, destinationDirectoryPath, description, title, headers,
                    showNotificationAfterDownload);
        }
    }
}