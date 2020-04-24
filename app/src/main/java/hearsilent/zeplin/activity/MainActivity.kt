package hearsilent.zeplin.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import hearsilent.zeplin.R
import hearsilent.zeplin.callback.ScreenCallback
import hearsilent.zeplin.callback.TokenCallback
import hearsilent.zeplin.libs.AccessHelper
import hearsilent.zeplin.libs.Constant
import hearsilent.zeplin.libs.Memory
import hearsilent.zeplin.models.ScreenModel
import hearsilent.zeplin.models.TokenModel


class MainActivity : AppCompatActivity() {

    private var mZeplinToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViews()
    }

    private fun setUpViews() {
        val model = Memory.getObject(this@MainActivity, Constant.PREF_ZEPLIN_TOKEN, TokenModel::class.java)
        if (model == null) {
            checkZeplinToken()
        } else {
            val data: Uri? = intent.data
            if (data != null && data.scheme.equals("zpl")) {
                if (data.host.equals("screen")) {
                    val pid = data.getQueryParameter("pid")
                    val sid = data.getQueryParameter("sids")
                    if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(sid))
                        AccessHelper.getScreen(this, pid!!, sid!!, object : ScreenCallback() {
                            override fun onSuccess(screen: ScreenModel) {
                                runOnUiThread {
                                    val intent = Intent(this@MainActivity, ScreenActivity::class.java).apply {
                                        putExtra("url", screen.image.original_url)
                                    }
                                    startActivity(intent)
                                }
                            }

                            override fun onFail(errorMessage: String?) {
                                Log.wtf(Constant.TAG, errorMessage)
                            }
                        })
                }
            }
        }
    }

    private fun checkZeplinToken() {
        val data: Uri? = intent.data
        if (data != null && data.scheme.equals("hearsilent")) {
            val code = data.getQueryParameter("code")
            code?.let {
                AccessHelper.zeplinOauth(it, object : TokenCallback() {
                    override fun onSuccess(token: TokenModel) {
                        Memory.setObject(this@MainActivity, Constant.PREF_ZEPLIN_TOKEN, token)
                    }

                    override fun onFail(errorMessage: String?) {
                        Log.wtf(Constant.TAG, errorMessage)
                    }
                })
            }
        } else if (TextUtils.isEmpty(mZeplinToken)) {
            showZeplinTokenDialog()
        }
    }

    private fun showZeplinTokenDialog() {
        AlertDialog.Builder(this).setTitle("Blablablabla")
                .setMessage("Blablabla")
                .setPositiveButton("OK") { _, _ ->
                    val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                    builder.setToolbarColor(
                            ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                    val customTabsIntent: CustomTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this@MainActivity,
                            Uri.parse(AccessHelper.ZEPLIN_AUTHORIZE_URL))
                }.setCancelable(false).show()
    }
}
