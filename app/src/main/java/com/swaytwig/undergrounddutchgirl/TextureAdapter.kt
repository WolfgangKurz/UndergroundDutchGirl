package com.swaytwig.undergrounddutchgirl

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.switchmaterial.SwitchMaterial


class TextureAdapter(context: Context, data: Array<TextureData>, listener: ListSwitchChangedListener) : BaseAdapter() {
    interface ListSwitchChangedListener {
        fun onListSwitchChanged(position: Int, checked: Boolean)
    }

    private val ctx: Context = context
    private val inflater: LayoutInflater = LayoutInflater.from(ctx)

    private var list: Array<TextureData> = data
    private val listSwitchChangedListener: ListSwitchChangedListener = listener

    override fun getCount(): Int = list.size
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): TextureData = list[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        @SuppressLint("InflateParams")
        val view = convertView ?: inflater.inflate(R.layout.layout_texture_item, null, true)

        val switch = view.findViewById<SwitchMaterial>(R.id.item_switch)
        switch.text = list[position].getText()
        switch.setOnClickListener {
            listSwitchChangedListener.onListSwitchChanged(position, (it as SwitchMaterial).isChecked)
        }

        return view
    }
}
