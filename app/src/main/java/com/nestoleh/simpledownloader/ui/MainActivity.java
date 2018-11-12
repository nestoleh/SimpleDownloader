package com.nestoleh.simpledownloader.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nestoleh.simpledownloader.R;
import com.nestoleh.simpledownloader.domain.DownloadsDataManager;
import com.nestoleh.simpledownloader.domain.model.FileDownloadConfig;
import com.nestoleh.simpledownloader.domain.model.DownloadStatus;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {
    private final int LAYOUT = R.layout.activity_main;
    private final int PROGRESS_COEFFICIENT = 100; // used to more smooth loading

    @BindView(R.id.urlEditText)
    EditText urlEditText;
    @BindView(R.id.fileNameEditText)
    EditText fileNameEditText;
    @BindView(R.id.fileName)
    TextView fileName;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fileLoadingWrapper)
    LinearLayout fileLoadingWrapper;
    @BindView(R.id.downloadButton)
    Button downloadButton;
    @BindView(R.id.progress)
    TextView progress;

    private DownloadsDataManager downloadsDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);
        downloadsDataManager = new DownloadsDataManager(this);
        initUI();
    }

    private void initUI() {
        progressBar.setMax(100 * PROGRESS_COEFFICIENT);
    }

    @OnClick(R.id.downloadButton)
    public void onDownloadClick() {
        Disposable d = new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isGranted -> {
                            if (isGranted) {
                                downloadFile();
                            } else {
                                showMessage("Permission not granted, action disabled");
                            }
                        }
                );
    }

    private void downloadFile() {
        String url = urlEditText.getText().toString();
        String fileNameValue = fileNameEditText.getText().toString();
        FileDownloadConfig fileDownloadConfig = new FileDownloadConfig.Builder(url, fileNameValue)
                .setShowNotificationAfterDownload(true)
                .build();
        downloadsDataManager.downloadFile(fileDownloadConfig)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSubscriber<DownloadStatus>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        downloadButton.setEnabled(false);
                        fileName.setText(fileNameValue);
                        fileLoadingWrapper.setVisibility(View.VISIBLE);
                        progressBar.setIndeterminate(true);
                    }

                    @Override
                    public void onNext(DownloadStatus downloadStatus) {
                        double progress = downloadStatus.getProgress();
                        switch (downloadStatus.getStatus()) {
                            case STARTED:
                                progressBar.setIndeterminate(true);
                                setDownloadProgress(progress);
                                break;
                            case PROGRESS_CHANGED:
                                progressBar.setIndeterminate(false);
                                setDownloadProgress(progress);
                                break;
                            case SUCCESS:
                                progressBar.setIndeterminate(false);
                                setDownloadProgress(progress);
                                showMessage("File " + downloadStatus.getFilePath() + " successfully loaded");
                                break;
                            case CANCELLED:
                                progressBar.setIndeterminate(false);
                                setDownloadProgress(progress);
                                showMessage("File loading cancelled");
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        downloadButton.setEnabled(true);
                        Timber.e(t);
                        showMessage("Error happened when try to download file. " + t.getLocalizedMessage());
                        fileLoadingWrapper.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        downloadButton.setEnabled(true);
                        fileLoadingWrapper.setVisibility(View.GONE);
                    }
                });
    }

    private void setDownloadProgress(double progressValue) {
        progress.setText(String.valueOf(((int) progressValue)));
        int progressBarProgressValue = (int) (progressValue * PROGRESS_COEFFICIENT);
        progressBar.setProgress(progressBarProgressValue);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}