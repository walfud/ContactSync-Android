package com.walfud.contactsync_android.main

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.walfud.contactsync_android.BaseActivity
import com.walfud.contactsync_android.ContactSyncApplication
import com.walfud.contactsync_android.R
import com.walfud.contactsync_android.ui.OkCancelDialog
import com.walfud.dustofappearance.DustOfAppearance
import com.walfud.dustofappearance.annotation.FindView
import com.walfud.dustofappearance.annotation.OnClick
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*

class MainActivity : BaseActivity(), MainView {

    @FindView
    private val mUnuploadTv: TextView? = null
    @FindView
    private val mUndownloadTv: TextView? = null
    @FindView
    private val mUnsyncTv: TextView? = null
    @FindView
    private val mSyncBtn: TextView? = null
    @FindView
    private val mSortSp: Spinner? = null
    @FindView
    private val mContactRv: RecyclerView? = null
    private var mAdapter: Adapter? = null

//    @Inject
    private var mPresenter: MainPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DustOfAppearance.inject(this)

        mPresenter = MainPresenterImpl(this, ContactSyncApplication.userService!!, ContactSyncApplication.networkService!!)
        mContactRv!!.layoutManager = LinearLayoutManager(this)
        mAdapter = Adapter()
        mContactRv!!.adapter = mAdapter

        mContactRv!!.post { mPresenter!!.onRefresh() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_OAUTH2 -> if (resultCode == Activity.RESULT_OK) {
                val oid = data.getStringExtra("EXTRA_OID")
                val accessToken = data.getStringExtra("EXTRA_ACCESS_TOKEN")
                val refreshToken = data.getStringExtra("EXTRA_REFRESH_TOKEN")
                mPresenter!!.onLogin(oid, accessToken, refreshToken)
            } else {
                val err = data.getStringExtra("EXTRA_ERROR")
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OnClick
    fun onClickSyncBtn(view: View) {
        if (!ContactSyncApplication.userService!!.isLogin) {
            val intent = Intent()
            intent.component = ComponentName("com.walfud.oauth2_android", "com.walfud.oauth2_android.MainActivity")
            intent.putExtra("EXTRA_CLIENT_ID", "contactsync")
            startActivityForResult(intent, REQUEST_OAUTH2)
        } else {
            launch(UI) {
                async(CommonPool) {
                    mPresenter!!.onSync()
                }
            }
        }
    }

    //

    override fun show(dataList: List<MainView.ViewContactData>) {
        mAdapter!!.setData(dataList)
    }

    private val mLoadingDialog = OkCancelDialog()

    override fun loading(show: Boolean) {
        if (show) {
            mLoadingDialog.show(fragmentManager, null)
        } else {
            mLoadingDialog.dismiss()
        }
    }

    override fun error(err: String) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
    }

    //
    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @FindView
        var mIv: ImageView? = null
        @FindView
        var mNameTv: TextView? = null
        @FindView
        var mPhoneTv: TextView? = null

        init {
            DustOfAppearance.inject(this, itemView)
        }
    }

    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        private var mDataList: List<MainView.ViewContactData> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_contact, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val viewContactData = mDataList[position]
            // TODO: holder.mIv
            holder.mIv!!.setImageResource(R.mipmap.ic_launcher_round)
            holder.mNameTv!!.text = viewContactData.name
            holder.mPhoneTv!!.text = viewContactData.phoneList?.reduce { s, s2 -> s + "/" + s2 }
        }

        override fun getItemCount(): Int {
            return mDataList.size
        }

        //
        fun setData(dataList: List<MainView.ViewContactData>) {
            mDataList = dataList
            notifyDataSetChanged()
        }
    }

    companion object {

        private val REQUEST_OAUTH2 = 1
    }
}
