package com.swaytwig.undergrounddutchgirl.TextureData

class TextureDataRaw(private val key: String, private val hashOneStore: String, private val hashGoogle: String) {
    fun getKey() = this.key

    fun getHashOneStore() = this.hashOneStore
    fun getHashGoogle() = this.hashGoogle
}