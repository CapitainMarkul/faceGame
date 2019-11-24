package ru.tzhack.facegame.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.tzhack.facegame.R

//TODO: Anim Files
enum class FaceEmoji(
    @DrawableRes val resAnim: Int,
    @StringRes val resDescription: Int
) {
    DOUBLE_EYE_CLOSE(R.drawable.double_eye_anim, R.string.face_emoji_double_eye_close),
    LEFT_EYE_CLOSE(R.drawable.left_eye_anim, R.string.face_emoji_left_eye_close),
    RIGHT_EYE_CLOSE(R.drawable.right_eye_anim, R.string.face_emoji_right_eye_close),

    SMILE(R.drawable.smile_anim, R.string.face_emoji_smile),

    MOUTH_OPEN(R.drawable.empty_anim, R.string.face_emoji_mouth_open),

    HEAD_BIAS_LEFT(R.drawable.empty_anim, R.string.face_emoji_head_bias_left),
    HEAD_BIAS_RIGHT(R.drawable.empty_anim, R.string.face_emoji_head_bias_right),

    HEAD_ROTATE_LEFT(R.drawable.empty_anim, R.string.face_emoji_head_rotate_left),
    HEAD_ROTATE_RIGHT(R.drawable.empty_anim, R.string.face_emoji_head_rotate_right),
}