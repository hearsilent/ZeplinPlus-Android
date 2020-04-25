package hearsilent.zeplin.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hearsilent.zeplin.R
import hearsilent.zeplin.models.ScreenModel
import kotlinx.android.synthetic.main.activity_screen.*

class ScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen)

        setUpViews()
    }

    private fun setUpViews() {
        setSupportActionBar(toolbar)

        val screen = jacksonObjectMapper().readerFor(ScreenModel::class.java)
            .readValue<ScreenModel>(intent.extras!!.getString("screen"))
        supportActionBar!!.title = screen.name
        Glide.with(applicationContext).load(screen.image.original_url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(photoView)
    }

}