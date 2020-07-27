package jp.nita.tapchord.Util

import android.app.AlertDialog
import android.content.Context

object Dialogs {
    fun dialogBuilder(context: Context, titleId: Int): AlertDialog.Builder {
        return AlertDialog.Builder(context).setTitle(context.getString(titleId))
    }

    fun dialogBuilder(context: Context, titleId: Int, messageId: Int): AlertDialog.Builder {
        return AlertDialog.Builder(context).setTitle(context.getString(titleId)).setMessage(context.getString(messageId))
    }
}