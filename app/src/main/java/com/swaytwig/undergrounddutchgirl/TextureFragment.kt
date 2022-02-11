package com.swaytwig.undergrounddutchgirl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.ListFragment

class TextureFragment : ListFragment(), TextureAdapter.ListSwitchChangedListener {
    private val list: ArrayList<TextureData> = ArrayList<TextureData>()
    private var arr: Array<TextureData> = arrayOf<TextureData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_texture, container, false)

        if(list.size == 0) {
            list.add(TextureData("1", "TEST1"))
            list.add(TextureData("2", "TEST2"))
            list.add(TextureData("3", "TEST3"))
            list.add(TextureData("4", "TEST4"))
            list.add(TextureData("5", "TEST5"))
            arr = list.toArray(arrayOfNulls<TextureData>(list.size))
        }

        val listTextures = v.findViewById<ListView>(android.R.id.list)
        listTextures.adapter = TextureAdapter(requireActivity(), arr, this)
        return v
    }

    override fun onListSwitchChanged(position: Int, checked: Boolean) {
        val item = list[position]
        android.util.Log.e("TEST", item.getText() + ", " + checked.toString())
    }
}