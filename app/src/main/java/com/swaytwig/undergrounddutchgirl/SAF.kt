package com.swaytwig.undergrounddutchgirl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.core.content.FileProvider
import org.w3c.dom.Document
import java.io.File


class SAF {
    companion object {
        fun createRequestIntent(context: Context, path: String?): Intent {
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

                if (path != null && !path.isNullOrEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val uri = DocumentsContract.buildDocumentUri(
                            "com.android.externalstorage.documents",
                            "primary:Android/data/"
                        )
                        android.util.Log.e("URI", uri.toString())
                        putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
                    }
                }
            }

            return intent
        }
    }
}