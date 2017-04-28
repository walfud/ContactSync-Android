package com.walfud.contactsync_android.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.walfud.contactsync_android.R;
import com.walfud.dustofappearance.DustOfAppearance;
import com.walfud.dustofappearance.annotation.FindView;
import com.walfud.dustofappearance.annotation.OnClick;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends Activity implements MainView {

    @FindView
    private TextView mUnuploadTv;
    @FindView
    private TextView mUndownloadTv;
    @FindView
    private TextView mUnsyncTv;
    @FindView
    private TextView mSyncBtn;
    @FindView
    private Spinner mSortSp;
    @FindView
    private RecyclerView mContactRv;

    @Inject
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DustOfAppearance.inject(this);
    }

    @OnClick
    public void onClickSyncBtn(View view) {
        mPresenter.onSync();
    }

    //

    @Override
    public void login() {

    }

    @Override
    public void show(boolean isLogin, List<ContactModel> dataList) {

    }

    @Override
    public void loading() {

    }
}
