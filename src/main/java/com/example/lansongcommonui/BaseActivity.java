package com.example.lansongcommonui;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lansongsdk.commonFree.R;

public abstract class BaseActivity extends Activity {
    public Activity mContext;
    private AlertDialog progressDialog;
    TextView tvProgress;

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
    }

    public TextView showProgressDialog() {
        if (this.tvProgress == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            View inflate = View.inflate(this, R.layout.dialog_loading, null);
            builder.setView(inflate);
            ProgressBar progressBar = (ProgressBar) inflate.findViewById(R.id.pb_loading);
            this.tvProgress = (TextView) inflate.findViewById(R.id.tv_hint);
            if (VERSION.SDK_INT >= 21) {
                progressBar.setIndeterminateTintList(ContextCompat.getColorStateList(this, R.color.blue));
            }
            this.tvProgress.setText("处理中...");
            this.progressDialog = builder.create();
            this.progressDialog.show();
        }
        return this.tvProgress;
    }

    public TextView showProgressDialog(String str) {
        if (this.tvProgress == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            View inflate = View.inflate(this, R.layout.dialog_loading, null);
            builder.setView(inflate);
            ProgressBar progressBar = (ProgressBar) inflate.findViewById(R.id.pb_loading);
            this.tvProgress = (TextView) inflate.findViewById(R.id.tv_hint);
            if (VERSION.SDK_INT >= 21) {
                progressBar.setIndeterminateTintList(ContextCompat.getColorStateList(this, R.color.blue));
            }
            this.tvProgress.setText(str);
            this.progressDialog = builder.create();
            this.progressDialog.show();
        }
        return this.tvProgress;
    }

    public void setProgressPercent(int i) {
        if (this.tvProgress != null) {
            TextView textView = this.tvProgress;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("处理进度: ");
            stringBuilder.append(i);
            stringBuilder.append(" %");
            textView.setText(stringBuilder.toString());
        }
    }

    public void setProgressPercent(String str, int i) {
        if (this.tvProgress != null) {
            TextView textView = this.tvProgress;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(" ");
            stringBuilder.append(i);
            stringBuilder.append(" %");
            textView.setText(stringBuilder.toString());
        }
    }

    public void closeProgressDialog() {
        try {
            if (this.progressDialog != null) {
                this.progressDialog.dismiss();
                this.progressDialog = null;
                this.tvProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
