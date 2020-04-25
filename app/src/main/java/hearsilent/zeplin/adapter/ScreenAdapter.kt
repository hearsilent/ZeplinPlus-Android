package hearsilent.zeplin.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import hearsilent.zeplin.R
import hearsilent.zeplin.activity.ScreenActivity
import hearsilent.zeplin.extensions.LongExtension.toDays
import hearsilent.zeplin.extensions.LongExtension.toHours
import hearsilent.zeplin.extensions.LongExtension.toMinutes
import hearsilent.zeplin.extensions.LongExtension.toMonths
import hearsilent.zeplin.extensions.LongExtension.toWeeks
import hearsilent.zeplin.extensions.LongExtension.toYears
import hearsilent.zeplin.models.ScreenModel
import kotlinx.android.synthetic.main.item_screen.view.*
import java.util.*


class ScreenAdapter(
    private val mContext: Context,
    private val mList: List<ScreenModel>
) : RecyclerView.Adapter<ScreenAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.view_container.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return
            }
            val model = mList[adapterPosition]
            val intent =
                Intent(mContext, ScreenActivity::class.java).apply {
                    putExtra(
                        "screen",
                        ObjectMapper().writeValueAsString(model)
                    )
                }
            val pairs: MutableList<Pair<View, String>> = ArrayList()
            pairs.add(Pair.create(itemView.imageView, "screen"))
            val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation((mContext as Activity), *pairs.toTypedArray())
            mContext.startActivity(intent, options.toBundle())
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_screen, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val model = mList[position]
        val set = ConstraintSet()
        set.clone(holder.itemView as ConstraintLayout)
        set.setDimensionRatio(
            holder.itemView.view_container.id,
            if (model.image.height / model.image.width.toFloat() > 2.165f) "1:2.165"
            else "${model.image.width}:${model.image.height}"
        )
        set.applyTo(holder.itemView)

        Glide.with(mContext.applicationContext).load(model.image.original_url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.itemView.imageView)
        holder.itemView.textView_name.text = model.name

        val duration = System.currentTimeMillis() - model.updated * DateUtils.SECOND_IN_MILLIS
        when {
            duration.toYears() > 0 -> {
                holder.itemView.textView_updated.text =
                    mContext.getString(R.string.duration_year, duration.toYears())
            }
            duration.toMonths() > 0 -> {
                holder.itemView.textView_updated.text =
                    mContext.getString(R.string.duration_month, duration.toMonths())
            }
            duration.toWeeks() > 0 -> {
                holder.itemView.textView_updated.text =
                    mContext.getString(R.string.duration_week, duration.toWeeks())
            }
            duration.toDays() > 0 -> {
                holder.itemView.textView_updated.text =
                    mContext.getString(R.string.duration_day, duration.toDays())
            }
            duration.toHours() > 0 -> {
                holder.itemView.textView_updated.text =
                    mContext.getString(R.string.duration_hour, duration.toHours())
            }
            duration.toMinutes() > 0 -> {
                holder.itemView.textView_updated.text =
                    mContext.getString(R.string.duration_minute, duration.toMinutes())
            }
            else -> {
                holder.itemView.textView_updated.text = mContext.getString(R.string.duration_now)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}