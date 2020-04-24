package hearsilent.zeplin.callback

abstract class GeneralCallback {
    abstract fun onSuccess()
    abstract fun onFail(errorMessage: String?)
}