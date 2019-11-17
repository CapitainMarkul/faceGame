package ru.tzhack.facegame.nearby.slave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tzhack.facegame.R
import ru.tzhack.facegame.data.model.NearbyPlayer
import ru.tzhack.facegame.databinding.FragmentNearbySlaveBinding
import ru.tzhack.facegame.nearby.slave.adapter.NearbySlaveAdapter

class NearbySlaveFragment : Fragment() {
    companion object {
        val TAG: String = NearbySlaveFragment::class.java.simpleName

        private const val ARG_NICKNAME = "ARG_NICKNAME"

        fun createFragment(nickName: String) =
            NearbySlaveFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NICKNAME, nickName)
                }
            }
    }

    private lateinit var binding: FragmentNearbySlaveBinding

    private val nearbyAdapter = NearbySlaveAdapter(object : NearbySlaveAdapter.OnPlayerClickListener {
        override fun onPlayerClick(player: NearbyPlayer) {
            if (player.isHost) {
                /*TODO:*/
            } else {
                /*TODO:*/
            }
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby_slave, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${NearbySlaveFragment::class.java.canonicalName}")

        binding.nickName = arguments?.getString(ARG_NICKNAME, "DefaultNick")

        binding.btnStartSlave.setOnClickListener { startHost() }
        binding.btnStopSlave.setOnClickListener { stopHost() }

        binding.rvNearbyPlayers.apply {
            adapter = nearbyAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(activity!!.getDrawable(R.drawable.item_divider_horizontal)!!)
            })
        }

    }

    private fun startHost() {
        binding.progressSlave.visibility = View.VISIBLE

    }

    private fun stopHost() {
        binding.progressSlave.visibility = View.GONE

    }
}