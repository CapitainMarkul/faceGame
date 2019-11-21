package ru.tzhack.facegame.nearby.host

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.messages.MessagesClient
import ru.tzhack.facegame.BuildConfig
import ru.tzhack.facegame.R
import ru.tzhack.facegame.data.model.NearbyPlayer
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

    private val connectionClient: ConnectionsClient by lazy { Nearby.getConnectionsClient(activity!!) }
//    private var messageClient: MessagesClient = Nearby.getMessagesClient(activity!!)

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides.

            foundPlayer(NearbyPlayer(connectionInfo.endpointName, endPointId, false))

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
            Log.e(TAG, "TEST")
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            Log.e(TAG, "TEST")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${NearbyHostFragment::class.java.canonicalName}")

        val nickname = arguments?.getString(ARG_NICKNAME)
        binding.nickName = if(nickname.isNullOrEmpty()) "DefaultHostNick" else nickname

        binding.btnStartHost.setOnClickListener { startHost() }
        binding.btnStopHost.setOnClickListener { stopHost() }
        binding.btnStartGame.setOnClickListener { startGame() }

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
        stopHost()
    }

    private fun startHost() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        connectionClient
            .startAdvertising(
                binding.nickName!!,
                BuildConfig.NEARBY_SERVICE_ID,
                connectionLifecycleCallback,
                advertisingOptions
            )
            .addOnSuccessListener {
                // We're advertising!
                binding.progressHost.visibility = View.VISIBLE

            }
            .addOnFailureListener { e: Exception ->
                // We were unable to start advertising.
                e.printStackTrace()
                showError(e.localizedMessage ?: "")
            }
    }

    private fun stopHost() {
        connectionClient.stopAllEndpoints()
        connectionClient.stopAdvertising()

        nearbyAdapter.removeAllItems()
        binding.progressHost.visibility = View.GONE
    }

    private fun startGame() {
        /*TODO:*/
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