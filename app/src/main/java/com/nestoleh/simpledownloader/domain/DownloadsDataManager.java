package com.nestoleh.simpledownloader.domain;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.util.Pair;

import com.nestoleh.simpledownloader.domain.model.FileDownloadConfig;
import com.nestoleh.simpledownloader.domain.model.DownloadStatus;
import com.nestoleh.simpledownloader.exceptions.FileDownloadException;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;

/**
 * Data manager to simplify files downloading
 *
 * @author oleg.nestyuk
 */
public class DownloadsDataManager {
    private Context context;
    private DownloadManager downloadManager;

    private LongSparseArray<FlowableEmitter<DownloadStatus>> downloadsArray;

    public DownloadsDataManager(@NonNull Context context) {
        this.context = context;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.downloadsArray = new LongSparseArray<>();
    }

    public Flowable<DownloadStatus> downloadFile(@NonNull FileDownloadConfig fileDownloadConfig) {
        return Flowable.create(emitter -> {
            DownloadManager.Request request = generateDownloadRequest(fileDownloadConfig);
            long downloadId = downloadManager.enqueue(request);
            downloadsArray.put(downloadId, emitter);
            registerContentResolverForDownload(downloadId);
            DownloadStatus startStatus = new DownloadStatus(downloadId, DownloadStatus.Status.STARTED, 0, null);
            emitter.onNext(startStatus);
        }, BackpressureStrategy.BUFFER);
    }

    private void registerContentResolverForDownload(long downloadId) {
        Uri myDownloads = Uri.parse("content://downloads/my_downloads/" + downloadId);
        HandlerThread handlerThread = new HandlerThread("DownloadFileBackgroundThread");
        if (!handlerThread.isAlive()) {
            handlerThread.start();
        }
        Handler handler = new Handler(handlerThread.getLooper());
        context.getContentResolver().registerContentObserver(myDownloads, true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                FlowableEmitter<DownloadStatus> downloadEmitter = downloadsArray.get(downloadId);
                if (downloadEmitter != null) {
                    DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        switch (status) {
                            case DownloadManager.STATUS_FAILED:
                                onDownloadError(downloadEmitter, cursor);
                                break;
                            case DownloadManager.STATUS_SUCCESSFUL:
                                onDownloadSuccess(downloadEmitter, cursor);
                                break;
                            default:
                                onDownloadProgressChanged(downloadEmitter, cursor);
                                break;
                        }
                    }
                    cursor.close();
                } else {
                    unregisterThis();
                }
            }

            private void onDownloadProgressChanged(FlowableEmitter<DownloadStatus> downloadEmitter, Cursor cursor) {
                long size = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                long downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                double progress = 0.0;
                if (size != -1) {
                    progress = downloaded * 100.0 / size;
                }
                downloadEmitter.onNext(new DownloadStatus(downloadId, DownloadStatus.Status.PROGRESS_CHANGED, progress,
                        null));
            }

            private void onDownloadSuccess(FlowableEmitter<DownloadStatus> downloadEmitter, Cursor cursor) {
                String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                DownloadStatus successDownloadStatus = new DownloadStatus(downloadId, DownloadStatus.Status.SUCCESS,
                        100, null, filePath);
                downloadEmitter.onNext(successDownloadStatus);
                downloadEmitter.onComplete();
                downloadsArray.remove(downloadId);
                unregisterThis();
            }

            private void onDownloadError(FlowableEmitter<DownloadStatus> downloadEmitter, Cursor cursor) {
                String failedReason = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                downloadEmitter.onError(new FileDownloadException(failedReason));
                downloadsArray.remove(downloadId);
                unregisterThis();
            }

            private void unregisterThis() {
                context.getContentResolver().unregisterContentObserver(this);
            }
        });
    }

    private DownloadManager.Request generateDownloadRequest(FileDownloadConfig config) {
        DownloadManager.Request request = new DownloadManager.Request(config.getUri())
                .setTitle(config.getTitle())
                .setDescription(config.getTitle())
                .setVisibleInDownloadsUi(true)
                .setNotificationVisibility(config.isShowNotificationAfterDownload()
                        ? DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                        : DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(config.getDestinationDirectoryPath(), config.getFileName());
        List<Pair<String, String>> headers = config.getHeaders();
        if (headers != null && headers.size() > 0) {
            for (Pair<String, String> header : headers) {
                request.addRequestHeader(header.first, header.second);
            }
        }
        return request;
    }
}