package com.swaytwig.undergrounddutchgirl.TextureData

import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class TextureDataManager {
    companion object {
        private var dataVersion: String? = null
        private var dataVersionJob: Job? = null

        fun getDataVersion(cb: (ver: String) -> Unit) {
            if (dataVersion != null)
                GlobalScope.launch(Dispatchers.Main) { cb(dataVersion!!) }
            else if (dataVersionJob != null) { // fetching
                GlobalScope.launch(Dispatchers.Main) {
                    dataVersionJob!!.join()
                    cb(dataVersion!!)
                }
            } else {
                dataVersionJob = GlobalScope.launch(Dispatchers.IO) {
                    val url = URL("https://udg.swaytwig.com/ver.txt")
                    with(url.openConnection() as HttpsURLConnection) {
                        requestMethod = "GET"

                        inputStream.bufferedReader().use {
                            dataVersion = it.readText()
                            withContext(Dispatchers.Main) { cb(dataVersion!!) }
                        }
                    }
                }
            }
        }

        private var textureList: Array<TextureDataRaw>? = null
        private var textureListJob: Job? = null

        fun getTextureList(cb: (textures: Array<TextureDataRaw>) -> Unit) {
            getDataVersion {
                val ver = it

                if (textureList != null)
                    GlobalScope.launch(Dispatchers.Main) { cb(textureList!!) }
                else if (textureListJob != null) { // fetching
                    GlobalScope.launch(Dispatchers.Main) {
                        textureListJob!!.join()
                        cb(textureList!!)
                    }
                } else {
                    textureListJob = GlobalScope.launch(Dispatchers.IO) {
                        val url = URL("https://udg.swaytwig.com/db/${ver}.json")
                        with(url.openConnection() as HttpsURLConnection) {
                            requestMethod = "GET"

                            inputStream.bufferedReader().use {
                                val json = JSONObject(it.readText())
                                val keys = json.keys()
                                val list: ArrayList<TextureDataRaw> = arrayListOf()
                                for (key in keys) {
                                    val item = json.getJSONObject(key)

                                    val hashOneStore = item.getString("onestore")
                                    val hashGoogle = item.getString("google")

                                    list.add(TextureDataRaw(key, hashOneStore, hashGoogle))
                                }
                                textureList = list.toArray(arrayOfNulls<TextureDataRaw>(list.size))

                                withContext(Dispatchers.Main) { cb(textureList!!) }
                            }
                        }
                    } // GlobalScope.launch
                }
            } // getDataVersion
        }
    }
}