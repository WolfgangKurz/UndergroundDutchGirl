package com.swaytwig.undergrounddutchgirl.TextureData

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.json.JSONObject

class TextureDataManager {
    companion object {
        private val client = HttpClient(CIO)

        private suspend fun fetchText(url: String) = withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val ret = client.request<HttpResponse>(url) { method = HttpMethod.Get }
                if(ret.status == HttpStatusCode.OK)
                    return@withContext ret.readText().trim()
            }
            null
        }

        private var dataVersion: String? = null
        suspend fun getDataVersion(): String? {
            if (dataVersion == null)
                dataVersion = fetchText("https://udg.swaytwig.com/ver.txt")

            return dataVersion
        }

        private var textureList: Array<TextureDataRaw>? = null
        suspend fun getTextureList(): Array<TextureDataRaw>? {
            val ver = getDataVersion()

            if (textureList == null) {
                val content = fetchText("https://udg.swaytwig.com/db/$ver.json") ?: return null
                val json = JSONObject(content)
                val keys = json.keys()
                val list: ArrayList<TextureDataRaw> = arrayListOf()
                for (key in keys) {
                    val item = json.getJSONObject(key)

                    val hashOneStore = item.getString("onestore")
                    val hashGoogle = item.getString("google")

                    list.add(TextureDataRaw(key, hashOneStore, hashGoogle))
                }
                textureList = list.toArray(arrayOfNulls<TextureDataRaw>(list.size))
            }

            return textureList
        }
    }
}