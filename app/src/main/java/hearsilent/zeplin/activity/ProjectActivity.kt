package hearsilent.zeplin.activity

import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hearsilent.zeplin.R
import hearsilent.zeplin.adapter.ScreenAdapter
import hearsilent.zeplin.callback.ScreensCallback
import hearsilent.zeplin.databinding.ActivityProjectBinding
import hearsilent.zeplin.extensions.LongExtension.toDuration
import hearsilent.zeplin.libs.Constant
import hearsilent.zeplin.libs.ScreenModelDataSourceFactory
import hearsilent.zeplin.models.ProjectModel
import hearsilent.zeplin.models.ScreenModel


class ProjectActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityProjectBinding

    private var mProjectModel: ProjectModel? = null
    private val mScreenAdapter: ScreenAdapter by lazy {
        ScreenAdapter(this@ProjectActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViews()
    }

    private fun setUpViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.bg_secondary)
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        binding.swipeRefreshLayout.setOnRefreshListener { fetchScreens() }

        mProjectModel = jacksonObjectMapper().readerFor(ProjectModel::class.java)
            .readValue<ProjectModel>(intent.extras!!.getString("project"))
        supportActionBar!!.title = mProjectModel!!.name
        supportActionBar!!.subtitle =
            (mProjectModel!!.updated * DateUtils.SECOND_IN_MILLIS).toDuration(this)

        binding.buttonEmpty.setOnClickListener(this)

        binding.recyclerView.adapter = mScreenAdapter
        fetchScreens()
    }

    private fun fetchScreens() {
        binding.textViewEmpty.visibility = View.GONE
        binding.buttonEmpty.visibility = View.GONE
        binding.swipeRefreshLayout.isRefreshing = true
        val dataSourceFactory =
            ScreenModelDataSourceFactory(this, mProjectModel!!.id, object : ScreensCallback() {
                override fun onSuccess(screens: List<ScreenModel>) {
                    if (isFinishing) {
                        return
                    }
                    runOnUiThread {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.recyclerView.setHasFixedSize(true)
                    }
                }

                override fun onFail(errorMessage: String?) {
                    if (isFinishing) {
                        return
                    }
                    runOnUiThread {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.textViewEmpty.visibility = View.VISIBLE
                        binding.buttonEmpty.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@ProjectActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        val pagedListConfig =
            PagedList.Config.Builder().setPageSize(Constant.PAGE_SIZE_OF_SCREENS)
                .setEnablePlaceholders(false).build()

        val livePagedList = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
        livePagedList.observe(this, { pagedList: PagedList<ScreenModel> ->
            mScreenAdapter.submitList(pagedList)
        })
    }

    override fun onClick(v: View?) {
        if (v == binding.buttonEmpty) {
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
