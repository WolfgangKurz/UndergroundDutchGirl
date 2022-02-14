package com.swaytwig.undergrounddutchgirl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.ListFragment
import com.swaytwig.undergrounddutchgirl.TextureData.TextureData
import com.swaytwig.undergrounddutchgirl.TextureData.TextureDataManager
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextureFragment : ListFragment(), TextureAdapter.ListSwitchChangedListener {
    private val list = ArrayList<TextureData>()
    private var arr = arrayOf<TextureData>()

    private val client = HttpClient(CIO)

    private suspend fun downloadFile(url: String) = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val ret = client.request<HttpResponse>(url) { method = HttpMethod.Get }
            if (ret.status == HttpStatusCode.OK)
                return@withContext ret.readBytes()
        }
        null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_texture, container, false)

        CoroutineScope(Dispatchers.Main).launch {
            val ctx = requireActivity()

            val loading = v.findViewById<ProgressBar>(R.id.list_loading)
            val fetchError = v.findViewById<TextView>(R.id.fetch_error_text)

            if (arr.isEmpty()) {
                val textures = TextureDataManager.getTextureList()
                if (textures == null) {
                    fetchError.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                    return@launch
                }

                for (item in textures) {
                    val key = item.getKey()
                    val hash = item.getHashOneStore()

//                    if (key != "2dmodel_br_thetis_n") continue // Test

                    val dir = "com.smartjoy.LastOrigin_C/files/UnityCache/Shared/$key/$hash"
                    val path = "$dir/__data"
                    val original = "$dir/__original"

                    if (!MFile.isExists(ctx, path)) continue // Source not found

                    val useOneStore = !MFile.isExists(ctx, original)
                    list.add(
                        TextureData(
                            item.getKey(),
                            item.getKey(),
                            item.getHashOneStore(),
                            useOneStore
                        )
                    )
                }

                arr = list.toArray(arrayOfNulls<TextureData>(list.size))
            }

            val btnPatch = v.findViewById<Button>(R.id.patch)
            btnPatch.isEnabled = true
            btnPatch.setOnClickListener { doPatch(btnPatch) }

            val listTextures = v.findViewById<ListView>(android.R.id.list)
            listTextures.adapter = TextureAdapter(ctx, arr, this@TextureFragment)
            listTextures.visibility = View.VISIBLE

            loading.visibility = View.GONE
        }

        return v
    }

    override fun onListSwitchChanged(position: Int, checked: Boolean) {
        // Placeholder
    }

    private fun doPatch(button: Button) {
        button.isEnabled = false

        val ctx = requireContext()
        CoroutineScope(Dispatchers.Main).launch {
            val ver = TextureDataManager.getDataVersion()

            var failed = 0
            var total = 0

            for (item in list) {
                val key = item.key
                val hash = item.hash

                val dir = "com.smartjoy.LastOrigin_C/files/UnityCache/Shared/$key/$hash"
                if (item.useOneStore) { // patched -> original
                    val original = "$dir/__original"
                    if (!MFile.isExists(ctx, original)) // not patched
                        continue

                    total++

                    MFile.copy(ctx, "$dir/__original", "$dir/__data")
                    MFile.delete(ctx, "$dir/__original")
                } else { // original -> patched
                    val original = "$dir/__original"
                    if (MFile.isExists(ctx, original)) // already patched
                        continue

                    MFile.copy(ctx, "$dir/__data", "$dir/__original")

                    total++
                    val url = "https://udg.swaytwig.com/db/$ver/$key/__data"
                    val bytes = downloadFile(url)
                    if (bytes == null) {
                        failed++
                        continue
                    }

                    MFile.delete(ctx, "$dir/__data")
                    MFile.write(ctx, "$dir/__data", bytes)
                }
            }

            if (failed > 0) {
                val template = ctx.resources.getString(R.string.patch_failed)
                val str = String.format(template, "$failed/$total")
                Toast.makeText(ctx, str, Toast.LENGTH_SHORT)
                    .show()
            } else
                Toast.makeText(ctx, R.string.patch_done, Toast.LENGTH_SHORT).show()

            button.isEnabled = true
        }
    }
}