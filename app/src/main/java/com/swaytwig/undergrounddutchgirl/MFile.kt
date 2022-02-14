package com.swaytwig.undergrounddutchgirl

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile


class MFile {
    companion object {
        private val treeUri = DocumentsContract.buildTreeDocumentUri(
            "com.android.externalstorage.documents",
            "primary:Android/data"
        )

        fun checkGranted(context: Context): Boolean =
            context.contentResolver.persistedUriPermissions.any { it.uri.equals(treeUri) && it.isReadPermission && it.isWritePermission }

        fun createRequestIntent(): Intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, treeUri)
            }

        fun isExists(context: Context, path: String): Boolean {
            val file = find(context, path)
            return file?.exists() ?: false
        }

        fun find(context: Context, path: String): DocumentFile? {
            val uri =
                DocumentsContract.buildDocumentUriUsingTree(treeUri, "primary:Android/data/$path")

            return DocumentFile.fromSingleUri(context, uri)
        }

        fun copy(context: Context, from: String, to: String): Boolean {
            val content = read(context, from) ?: return false
            if (isExists(context, to)) delete(context, to)
            return write(context, to, content)
        }

        fun write(context: Context, path: String, content: ByteArray): Boolean {
            var file = find(context, path) ?: return false
            if (!file.exists()) {
                val fileName = path.substringAfterLast('/')
                val uri = DocumentsContract.createDocument(
                    context.contentResolver, DocumentsContract.buildDocumentUriUsingTree(
                        treeUri,
                        "primary:Android/data/${path.substringBeforeLast('/')}"
                    ), "application/octet-stream", fileName
                ) ?: return false

                file = DocumentFile.fromSingleUri(context, uri) ?: return false
            }

            if (!file.canWrite()) return false

            val resolver = context.contentResolver
            val stream = resolver.openOutputStream(file.uri) ?: return false
            stream.write(content)
            stream.flush()
            stream.close()

            return true
        }

        fun read(context: Context, path: String): ByteArray? {
            val file = find(context, path)
            if (file?.canRead() != true) return null

            val resolver = context.contentResolver
            val stream = resolver.openInputStream(file.uri) ?: return null
            val ret = stream.readBytes()
            stream.close()

            return ret
        }

        fun delete(context: Context, path: String): Boolean {
            val file = find(context, path) ?: return false
            return file.delete()
        }
    }
}