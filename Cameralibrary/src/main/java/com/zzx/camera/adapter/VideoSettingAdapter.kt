package com.zzx.camera.adapter

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.tomy.lib.ui.adapter.BindListAdapter
import com.zzx.camera.R

/**@author Tomy
 * Created by Tomy on 2018/10/18.
 */
class VideoSettingAdapter(var context: Context): BindListAdapter<CharSequence, VideoSettingAdapter.ViewHolder>() {

    private var mPosition = 0

    fun setCheck(position: Int) {
        mPosition   = position
        notifyDataSetChanged()
    }

    override fun getLayoutId(): Int {
//        return android.R.layout.simple_list_item_single_choice
        return R.layout.container_preference_list_item
    }

    override fun getHolder(): ViewHolder {
        return ViewHolder()
    }

    /*override fun bindToHolder(holder: ViewHolder, position: Int, item: CharSequence) {
        holder.bindData(item, position == mPosition)
    }*/


    inner class ViewHolder: BaseHolder<CharSequence>() {

        @BindView(R.id.check_box)
        lateinit var mCheckBox: CheckBox

        @BindView(R.id.icon_mode)
        lateinit var mIcon: ImageView

        @BindView(R.id.info)
        lateinit var mInfo: TextView

        override fun bindToItem(position: Int, item: CharSequence) {
            mCheckBox.isChecked = mPosition == position
            mInfo.text  = item
            mIcon.visibility = View.GONE
        }
    }
}