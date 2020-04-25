package hearsilent.zeplin.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import hearsilent.zeplin.R
import hearsilent.zeplin.activity.ProjectActivity
import hearsilent.zeplin.extensions.LongExtension.toDays
import hearsilent.zeplin.extensions.LongExtension.toHours
import hearsilent.zeplin.extensions.LongExtension.toMinutes
import hearsilent.zeplin.extensions.LongExtension.toMonths
import hearsilent.zeplin.extensions.LongExtension.toWeeks
import hearsilent.zeplin.extensions.LongExtension.toYears
import hearsilent.zeplin.models.ProjectModel
import kotlinx.android.synthetic.main.item_project.view.*

class ProjectAdapter(
    private val mContext: Context,
    private val mList: List<ProjectModel>
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return
            }
            val model = mList[adapterPosition]
            val intent =
                Intent(mContext, ProjectActivity::class.java).apply {
                    putExtra(
                        "project",
                        ObjectMapper().writeValueAsString(model)
                    )
                }
            mContext.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_project, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val model = mList[position]
        Glide.with(mContext.applicationContext).load(model.thumbnail)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.itemView.imageView)
        holder.itemView.textView_platform.text = model.platform
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