package id.co.dif.smj.presentation.activity

import android.content.Intent
import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.databinding.ActivityMessageBinding
import id.co.dif.smj.presentation.adapter.MessagesAdapter
import id.co.dif.smj.viewmodel.MessagesViewModel

class MessageActivity : BaseActivity<MessagesViewModel, ActivityMessageBinding>() {
    override val layoutResId = R.layout.activity_message

    lateinit var adapter : MessagesAdapter

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        adapter = MessagesAdapter()
        binding.rvMessage.adapter = adapter

        binding.rootLayout.onBackButtonClicked =  {
          startActivity(Intent(this, ChatActivity::class.java))
        }

    }
}