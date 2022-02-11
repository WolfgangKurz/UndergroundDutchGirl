package com.swaytwig.undergrounddutchgirl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class TextureFragment : Fragment() {
    private val list:ArrayList<TextureData> = ArrayList<TextureData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_texture, container, false)

        list.add(TextureData("1","TEST1"))
        list.add(TextureData("2","TEST2"))
        list.add(TextureData("3","TEST3"))
        list.add(TextureData("4","TEST4"))
        list.add(TextureData("5","TEST5"))
        val adapter = TextureAdapter(container!!.context, list)

        val listTextures = v.findViewById<ListView>(R.id.list_textures)
        listTextures.adapter = adapter

        return v
    }
}
