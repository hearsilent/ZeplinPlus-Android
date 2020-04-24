package hearsilent.zeplin.callback

import hearsilent.zeplin.models.ScreenModel

abstract class ScreenCallback {
    abstract fun onSuccess(screen: ScreenModel)
    abstract fun onFail(errorMessage: String?)
}