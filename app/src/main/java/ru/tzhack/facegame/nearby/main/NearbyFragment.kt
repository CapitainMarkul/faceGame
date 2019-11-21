package ru.tzhack.facegame.nearby.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import ru.tzhack.facegame.R
import ru.tzhack.facegame.databinding.FragmentNearbyMainBinding
import ru.tzhack.facegame.nearby.host.NearbyHostFragment
import ru.tzhack.facegame.nearby.slave.NearbySlaveFragment

class NearbyFragment : Fragment(), ViewModelStoreOwner {

    companion object {
        val TAG: String = NearbyFragment::class.java.simpleName

        fun createFragment() =
            NearbyFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    private lateinit var binding: FragmentNearbyMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${NearbyFragment::class.java.canonicalName}")

        binding.btnHostPlayer.setOnClickListener { showHostFragment() }

        binding.btnSearchPlayer.setOnClickListener { showSlaveFragment() }
    }

    private fun showHostFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, NearbyHostFragment.createFragment(getNickname()), NearbyHostFragment.TAG)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun showSlaveFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, NearbySlaveFragment.createFragment(getNickname()), NearbySlaveFragment.TAG)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun getNickname() = binding.etxtPlayerNickname.text.toString()
}