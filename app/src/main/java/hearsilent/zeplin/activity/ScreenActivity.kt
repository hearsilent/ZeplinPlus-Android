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
import hearsilent.zeplin.R
import hearsilent.zeplin.extensions.LongExtension.toDuration
import hearsilent.zeplin.models.ScreenModel
import kotlinx.android.synthetic.main.activity_screen.*


class ScreenActivity : AppCompatActivity(), View.OnClickListener {

    private var mScreenModel: ScreenModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen)

        setUpViews()
    }

    private fun setUpViews() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mScreenModel = jacksonObjectMapper().readerFor(ScreenModel::class.java)
            .readValue<ScreenModel>(intent.extras!!.getString("screen"))
        supportActionBar!!.title = mScreenModel!!.name
        supportActionBar!!.subtitle =
            (mScreenModel!!.updated * DateUtils.SECOND_IN_MILLIS).toDuration(this)

        button_empty.setOnClickListener(this)

        loadImage()
    }

    private fun loadImage() {
        progressBar.visibility = View.VISIBLE
        Glide.with(applicationContext).load(mScreenModel!!.image.original_url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    textView_empty.visibility = View.VISIBLE
                    button_empty.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(photoView)
    }

    override fun onClick(v: View?) {
        if (v == button_empty) {
            textView_empty.visibility = View.GONE
            button_empty.visibility = View.GONE
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