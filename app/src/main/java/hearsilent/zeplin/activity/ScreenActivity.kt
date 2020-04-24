package hearsilent.zeplin.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import hearsilent.zeplin.R
import kotlinx.android.synthetic.main.activity_screen.*

class ScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen)

        setUpViews()
    }

    private fun setUpViews() {
        val url = intent.extras!!.getString("url")
        Glide.with(applicationContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(photoView)
    }

}