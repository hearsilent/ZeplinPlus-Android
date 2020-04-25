package hearsilent.zeplin.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hearsilent.zeplin.R
import hearsilent.zeplin.adapter.ScreenAdapter
import hearsilent.zeplin.callback.ScreensCallback
import hearsilent.zeplin.libs.NetworkHelper
import hearsilent.zeplin.models.ProjectModel
import hearsilent.zeplin.models.ScreenModel
import kotlinx.android.synthetic.main.activity_main.*


class ProjectActivity : AppCompatActivity() {

    private var mProjectModel: ProjectModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        setUpViews()
    }

    private fun setUpViews() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.bg_secondary)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener { fetchScreens() }

        mProjectModel = jacksonObjectMapper().readerFor(ProjectModel::class.java)
            .readValue<ProjectModel>(intent.extras!!.getString("project"))
        supportActionBar!!.title = mProjectModel!!.name

        fetchScreens()
    }

    private fun fetchScreens() {
        swipeRefreshLayout.isRefreshing = true
        NetworkHelper.getScreens(this, mProjectModel!!.id, object : ScreensCallback() {
            override fun onSuccess(screens: List<ScreenModel>) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = ScreenAdapter(this@ProjectActivity, screens)
                }
            }

            override fun onFail(errorMessage: String?) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(
                        this@ProjectActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

}
