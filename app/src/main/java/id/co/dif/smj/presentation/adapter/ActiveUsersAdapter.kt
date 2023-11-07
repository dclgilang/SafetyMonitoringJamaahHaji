package id.co.dif.smj.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.base.BaseViewHolder
import id.co.dif.smj.data.ActiveUser
import id.co.dif.smj.databinding.ItemActiveUserBinding
import id.co.dif.smj.databinding.ItemActiveUserLoadingBinding
import id.co.dif.smj.presentation.activity.EngineerProfileActivity
import id.co.dif.smj.utils.base64ImageToBitmap
import id.co.dif.smj.utils.gravatar
import id.co.dif.smj.utils.loadImage
import id.co.dif.smj.utils.shimmerDrawable
import id.co.dif.smj.utils.str
import id.co.dif.smj.viewmodel.MyDashboardViewModel

class ActiveUsersAdapter(data: List<ActiveUser> = listOf()) :
    BaseAdapter<MyDashboardViewModel, ViewBinding, ActiveUser>() {
    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_LOADING = 2
    }

    override val layoutResId: Int
        get() = R.layout.item_active_user

    init {
        this.data.addAll(data)
    }

    var isLoading = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding> {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            ITEM_VIEW_TYPE_CONTENT -> {
                ItemActiveUserBinding.inflate(inflater, parent, false)
            }

            ITEM_VIEW_TYPE_LOADING -> {
                ItemActiveUserLoadingBinding.inflate(inflater, parent, false)
            }

            else -> throw Error("Unrecognized view type $viewType")
        }

        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.lastIndex && isLoading) ITEM_VIEW_TYPE_LOADING else ITEM_VIEW_TYPE_CONTENT
    }

    fun addLoadingFooter() {
        isLoading = true
        add(ActiveUser("", "", "", ""))
    }

    fun add(activeUser: ActiveUser) {
        data.add(activeUser)
        notifyItemInserted(data.lastIndex)
    }

    fun addAll(newData: List<ActiveUser>) {
        for (result in newData) {
            add(result)
        }
    }

    fun removeLoadingFooter() {
        isLoading = false
        val position = data.lastIndex
        if (data.lastOrNull() != null) {
            data.removeLast()
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): ActiveUser {
        return data[position]
    }

    override fun onLoadItem(
        binding: ViewBinding,
        data: MutableList<ActiveUser>,
        position: Int
    ) {
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_CONTENT -> {
                binding as ItemActiveUserBinding
                val activeUser = data[position]
                binding.root.setOnClickListener {
                    viewProfile(activeUser)
                }

                activeUser.image?.let { encoded ->
                    binding.imgProfilePic.loadImage(encoded, shimmerDrawable(), skipMemoryCaching = true)
                }

                binding.activeUser = activeUser
                binding.viewModel = viewModel
            }

            else -> {

            }
        }
    }

    private fun viewProfile(otherUser: ActiveUser) {
        preferences.selectedProfileId.value = otherUser.id.str.toInt()
        val intent = Intent(context, EngineerProfileActivity::class.java)
        context?.startActivity(intent)
    }

}