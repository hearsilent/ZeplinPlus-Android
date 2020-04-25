package hearsilent.zeplin.libs

import android.content.Context
import android.text.TextUtils
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hearsilent.zeplin.callback.*
import hearsilent.zeplin.models.ProjectModel
import hearsilent.zeplin.models.ScreenModel
import hearsilent.zeplin.models.TokenModel
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

object NetworkHelper {

    private const val ZEPLIN_BASE_URL = "https://api.zeplin.dev/v1"

    const val ZEPLIN_AUTHORIZE_URL =
        "${ZEPLIN_BASE_URL}/oauth/authorize?client_id=${Constant.CLIENT_ID}&redirect_uri=${Constant.REDIRECT_URI}&response_type=code"
    private const val ZEPLIN_OAUTH_ACCESS_URL = "${ZEPLIN_BASE_URL}/oauth/token"

    private const val ZEPLIN_PROJECTS_URL = "${ZEPLIN_BASE_URL}/projects"
    private const val ZEPLIN_PROJECT_URL = "${ZEPLIN_BASE_URL}/projects/%s"
    private const val ZEPLIN_SCREENS_URL =
        "${ZEPLIN_BASE_URL}/projects/%s/screens?sort=section&limit=999&offset=0"
    private const val ZEPLIN_SCREEN_URL = "${ZEPLIN_BASE_URL}/projects/%s/screens/%s"

    private var mClient: OkHttpClient = init()

    private fun init(): OkHttpClient {
        return OkHttpClient().newBuilder().followRedirects(true).followSslRedirects(true)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).build()
    }

    private fun getOauthToken(context: Context): String? {
        return Memory.getObject(
            context,
            Constant.PREF_ZEPLIN_TOKEN,
            TokenModel::class.java
        )?.access_token
    }

    fun zeplinOauth(code: String, callback: TokenCallback) {
        val builder = FormBody.Builder()
        builder.add("grant_type", "authorization_code")
        builder.add("code", code)
        builder.add("redirect_uri", Constant.REDIRECT_URI)
        builder.add("client_id", Constant.CLIENT_ID)
        builder.add("client_secret", Constant.CLIENT_SECRET)

        val request: Request = Request.Builder()
            .url(ZEPLIN_OAUTH_ACCESS_URL).post(builder.build())
            .build()

        mClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                    val body = responseBodyCopy.string()
                    val model = jacksonObjectMapper().readerFor(TokenModel::class.java)
                        .readValue<TokenModel>(body)
                    callback.onSuccess(model)
                } catch (e: Exception) {
                    callback.onFail(e.toString())
                }
            }
        })
    }

    fun getProjects(context: Context, callback: ProjectsCallback) {
        val token = getOauthToken(context)
        if (TextUtils.isEmpty(token)) {
            callback.onFail("Token is empty.")
            return
        }

        val request: Request =
            Request.Builder().header("authorization", "Bearer $token").url(ZEPLIN_PROJECTS_URL)
                .get().build()

        mClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                    val body = responseBodyCopy.string()
                    val model = jacksonObjectMapper().readerFor(object :
                        TypeReference<List<ProjectModel>>() {})
                        .readValue<List<ProjectModel>>(body)
                    callback.onSuccess(model)
                } catch (e: Exception) {
                    callback.onFail(e.toString())
                }
            }
        })
    }

    fun getProject(context: Context, pid: String, callback: ProjectCallback) {
        val token = getOauthToken(context)
        if (TextUtils.isEmpty(token)) {
            callback.onFail("Token is empty.")
            return
        }

        val url = String.format(Locale.getDefault(), ZEPLIN_PROJECT_URL, pid)
        val request: Request =
            Request.Builder().header("authorization", "Bearer $token").url(url).get().build()

        mClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                    val body = responseBodyCopy.string()
                    val model = jacksonObjectMapper().readerFor(ProjectModel::class.java)
                        .readValue<ProjectModel>(body)
                    callback.onSuccess(model)
                } catch (e: Exception) {
                    callback.onFail(e.toString())
                }
            }
        })
    }

    fun getScreens(context: Context, pid: String, callback: ScreensCallback) {
        val token = getOauthToken(context)
        if (TextUtils.isEmpty(token)) {
            callback.onFail("Token is empty.")
            return
        }

        val url = String.format(Locale.getDefault(), ZEPLIN_SCREENS_URL, pid)
        val request: Request =
            Request.Builder().header("authorization", "Bearer $token").url(url)
                .get().build()

        mClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                    val body = responseBodyCopy.string()
                    val model = jacksonObjectMapper().readerFor(object :
                        TypeReference<List<ScreenModel>>() {})
                        .readValue<List<ScreenModel>>(body)
                    callback.onSuccess(model)
                } catch (e: Exception) {
                    callback.onFail(e.toString())
                }
            }
        })
    }

    fun getScreen(context: Context, pid: String, sid: String, callback: ScreenCallback) {
        val token = getOauthToken(context)
        if (TextUtils.isEmpty(token)) {
            callback.onFail("Token is empty.")
            return
        }

        val url = String.format(Locale.getDefault(), ZEPLIN_SCREEN_URL, pid, sid)
        val request: Request =
            Request.Builder().header("authorization", "Bearer $token").url(url).get().build()

        mClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                    val body = responseBodyCopy.string()
                    val model = jacksonObjectMapper().readerFor(ScreenModel::class.java)
                        .readValue<ScreenModel>(body)
                    callback.onSuccess(model)
                } catch (e: Exception) {
                    callback.onFail(e.toString())
                }
            }
        })
    }

}