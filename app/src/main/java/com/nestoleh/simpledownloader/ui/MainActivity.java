package com.nestoleh.simpledownloader.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nestoleh.simpledownloader.R;
import com.nestoleh.simpledownloader.domain.DownloadsDataManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private final int LAYOUT = R.layout.activity_main;

    @BindView(R.id.urlEditText)
    EditText urlEditText;
    @BindView(R.id.fileName)
    TextView fileName;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fileLoadingWrapper)
    LinearLayout fileLoadingWrapper;

    private DownloadsDataManager downloadsDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);
        downloadsDataManager = new DownloadsDataManager(this);
    }

    @OnClick(R.id.downloadButton)
    public void onDownloadClick() {
        String url = urlEditText.getText().toString();
        // TODO: download file
        showMessage("TODO: download file");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}