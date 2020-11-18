package hearsilent.zeplin.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hearsilent.zeplin.databinding.ActivityScreenBinding
import hearsilent.zeplin.extensions.LongExtension.toDuration
import hearsilent.zeplin.models.ScreenModel


class ScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityScreenBinding

    private var mScreenModel: ScreenModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViews()
    }

    private fun setUpViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mScreenModel = jacksonObjectMapper().readerFor(ScreenModel::class.java)
            .readValue<ScreenModel>(intent.extras!!.getString("screen"))
        supportActionBar!!.title = mScreenModel!!.name
        supportActionBar!!.subtitle =
            (mScreenModel!!.updated * DateUtils.SECOND_IN_MILLIS).toDuration(this)

        binding.buttonEmpty.setOnClickListener(this)

        loadImage()
    }

    private fun loadImage() {
        binding.progressBar.visibility = View.VISIBLE
        Glide.with(applicationContext).load(mScreenModel!!.image.original_url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    binding.textViewEmpty.visibility = View.VISIBLE
                    binding.buttonEmpty.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(binding.photoView)
    }

    override fun onClick(v: View?) {
        if (v == binding.buttonEmpty) {
            binding.textViewEmpty.visibility = View.GONE
            binding.buttonEmpty.visibility = View.GONE
            loadImage()
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