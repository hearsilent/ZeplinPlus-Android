package hearsilent.zeplin.libs

import android.content.Context
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import hearsilent.zeplin.callback.ScreensCallback
import hearsilent.zeplin.models.ScreenModel

class ScreenModelDataSource(
    private val context: Context,
    private val pid: String,
    private val screensCallback: ScreensCallback
) : PageKeyedDataSource<Int, ScreenModel>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ScreenModel>
    ) {
        NetworkHelper.getScreens(context, pid, 0, object : ScreensCallback() {
            override fun onSuccess(screens: List<ScreenModel>) {
                // pass result to DataSource and set previousPageKey and nextPageKey
                callback.onResult(screens, null, screens.size)
                screensCallback.onSuccess(screens)
            }

            override fun onFail(errorMessage: String?) {
                screensCallback.onFail(errorMessage)
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ScreenModel>) {
        NetworkHelper.getScreens(context, pid, params.key, object : ScreensCallback() {
            override fun onSuccess(screens: List<ScreenModel>) {
                // pass result to DataSource and set previousPageKey and nextPageKey
                callback.onResult(screens, params.key + screens.size)
                screensCallback.onSuccess(screens)
            }

            override fun onFail(errorMessage: String?) {
                screensCallback.onFail(errorMessage)
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ScreenModel>) {
    }
}

class ScreenModelDataSourceFactory(
    private val context: Context,
    private val pid: String,
    private val screensCallback: ScreensCallback
) : DataSource.Factory<Int, ScreenModel>() {
    override fun create(): DataSource<Int, ScreenModel> {
        return ScreenModelDataSource(context, pid, screensCallback)
    }
}