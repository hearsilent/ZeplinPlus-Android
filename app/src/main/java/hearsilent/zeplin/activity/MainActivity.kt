package hearsilent.zeplin.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.databind.ObjectMapper
import hearsilent.zeplin.BuildConfig
import hearsilent.zeplin.R
import hearsilent.zeplin.adapter.ProjectAdapter
import hearsilent.zeplin.callback.ProjectCallback
import hearsilent.zeplin.callback.ProjectsCallback
import hearsilent.zeplin.callback.ScreenCallback
import hearsilent.zeplin.callback.TokenCallback
import hearsilent.zeplin.libs.Constant
import hearsilent.zeplin.libs.Memory
import hearsilent.zeplin.libs.NetworkHelper
import hearsilent.zeplin.models.ProjectModel
import hearsilent.zeplin.models.ScreenModel
import hearsilent.zeplin.models.TokenModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViews()
    }

    private fun setUpViews() {
        setSupportActionBar(toolbar)

        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.bg_secondary)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener { fetchProject() }

        button_login.setOnClickListener(this)

        val model = Memory.getObject(this, Constant.PREF_ZEPLIN_TOKEN, TokenModel::class.java)
        if (model == null) {
            swipeRefreshLayout.isEnabled = false
            checkZeplinToken()
        } else {
            if (!checkIntent()) {
                if (System.currentTimeMillis() > model.expires_in - DateUtils.HOUR_IN_MILLIS) {
                    if (System.currentTimeMillis() > model.refresh_expires_in - DateUtils.HOUR_IN_MILLIS) {
                        Memory.setObject(this, Constant.PREF_ZEPLIN_TOKEN, null)
                        swipeRefreshLayout.isEnabled = false
                        checkZeplinToken()
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.i(Constant.TAG, "Refresh token: ${model.refresh_token}")
                        }
                        fetchToken(model.refresh_token, true)
                    }
                } else {
                    fetchProject()
                }
            }
        }
    }

    private fun checkIntent(): Boolean {
        val data: Uri? = intent.data
        if (data != null && data.scheme.equals("zpl")) {
            if (data.host.equals("screen")) {
                val pid = data.getQueryParameter("pid")
                val sid = data.getQueryParameter("sids")
                if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(sid)) {
                    swipeRefreshLayout.isRefreshing = true
                    NetworkHelper.getScreen(this, pid!!, sid!!, object : ScreenCallback() {
                        override fun onSuccess(screen: ScreenModel) {
                            if (isFinishing) {
                                return
                            }
                            runOnUiThread {
                                val intent =
                                    Intent(this@MainActivity, ScreenActivity::class.java).apply {
                                        putExtra(
                                            "screen",
                                            ObjectMapper().writeValueAsString(screen)
                                        )
                                    }
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFail(errorMessage: String?) {
                            if (isFinishing) {
                                return
                            }
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                                fetchProject()
                            }
                        }
                    })
                    return true
                }
            } else if (data.host.equals("project")) {
                val pid = data.getQueryParameter("pid")
                if (!TextUtils.isEmpty(pid)) {
                    swipeRefreshLayout.isRefreshing = true
                    NetworkHelper.getProject(this, pid!!, object : ProjectCallback() {
                        override fun onSuccess(project: ProjectModel) {
                            if (isFinishing) {
                                return
                            }
                            runOnUiThread {
                                val intent =
                                    Intent(this@MainActivity, ProjectActivity::class.java).apply {
                                        putExtra(
                                            "project",
                                            ObjectMapper().writeValueAsString(project)
                                        )
                                    }
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFail(errorMessage: String?) {
                            if (isFinishing) {
                                return
                            }
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                                fetchProject()
                            }
                        }
                    })
                    return true
                }
            }
        }
        return false
    }

    private fun checkZeplinToken() {
        val data: Uri? = intent.data
        if (data != null && data.scheme.equals("hearsilent")) {
            button_login.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            val code = data.getQueryParameter("code")
            code?.let { fetchToken(code, false) }
        } else {
            button_login.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
        }
    }

    private fun fetchToken(code: String, isRefresh: Boolean) {
        swipeRefreshLayout.isRefreshing = true
        NetworkHelper.zeplinOauth(code, isRefresh, object : TokenCallback() {
            override fun onSuccess(token: TokenModel) {
                if (isFinishing) {
                    return
                }
                Memory.setObject(this@MainActivity, Constant.PREF_ZEPLIN_TOKEN, token)
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        if (isRefresh) R.string.refresh_zeplin_token_success else R.string.get_zeplin_token_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchProject()
                }
            }

            override fun onFail(errorMessage: String?) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    button_login.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@MainActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun fetchProject() {
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.isRefreshing = true
        NetworkHelper.getProjects(this, object : ProjectsCallback() {
            override fun onSuccess(projects: List<ProjectModel>) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = ProjectAdapter(this@MainActivity, projects)
                }
            }

            override fun onFail(errorMessage: String?) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(
                        this@MainActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        if (v == button_login) {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(
                ContextCompat.getColor(this@MainActivity, R.color.toolbar)
            )
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(
                this@MainActivity,
                Uri.parse(NetworkHelper.ZEPLIN_AUTHORIZE_URL)
            )
        }
    }

}
