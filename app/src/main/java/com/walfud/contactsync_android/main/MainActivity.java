package com.walfud.contactsync_android.main;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.walfud.contactsync_android.BaseActivity;
import com.walfud.contactsync_android.ContactSyncApplication;
import com.walfud.contactsync_android.R;
import com.walfud.contactsync_android.ui.OkCancelDialog;
import com.walfud.dustofappearance.DustOfAppearance;
import com.walfud.dustofappearance.annotation.FindView;
import com.walfud.dustofappearance.annotation.OnClick;

import java.util.ArrayList;
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
    private Adapter mAdapter;

    @Inject
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DustOfAppearance.inject(this);

        mPresenter = new MainPresenterImpl(this, ContactSyncApplication.userService, ContactSyncApplication.networkService);
        mContactRv.setLayoutManager(new LinearLayoutManager(this));
        mContactRv.setAdapter(mAdapter = new Adapter());

        mContactRv.post(() -> mPresenter.onRefresh());
    }

    @OnClick
    public void onClickSyncBtn(View view) {
        if (!ContactSyncApplication.userService.isLogin()) {
            login();
        } else {
            mPresenter.onSync();
        }
    }

    //

    @Override
    public void login() {
        // TODO: oauth2
    }

    @Override
    public void show(List<ViewContactData> dataList) {
        mAdapter.setData(dataList);
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

    //
    static class ViewHolder extends RecyclerView.ViewHolder {

        @FindView
        public ImageView mIv;
        @FindView
        public TextView mNameTv;
        @FindView
        public TextView mPhoneTv;

        public ViewHolder(View itemView) {
            super(itemView);
            DustOfAppearance.inject(this, itemView);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private List<ViewContactData> mDataList = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_contact, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ViewContactData viewContactData = mDataList.get(position);
            // TODO: holder.mIv
            holder.mIv.setImageResource(R.mipmap.ic_launcher_round);
            holder.mNameTv.setText(viewContactData.name);
            holder.mPhoneTv.setText(viewContactData.phoneList.stream().reduce((s, s2) -> s + "/" + s2).orElse(null));
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        //
        public void setData(List<ViewContactData> dataList) {
            mDataList = dataList;
            notifyDataSetChanged();
        }
    }
}
