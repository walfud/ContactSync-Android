package com.walfud.contactsync_android.main

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import com.walfud.contactsync_android.BaseActivity
import com.walfud.contactsync_android.R
import com.walfud.contactsync_android.appContext
import com.walfud.contactsync_android.main.MainContract.MainPresenter
import com.walfud.contactsync_android.main.MainContract.MainView
import com.walfud.contactsync_android.service.user.UserService
import com.walfud.dustofappearance.DustOfAppearance
import com.walfud.dustofappearance.annotation.FindView
import com.walfud.oauth2_android_lib.*
import com.walfud.walle.android.contact.ContactUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainView {

    lateinit var mPresenter: MainPresenter
    lateinit private var mAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPresenter = MainPresenterImpl(this)
        contactRv.layoutManager = LinearLayoutManager(this)
        mAdapter = Adapter()
        contactRv.adapter = mAdapter

        RxPermissions(this).request(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
                .subscribe({ granted ->
                    if (granted) {
                        //
                    } else {
                        Toast.makeText(this@MainActivity, "...", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                })

        // DEBUG
        downloadBtn.setOnLongClickListener {
            UserService.changeUser("")
            ContactUtils.getContactList(appContext).forEach {
                ContactUtils.delete(appContext, it.id)
            }
            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_OAUTH2 -> {
                loading(false)

                if (resultCode == Activity.RESULT_OK) {
                    val oid = data.getStringExtra(EXTRA_OID)
                    val accessToken = data.getStringExtra(EXTRA_ACCESS_TOKEN)
                    val refreshToken = data.getStringExtra(EXTRA_REFRESH_TOKEN)
                    mPresenter.onLogin(oid, accessToken, refreshToken)
                } else {
                    val err = data.getStringExtra(EXTRA_ERROR)
                    Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onClickDownloadBtn(view: View) {
        if (ensureLogin()) {
            mPresenter.onDownload()
        }
    }

    fun onClickUploadBtn(view: View) {
        if (ensureLogin()) {
            mPresenter.onUpload()
        }
    }

    //
    override fun show(dataList: List<MainView.ViewContactData>) {
        mAdapter.setData(dataList)
        contactRv.scrollToPosition(dataList.size)
    }

    val mLoadingDialog = lazy { ProgressDialog(this) }

    override fun loading(show: Boolean) {
        if (show) {
            mLoadingDialog.value.show()
        } else {
            mLoadingDialog.value.dismiss()
        }
    }

    override fun error(err: String) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
    }

    fun ensureLogin(): Boolean {
        if (!UserService.isLogin) {
            startOAuth2ForResult(this, REQUEST_OAUTH2, "contactsync")
            loading(true)

            return false
        }
        return true
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
        private var mDataList: List<MainView.ViewContactData> = listOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_contact, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val viewContactData = mDataList[position]
            // TODO: holder.mIv
            when (viewContactData.status) {
                MainView.ViewContactData.STATUS_LOCAL_ONLY -> {
                    holder.mIv!!.setImageResource(R.mipmap.ic_launcher_round)
                }
                MainView.ViewContactData.STATUS_REMOTE_ONLY -> {
                    holder.mIv!!.setImageResource(R.mipmap.ic_launcher)
                }
            }
            holder.mNameTv!!.text = viewContactData.name
            holder.mPhoneTv!!.text = viewContactData.phoneList.joinToString("/")
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
