package id.co.dif.smj.presentation.adapter

import android.view.View
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.data.TopUser
import id.co.dif.smj.databinding.ItemTopUserBinding
import id.co.dif.smj.utils.gravatar
import id.co.dif.smj.utils.loadImage
import id.co.dif.smj.utils.shimmerDrawable
import id.co.dif.smj.viewmodel.TeamDashboardViewModel

class TopUsersAdapter(data: List<TopUser> = listOf()) :
    BaseAdapter<TeamDashboardViewModel, ItemTopUserBinding, TopUser>() {
    override val layoutResId: Int
        get() = R.layout.item_top_user

    init {
        this.data.addAll(data)
    }

    override fun onLoadItem(
        binding: ItemTopUserBinding,
        data: MutableList<TopUser>,
        position: Int
    ) {
        val topUser = data[position]
        binding.imgProfilePic.loadImage(
            topUser.userPictureUrl ?: gravatar(topUser.username), shimmerDrawable()
        )
        binding.topUser = topUser
        if (position == data.size - 1) binding.dividerItem.visibility = View.GONE

    }
}