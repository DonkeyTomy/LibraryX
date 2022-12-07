package com.zzx.camera.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.tomy.lib.ui.adapter.BindListAdapter
import com.zzx.camera.R

/**@author Tomy
 * Created by Tomy on 2018/10/11.
 */
class PhotoSettingAdapter(var context: Context): BindListAdapter<CharSequence, PhotoSettingAdapter.ViewHolder>() {

    private var mPosition = 0

    private val mIconList = ArrayList<Drawable>()

    fun setCheck(position: Int) {
        mPosition   = position
        notifyDataSetChanged()
    }

    fun setIconListId(iconList: List<Drawable>) {
        mIconList.apply {
            clear()
            addAll(iconList)
        }
    }

    override fun getLayoutId(): Int {
//        return android.R.layout.simple_list_item_single_choice
        return R.layout.container_preference_list_item
    }

    override fun getHolder(): ViewHolder {
        return ViewHolder()
    }

    /*override fun bindToHolder(holder: ViewHolder, position: Int, item: CharSequence) {
        holder.bindData(item, position, position == mPosition)
    }*/


    inner class ViewHolder: BindListAdapter.BaseHolder<CharSequence>() {

        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox

        @BindView(R.id.icon_mode)
        lateinit var mIcon: ImageView

        @BindView(R.id.info)
        lateinit var mInfo: TextView

        override fun bindToItem(position: Int, item: CharSequence) {
            mIcon.visibility = View.VISIBLE
            mCheckBox.isChecked = position == mPosition
            mInfo.text  = item
            mIcon.setImageDrawable(mIconList[position])
        }
    }

}