package hearsilent.zeplin.callback

import hearsilent.zeplin.models.ScreenModel

abstract class ScreensCallback {
    abstract fun onSuccess(screens: List<ScreenModel>)
    abstract fun onFail(errorMessage: String?)
}