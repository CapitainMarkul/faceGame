package ru.tzhack.facegame.nearby.slave

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.nearby.connection.Payload
import ru.tzhack.facegame.R
import ru.tzhack.facegame.data.model.NearbyPlayer
import ru.tzhack.facegame.databinding.FragmentNearbySlaveBinding
import ru.tzhack.facegame.nearby.NearbyObject
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
            connectToHost(player)
        }
    })

    private val slaveListener = object : NearbyObject.NearbyFitQuestSlaveListener {
        override fun onDiscovering() {
            // We're discovering!
            binding.progressSlave.visibility = View.VISIBLE

            Log.e(TAG, "startSlave: | OK")
        }

        override fun onPlayerConnected(endPoint: String) {
            // We're connected! Can now start sending and receiving data.

            nearbyAdapter.updateItemConnected(endPoint)
        }

        override fun onMessageRecive(payload: Payload) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun slaveFoundPlayer(player: NearbyPlayer) {
            // An endpoint was found. We request a connection to it.
            Log.e("TAG", "TEST")

            foundPlayer(player)
        }

        override fun slaveLostPlayer(playerEndPointId: String) {
            lostPlayer(playerEndPointId)
        }

        override fun onError(error: String) {
            showError(error)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby_slave, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NearbyObject.initSlave(activity!!, slaveListener)

        binding = DataBindingUtil.bind(view)
                ?: throw IllegalStateException("ViewDataBinding is null for ${NearbySlaveFragment::class.java.canonicalName}")

        val nickname = arguments?.getString(ARG_NICKNAME)
        binding.nickName = if (nickname.isNullOrEmpty()) "DefaultSlaveNick" else nickname

        binding.btnStartSlave.setOnClickListener { startSlave() }
        binding.btnStopSlave.setOnClickListener { stopSlave() }

        binding.rvNearbyPlayers.apply {
            adapter = nearbyAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(activity!!.getDrawable(R.drawable.item_divider_horizontal)!!)
            })
        }

    }

    override fun onStop() {
        super.onStop()
        stopSlave()
    }

    override fun onDestroy() {
        NearbyObject.destroy()
        super.onDestroy()
    }

    private fun startSlave() {
        ifAvailablePermissions {
            NearbyObject.startSlave()
        }
    }

    private fun ifAvailablePermissions(action: () -> Unit) {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN), 1)
        } else action()
    }

    private fun stopSlave() {
        NearbyObject.stopSlave()

        nearbyAdapter.removeAllItems()
        binding.progressSlave.visibility = View.GONE
    }

    private fun connectToHost(player: NearbyPlayer) {
        NearbyObject.connectToHost(binding.nickName!!, player)
    }

    private fun foundPlayer(player: NearbyPlayer) {
        nearbyAdapter.addItem(player)
    }

    private fun lostPlayer(playerEndPointId: String) {
        nearbyAdapter.removeItem(playerEndPointId)
    }

    private fun showError(errorDescription: String) {
        MaterialDialog(activity!!).show {
            title(text = "Ошибка")
            message(text = errorDescription)
            positiveButton(text = "OK")
        }
    }
}