package ru.tzhack.facegame.nearby

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.messages.MessagesClient
import ru.tzhack.facegame.BuildConfig
import ru.tzhack.facegame.data.model.NearbyPlayer

object NearbyObject {

    interface NearbyFitQuestHostListener {
        fun onAdvertising()

        fun onPlayerConnected(endPoint: String)

        fun onMessageRecive(payload: Payload)

        fun hostFoundPlayer(player: NearbyPlayer)
        fun hostLostPlayer(playerEndPointId: String)

        fun onError(error: String)
    }

    interface NearbyFitQuestSlaveListener {
        fun onDiscovering()

        fun onPlayerConnected(endPoint: String)

        fun onMessageRecive(payload: Payload)

        fun slaveFoundPlayer(player: NearbyPlayer)
        fun slaveLostPlayer(playerEndPointId: String)

        fun onError(error: String)
    }

    val TAG: String = NearbyObject::class.java.simpleName

    private val clients = mutableListOf<String>()

    private var context: Context? = null

    private var hostListener: NearbyFitQuestHostListener? = null
    private var slaveListener: NearbyFitQuestSlaveListener? = null

    private val connectionClient: ConnectionsClient by lazy {
        Nearby.getConnectionsClient(getContext())
    }

    private var messageClient: MessagesClient = Nearby.getMessagesClient(getContext())

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endPointId: String, info: DiscoveredEndpointInfo) {
            // An endpoint was found. We request a connection to it.
            Log.e("TAG", "TEST")

            slaveListener?.slaveFoundPlayer(NearbyPlayer(info.endpointName, endPointId, false))
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
            Log.e("TAG", "TEST")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides.

            val player = NearbyPlayer(connectionInfo.endpointName, endPointId, false)
            hostListener?.hostFoundPlayer(player)
            clients.add(connectionInfo.endpointName)

            connectionClient.acceptConnection(endPointId, payloadCallback)
            Log.e(TAG, "onConnectionInitiated: $endPointId | OK")
        }

        override fun onConnectionResult(endPointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    // We're connected! Can now start sending and receiving data.

                    hostListener?.onPlayerConnected(endPointId)
                    slaveListener?.onPlayerConnected(endPointId)
                    //SLAVE:HOST:nearbyAdapter.updateItemConnected(endPointId)

                    Log.e(TAG, "onConnectionResult: $endPointId | STATUS_OK")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    // The connection was rejected by one or both sides.
                    Log.e(TAG, "onConnectionResult: $endPointId | STATUS_CONNECTION_REJECTED")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    // The connection broke before it was able to be accepted.
                    Log.e(TAG, "onConnectionResult: $endPointId | STATUS_ERROR")
                }
            }
        }

        override fun onDisconnected(endPointId: String) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.

            hostListener?.hostLostPlayer(endPointId)
            slaveListener?.slaveLostPlayer(endPointId)
            //TODO:SLAVE:HOST:lostPlayer(endPointId)

            clients.remove(endPointId)

            Log.e(TAG, "onDisconnected: $endPointId")
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            hostListener?.onMessageRecive(payload)
            slaveListener?.onMessageRecive(payload)
        }

        override fun onPayloadTransferUpdate(endpointId: String, payload: PayloadTransferUpdate) {
            Log.e(TAG, "TEST")
        }
    }

    //============== HOST ==============
    fun initHost(context: Context, listener: NearbyFitQuestHostListener) {
        this.context = context
        this.hostListener = listener
    }

    fun startHost(nickName: String) {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        connectionClient
                .startAdvertising(
                        nickName,
                        BuildConfig.NEARBY_SERVICE_ID,
                        connectionLifecycleCallback,
                        advertisingOptions
                )
                .addOnSuccessListener {
                    // We're advertising!
                    hostListener?.onAdvertising()
//                  HOST:  binding.progressHost.visibility = View.VISIBLE

                }
                .addOnFailureListener { e: Exception ->
                    // We were unable to start advertising.
                    e.printStackTrace()

                    hostListener?.onError(e.localizedMessage ?: "")
                }
    }

    fun stopHost() {
        connectionClient.stopAllEndpoints()
        connectionClient.stopAdvertising()
    }

    //============== SLAVE ==============
    fun initSlave(context: Context, listener: NearbyFitQuestSlaveListener) {
        this.context = context
        this.slaveListener = listener
    }

    fun startSlave() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

        connectionClient
                .startDiscovery(BuildConfig.NEARBY_SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener {
                    // We're discovering!
                    slaveListener?.onDiscovering()
//                    SLAVE: binding.progressSlave.visibility = View.VISIBLE

                    Log.e(TAG, "startSlave: | OK")
                }
                .addOnFailureListener { e: Exception ->
                    // We're unable to start discovering.
                    e.printStackTrace()

                    slaveListener?.onError(e.localizedMessage ?: "")

                    Log.e(TAG, "startSlave: | ERROR")
                }
    }

    fun connectToHost(nickName: String, player: NearbyPlayer) {
        connectionClient
                .requestConnection(nickName, player.playerEndPoint, connectionLifecycleCallback)
                .addOnSuccessListener {
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    Log.e(TAG, "connectToHost: OK")
                }
                .addOnFailureListener { e: Exception ->
                    // Nearby Connections failed to request the connection.
                    e.printStackTrace()
                    slaveListener?.onError(e.localizedMessage ?: "")
                    Log.e(TAG, "connectToHost: ERROR")
                }
    }

    fun stopSlave() {
        connectionClient.stopAllEndpoints()
        connectionClient.stopDiscovery()

        clients.clear()
    }

    //============== COMMON ==============

    fun startGameMessage() {
//TODO;
    }

    fun sendYouLoseMessage() {
//TODO;
    }

    fun destroy() {
        /*TODO:*/
        context = null
        hostListener = null
        slaveListener = null
    }

    private fun getContext() =
            context ?: throw Exception("Nearby 'Init' must be called!")
}