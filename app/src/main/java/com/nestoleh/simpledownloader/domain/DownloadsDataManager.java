package com.nestoleh.simpledownloader.domain;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import com.nestoleh.simpledownloader.domain.model.DownloadStatus;

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
    private BroadcastReceiver onDownloadCompleteReceiver;

    public DownloadsDataManager(@NonNull Context context) {
        this.context = context;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.downloadsArray = new LongSparseArray<>();
        initDownloadsListener();
    }

    private void initDownloadsListener() {
        onDownloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId != -1) {
                    FlowableEmitter<DownloadStatus> flowableEmitter = downloadsArray.get(downloadId);
                    if (flowableEmitter != null) {
                        flowableEmitter.onNext(new DownloadStatus(downloadId, DownloadStatus.Status.SUCCESS, 100, null));
                        flowableEmitter.onComplete();
                        downloadsArray.remove(downloadId);
                    }
                }
            }
        };
        context.registerReceiver(onDownloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public Flowable<DownloadStatus> downloadFile(@NonNull final String url,
                                                 @NonNull final String fileName,
                                                 @Nullable final String description) {

        return Flowable.create(emitter -> {
            Uri uri = Uri.parse(url);
            // file path with spaces doesn't working, don't know why
            String filePath = String.format("%s", fileName).replace(" ", "_");
            DownloadManager.Request request = new DownloadManager.Request(uri)
                    .setTitle(fileName)
                    .setDescription(description)
                    .setVisibleInDownloadsUi(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filePath);
            long downloadId = downloadManager.enqueue(request);
            downloadsArray.put(downloadId, emitter);
            DownloadStatus startStatus = new DownloadStatus(downloadId, DownloadStatus.Status.STARTED, 0, null);
            emitter.onNext(startStatus);
        }, BackpressureStrategy.BUFFER);
    }
}