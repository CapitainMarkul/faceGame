package ru.tzhack.facegame.nearby.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tzhack.facegame.R
import ru.tzhack.facegame.databinding.FragmentNearbyHostBinding
import ru.tzhack.facegame.nearby.host.adapter.NearbyHostAdapter

class NearbyHostFragment : Fragment() {
    companion object {
        val TAG: String = NearbyHostFragment::class.java.simpleName

        private const val ARG_NICKNAME = "ARG_NICKNAME"

        fun createFragment(nickName: String) =
            NearbyHostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NICKNAME, nickName)
                }
            }
    }

    private lateinit var binding: FragmentNearbyHostBinding

    private val nearbyAdapter = NearbyHostAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${NearbyHostFragment::class.java.canonicalName}")

        binding.nickName = arguments?.getString(ARG_NICKNAME, "DefaultNick")

        binding.btnStartHost.setOnClickListener { startHost() }
        binding.btnStopHost.setOnClickListener { stopHost() }

        binding.rvNearbyPlayers.apply {
            adapter = nearbyAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(activity!!.getDrawable(R.drawable.item_divider_horizontal)!!)
            })
        }

    }

    private fun startHost() {
        binding.progressHost.visibility = View.VISIBLE

    }

    private fun stopHost() {
        binding.progressHost.visibility = View.GONE

    }
}