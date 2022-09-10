package jp.co.yumemi.android.code_check.model.github.users

import androidx.annotation.StringRes
import jp.co.yumemi.android.code_check.R
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

/**
 * https://api.github.com/user/USERNAME を叩いた時の要素。
 * [Json]のignoreUnknownKeysをtrueにしているため、利用する値のみ定義している。
 * ドキュメント&スキーマ：https://docs.github.com/ja/rest/users/users#get-a-user
 */
@Serializable
data class GitUser(
    @SerialName("login") val name: String,
    @SerialName("html_url") val url: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("bio") val bio: String? = null,
    @SerialName("type") val type: UserType = UserType.UNKNOWN,
    @SerialName("company") val company: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("followers") val followers: Int,
    @SerialName("following") val following: Int
) {
    companion object {
        @Serializable(with = UserTypeSerializer::class)
        enum class UserType(@StringRes val stringResId: Int) {
            USER(R.string.user_label),
            ORGANIZATION(R.string.organization_label),
            UNKNOWN(R.string.unknown_label)
        }

        @OptIn(ExperimentalSerializationApi::class)
        @Serializer(forClass = UserType::class)
        object UserTypeSerializer : KSerializer<UserType> {
            override fun deserialize(decoder: Decoder): UserType {
                return when (decoder.decodeString()) {
                    "User" -> UserType.USER
                    "Organization" -> UserType.ORGANIZATION
                    else -> UserType.UNKNOWN
                }
            }

            override val descriptor: SerialDescriptor
                get() = PrimitiveSerialDescriptor("UserType", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: UserType) {
                val typeText = when (value) {
                    UserType.USER -> "User"
                    UserType.UNKNOWN -> "Unknown"
                    UserType.ORGANIZATION -> "Organization"
                }
                encoder.encodeString(typeText)
            }
        }
    }
}
