package com.walfud.contactsync_android.main;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walfud.contactsync_android.BaseActivity;
import com.walfud.contactsync_android.ContactSyncApplication;
import com.walfud.contactsync_android.R;
import com.walfud.contactsync_android.model.ContactRealm;
import com.walfud.contactsync_android.service.contact.ContactModel;
import com.walfud.contactsync_android.service.contact.ContactService;
import com.walfud.contactsync_android.ui.OkCancelDialog;
import com.walfud.dustofappearance.DustOfAppearance;
import com.walfud.dustofappearance.annotation.FindView;
import com.walfud.dustofappearance.annotation.OnClick;
import com.walfud.walle.collection.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;

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

        findViewById(R.id.btn_sync).setOnLongClickListener(v -> {
            for (int i = 0; i < 100; i++) {
                getContentResolver().delete(Uri.parse("content://com.android.contacts/contacts/" + i), null, null);
                getContentResolver().delete(Uri.parse("content://com.android.contacts/raw_contacts/" + i), null, null);
                getContentResolver().delete(Uri.parse("content://com.android.contacts/data/" + i), null, null);
            }
            ContactService.insert(this, "a", CollectionUtils.newArrayList("1"));
            ContactService.insert(this, "a2", CollectionUtils.newArrayList("1222"));
            ContactService.insert(this, "b", CollectionUtils.newArrayList("2", "22"));
            ContactService.insert(this, "c", CollectionUtils.newArrayList("3"));
            ContactService.insert(this, "y", CollectionUtils.newArrayList("8", "88"));
            ContactService.insert(this, "z", CollectionUtils.newArrayList("9"));
            List<ContactModel> z = ContactService.getContactList(MainActivity.this);

            ContactService.delete(this, z.get(3).id);


            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.deleteAll();
            realm.createAllFromJson(ContactRealm.class, "[\n" +
                    "    {\n" +
                    "        \"id\": \"b\",\n" +
                    "        \"localId\": " + z.get(2).id + ",\n" +
                    "        \"name\": \"b\",\n" +
                    "        \"phoneRealmList\": [\n" +
                    "            {\n" +
                    "                \"num\": 2\n" +
                    "            },\n" +
                    "            {\n" +
                    "                \"num\": 2222\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"modifyTime\": 1494474854000,\n" +
                    "        \"isDeleted\": false\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\": \"c\",\n" +
                    "        \"localId\": " + z.get(3).id + ",\n" +
                    "        \"name\": \"c\",\n" +
                    "        \"phoneRealmList\": [\n" +
                    "            {\n" +
                    "                \"num\": 3\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"modifyTime\": 1494474854000,\n" +
                    "        \"isDeleted\": true\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\": \"y\",\n" +
                    "        \"localId\": " + z.get(4).id + ",\n" +
                    "        \"name\": \"y\",\n" +
                    "        \"phoneRealmList\": [\n" +
                    "            {\n" +
                    "                \"num\": 8\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"modifyTime\": 1494474854000,\n" +
                    "        \"isDeleted\": false\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\": \"z\",\n" +
                    "        \"localId\": " + z.get(5).id + ",\n" +
                    "        \"name\": \"z\",\n" +
                    "        \"phoneRealmList\": [\n" +
                    "            {\n" +
                    "                \"num\": 9\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"modifyTime\": 1494474854000,\n" +
                    "        \"isDeleted\": false\n" +
                    "    }\n" +
                    "]");
            realm.commitTransaction();

            Toast.makeText(this, "reset", Toast.LENGTH_SHORT).show();

            return true;
        });
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

    @Override
    public void error(String err) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
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
