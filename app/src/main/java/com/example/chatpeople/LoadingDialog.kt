package com.example.chatpeople

import android.app.AlertDialog
import android.app.Dialog

class LoadingDialog(val mActivity: LatestMessagesActivity) {
    private lateinit var isDialog: Dialog;
    fun startLoading()
    {
        val inflater=mActivity.layoutInflater.inflate(R.layout.activity_progress_bar,null)
        val builder=AlertDialog.Builder(mActivity);
builder.setView(inflater)
        builder.setCancelable(false);
        isDialog=builder.create();
        isDialog.show();
    }
    fun Dismiss()
    {
       isDialog.dismiss();
    }

}