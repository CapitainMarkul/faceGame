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
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import ru.tzhack.facegame.BuildConfig
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

    private val connectionClient: ConnectionsClient by lazy { Nearby.getConnectionsClient(activity!!) }

    private val nearbyAdapter = NearbySlaveAdapter(object : NearbySlaveAdapter.OnPlayerClickListener {
        override fun onPlayerClick(player: NearbyPlayer) {
            connectToHost(player)
        }
    })

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endPointId: String, info: DiscoveredEndpointInfo) {
            // An endpoint was found. We request a connection to it.
            Log.e("TAG", "TEST")

            foundPlayer(NearbyPlayer(info.endpointName, endPointId, false))
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
            Log.e("TAG", "TEST")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides.

            connectionClient.acceptConnection(endPointId, payloadCallback)
            Log.e(TAG, "onConnectionInitiated: $endPointId | OK")
        }

        override fun onConnectionResult(endPointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK                  -> {
                    // We're connected! Can now start sending and receiving data.

                    nearbyAdapter.updateItemConnected(endPointId)

                    Log.e(TAG, "onConnectionResult: $endPointId | STATUS_OK")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    // The connection was rejected by one or both sides.
                    Log.e(TAG, "onConnectionResult: $endPointId | STATUS_CONNECTION_REJECTED")
                }
                ConnectionsStatusCodes.STATUS_ERROR               -> {
                    // The connection broke before it was able to be accepted.
                    Log.e(TAG, "onConnectionResult: $endPointId | STATUS_ERROR")
                }
            }
        }

        override fun onDisconnected(endPointId: String) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.

            lostPlayer(endPointId)

            Log.e(TAG, "onDisconnected: $endPointId")
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(p0: String, p1: Payload) {
            Log.e(TAG, "onPayloadReceived")
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            Log.e(TAG, "onPayloadTransferUpdate")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby_slave, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${NearbySlaveFragment::class.java.canonicalName}")

        val nickname = arguments?.getString(ARG_NICKNAME)
        binding.nickName = if(nickname.isNullOrEmpty()) "DefaultSlaveNick" else nickname

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

    private fun startSlave() {
        ifAvailablePermissions {
            val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

            connectionClient
                .startDiscovery(BuildConfig.NEARBY_SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener {
                    // We're discovering!
                    binding.progressSlave.visibility = View.VISIBLE

                    Log.e(TAG, "startSlave: | OK")
                }
                .addOnFailureListener { e: Exception ->
                    // We're unable to start discovering.
                    e.printStackTrace()
                    showError(e.localizedMessage ?: "")

                    Log.e(TAG, "startSlave: | ERROR")
                }
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
        connectionClient.stopAllEndpoints()
        connectionClient.stopDiscovery()

        nearbyAdapter.removeAllItems()
        binding.progressSlave.visibility = View.GONE
    }

    private fun connectToHost(player: NearbyPlayer) {
        connectionClient
            .requestConnection(binding.nickName!!, player.playerEndPoint, connectionLifecycleCallback)
            .addOnSuccessListener {
                // We successfully requested a connection. Now both sides
                // must accept before the connection is established.
                Log.e(TAG, "connectToHost: OK")
            }
            .addOnFailureListener { e: Exception ->
                // Nearby Connections failed to request the connection.
                e.printStackTrace()
                showError(e.localizedMessage ?: "")
                Log.e(TAG, "connectToHost: ERROR")
            }
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