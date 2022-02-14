package com.swaytwig.undergrounddutchgirl

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import com.swaytwig.undergrounddutchgirl.TextureData.TextureData
import com.swaytwig.undergrounddutchgirl.TextureData.TextureDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class TextureFragment : ListFragment(), TextureAdapter.ListSwitchChangedListener {
    private val list = ArrayList<TextureData>()
    private var arr = arrayOf<TextureData>()

    private val prefTextureSetKey = "pref_texture_set"
    private val prefTextureSet: SharedPreferences by lazy {
        requireContext().getSharedPreferences("TEXTURE_SET", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_texture, container, false)

        val ctx = requireActivity()

//        val pref = prefTextureSet.getString(prefTextureSetKey, null)
//        val textureSet = pref?.split("\n")?.associate {
//            val kv = it.split('=').toTypedArray()
//            kv[0] to kv[1]
//        }
//            ?: mapOf()

        val baseDir = Game.gameAssetDir(requireContext())

        TextureDataManager.getTextureList {
            for (item in it) {
                val key = item.getKey()
                val hash = item.getHashOneStore()

                val file = File("${baseDir}/${key}/${hash}/__data")
                if (!file.exists()) continue

                val original = File("${baseDir}/${key}/${hash}/__original")
                val useOneStore = !original.exists()
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

            val btnPatch = v.findViewById<Button>(R.id.patch)
            btnPatch.isEnabled = true
            btnPatch.setOnClickListener { doPatch(btnPatch) }

            val listTextures = v.findViewById<ListView>(android.R.id.list)
            listTextures.adapter = TextureAdapter(ctx, arr, this)
            listTextures.visibility = View.VISIBLE

            val loading = v.findViewById<ProgressBar>(R.id.list_loading)
            loading.visibility = View.GONE
        }

        return v
    }

    override fun onListSwitchChanged(position: Int, checked: Boolean) {
        val item = list[position]

        val pref = prefTextureSet.getString(prefTextureSetKey, null)
        val textureSet = (pref?.split("\n")?.associate {
            val kv = it.split('=').toTypedArray()
            kv[0] to kv[1]
        }
            ?: mapOf()).toMutableMap()

        textureSet[item.key] = if (checked) "1" else "0"
        val out = textureSet.toList().joinToString("\n") { "${it.first}=${it.second}" }

        prefTextureSet.edit().putString(prefTextureSetKey, out).apply()
    }

    private fun doPatch(button: Button) {
        button.isEnabled = false

        val baseDir = Game.gameAssetDir(requireContext())

        TextureDataManager.getDataVersion {
            GlobalScope.launch(Dispatchers.IO) {
                val ver = it

                for (item in list) {
                    val key = item.key
                    val hash = item.hash

                    val dir = "${baseDir}/${key}/${hash}"
                    if (item.useOneStore) {
                        val original = File("${dir}/__original")
                        if (!original.exists())
                            continue

                        val target = File("${dir}/__data")
                        original.copyTo(target, true)
                        original.delete()
                    } else {
                        val original = File("${dir}/__original")
                        if (original.exists())
                            continue

                        val target = File("${dir}/__data")
                        target.copyTo(original, true)

                        val url = URL("https://live-lastorigin-patch-google.akamaized.net/${ver}/lo_bundle/${key}")
                        with(url.openConnection() as HttpsURLConnection) {
                            requestMethod = "GET"

                            val bytes = inputStream.readBytes()
                            target.writeBytes(bytes)
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), R.string.patch_done, Toast.LENGTH_SHORT).show()
                    button.isEnabled = true
                }
            }
        } // TextureDataManager.getDataVersion
    }
}