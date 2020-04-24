package hearsilent.zeplin.callback

import hearsilent.zeplin.models.TokenModel

abstract class TokenCallback {
    abstract fun onSuccess(token: TokenModel)
    abstract fun onFail(errorMessage: String?)
}