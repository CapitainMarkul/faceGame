package ru.tzhack.facegame.nearby.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.nearby.connection.Payload
import ru.tzhack.facegame.R
import ru.tzhack.facegame.data.model.NearbyPlayer
import ru.tzhack.facegame.databinding.FragmentNearbyHostBinding
import ru.tzhack.facegame.nearby.NearbyObject
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

    private val hostListener = object : NearbyObject.NearbyFitQuestHostListener {
        override fun onAdvertising() {
            // We're advertising!
            binding.progressHost.visibility = View.VISIBLE
        }

        override fun onPlayerConnected(endPoint: String) {
            // We're connected! Can now start sending and receiving data.

            nearbyAdapter.updateItemConnected(endPoint)
        }

        override fun hostFoundPlayer(player: NearbyPlayer) {
            foundPlayer(player)
        }

        override fun onMessageRecive(payload: Payload) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun hostLostPlayer(playerEndPointId: String) {
            lostPlayer(playerEndPointId)
        }

        override fun onError(error: String) {
            showError(error)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NearbyObject.initHost(activity!!, hostListener)

        binding = DataBindingUtil.bind(view)
                ?: throw IllegalStateException("ViewDataBinding is null for ${NearbyHostFragment::class.java.canonicalName}")

        val nickname = arguments?.getString(ARG_NICKNAME)
        binding.nickName = if (nickname.isNullOrEmpty()) "DefaultHostNick" else nickname

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

    override fun onDestroy() {
        NearbyObject.destroy()
        super.onDestroy()
    }

    private fun startHost() {
        NearbyObject.startHost(binding.nickName!!)
    }

    private fun stopHost() {
        NearbyObject.stopHost()

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