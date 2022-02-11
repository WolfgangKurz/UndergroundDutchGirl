package com.swaytwig.undergrounddutchgirl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.switchmaterial.SwitchMaterial


class TextureAdapter(context: Context, data: ArrayList<TextureData>) : BaseAdapter() {
    var mContext: Context = context
    var mView: View = LayoutInflater.from(mContext).inflate(R.layout.layout_texture_item, null)
    var list: ArrayList<TextureData> = data

    override fun getCount(): Int = list.size
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): TextureData = list[position]

    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
        val view = mView

        val item = list[position]
        val switch = view.findViewById<SwitchMaterial>(R.id.item_switch)
        switch.text = item.getText()

        return view
    }
}
