package hearsilent.zeplin.activity

import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hearsilent.zeplin.R
import hearsilent.zeplin.adapter.ScreenAdapter
import hearsilent.zeplin.callback.ScreensCallback
import hearsilent.zeplin.extensions.LongExtension.toDuration
import hearsilent.zeplin.libs.NetworkHelper
import hearsilent.zeplin.models.ProjectModel
import hearsilent.zeplin.models.ScreenModel
import kotlinx.android.synthetic.main.activity_main.*


class ProjectActivity : AppCompatActivity(), View.OnClickListener {

    private var mProjectModel: ProjectModel? = null
    private var mScreenAdapter: ScreenAdapter? = null

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
        supportActionBar!!.subtitle =
            (mProjectModel!!.updated * DateUtils.SECOND_IN_MILLIS).toDuration(this)

        button_empty.setOnClickListener(this)

        fetchScreens()
    }

    private fun fetchScreens() {
        textView_empty.visibility = View.GONE
        button_empty.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = true
        NetworkHelper.getScreens(this, mProjectModel!!.id, object : ScreensCallback() {
            override fun onSuccess(screens: List<ScreenModel>) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    recyclerView.visibility = View.VISIBLE
                    recyclerView.setHasFixedSize(true)
                    mScreenAdapter = ScreenAdapter(this@ProjectActivity, screens)
                    recyclerView.adapter = mScreenAdapter
                }
            }

            override fun onFail(errorMessage: String?) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    textView_empty.visibility = View.VISIBLE
                    button_empty.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@ProjectActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        if (v == button_empty) {
            fetchScreens()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mScreenAdapter != null) {
            mScreenAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

}
