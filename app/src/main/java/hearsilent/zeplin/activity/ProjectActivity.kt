package hearsilent.zeplin.activity

import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hearsilent.zeplin.R
import hearsilent.zeplin.adapter.ScreenAdapter
import hearsilent.zeplin.callback.ScreensCallback
import hearsilent.zeplin.extensions.LongExtension.toDuration
import hearsilent.zeplin.libs.Constant
import hearsilent.zeplin.libs.ScreenModelDataSourceFactory
import hearsilent.zeplin.models.ProjectModel
import hearsilent.zeplin.models.ScreenModel
import kotlinx.android.synthetic.main.activity_main.*


class ProjectActivity : AppCompatActivity(), View.OnClickListener {

    private var mProjectModel: ProjectModel? = null
    private val mScreenAdapter: ScreenAdapter by lazy {
        ScreenAdapter(this@ProjectActivity)
    }

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

        recyclerView.adapter = mScreenAdapter
        fetchScreens()
    }

    private fun fetchScreens() {
        textView_empty.visibility = View.GONE
        button_empty.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = true
        val dataSourceFactory = ScreenModelDataSourceFactory(this, mProjectModel!!.id, object : ScreensCallback() {
            override fun onSuccess(screens: List<ScreenModel>) {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    recyclerView.visibility = View.VISIBLE
                    recyclerView.setHasFixedSize(true)
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
        val pagedListConfig =
            PagedList.Config.Builder().setPageSize(Constant.PAGE_SIZE_OF_SCREENS).setEnablePlaceholders(false).build()

        val livePagedList = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
        livePagedList.observe(this, Observer { pagedList: PagedList<ScreenModel> ->
            mScreenAdapter.submitList(pagedList)
        })
    }

    override fun onClick(v: View?) {
        if (v == button_empty) {
            fetchScreens()
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
