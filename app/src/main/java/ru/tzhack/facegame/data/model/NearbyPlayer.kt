package ru.tzhack.facegame.data.model

import java.util.*

data class NearbyPlayer(
    val idPlayer: UUID,
    val playerNickname: String,
    val isHost: Boolean
) /* TODO: Parcelable */ {
}