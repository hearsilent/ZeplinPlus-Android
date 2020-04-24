package hearsilent.zeplin.models

data class TokenModel(
    val access_token: String,
    val expires_in: Long,
    val refresh_token: String,
    val refresh_expires_in: Long,
    val token_type: String
)