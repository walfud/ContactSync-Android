package com.walfud.contactsync_android.ui

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

/**
 * Created by walfud on 2017/5/6.
 */

class OkCancelDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        return AlertDialog.Builder(activity)
                .create()
    }
}
