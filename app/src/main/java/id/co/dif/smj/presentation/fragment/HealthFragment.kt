package id.co.dif.smj.presentation.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentHealtBinding
import id.co.dif.smj.databinding.Healtfragment2Binding


class HealthFragment : BaseFragment<BaseViewModel, Healtfragment2Binding>() {
    override val layoutResId = R.layout.healtfragment2

    private val handler = Handler(Looper.getMainLooper())
    private val random = java.util.Random()
    private val stableProgressRange = 50..60
    private val jumpingProgressRange = 80..100
    private var isStableRange = true
    private var currentEmotionIndex = 0

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


    }


}