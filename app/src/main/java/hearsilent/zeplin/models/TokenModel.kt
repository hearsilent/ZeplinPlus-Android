package hearsilent.zeplin.models

data class TokenModel(
    val access_token: String,
    var expires_in: Long,
    val refresh_token: String,
    var refresh_expires_in: Long,
    val token_type: String
)