package com.walfud.contactsync_android.main;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walfud.contactsync_android.BaseActivity;
import com.walfud.contactsync_android.ContactSyncApplication;
import com.walfud.contactsync_android.R;
import com.walfud.contactsync_android.ui.OkCancelDialog;
import com.walfud.dustofappearance.DustOfAppearance;
import com.walfud.dustofappearance.annotation.FindView;
import com.walfud.dustofappearance.annotation.OnClick;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainView {

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

        mPresenter = new MainPresenterImpl(this, ContactSyncApplication.userService, ContactSyncApplication.networkService);
    }

    @OnClick
    public void onClickSyncBtn(View view) {
        mPresenter.onSync();
    }

    //

    @Override
    public void login() {
        Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void show(boolean isLogin, List<ContactModel> dataList) {

    }

    private DialogFragment mLoadingDialog = new OkCancelDialog();
    @Override
    public void loading(boolean show) {
        if (show) {
            mLoadingDialog.show(getFragmentManager(), null);
        } else {
            mLoadingDialog.dismiss();
        }
    }
}
