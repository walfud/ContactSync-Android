package com.walfud.contactsync_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Spinner;
import android.widget.TextView;

import com.walfud.dustofappearance.DustOfAppearance;
import com.walfud.dustofappearance.annotation.FindView;

public class MainActivity extends Activity {

    @FindView
    TextView mUnuploadTv;
    @FindView
    TextView mUndownloadTv;
    @FindView
    TextView mUnsyncTv;
    @FindView
    TextView mSyncBtn;
    @FindView
    Spinner mSortSp;
    @FindView
    RecyclerView mContactRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DustOfAppearance.inject(this);
    }
}
