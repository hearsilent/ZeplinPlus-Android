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
import hearsilent.zeplin.databinding.ItemProjectBinding
import hearsilent.zeplin.extensions.LongExtension.toDuration
import hearsilent.zeplin.models.ProjectModel

class ProjectAdapter(
    private val mContext: Context,
    private val mList: List<ProjectModel>
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val binding = ItemProjectBinding.bind(view)

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
            .into(holder.binding.imageView)
        holder.binding.textViewPlatform.text = model.platform
        holder.binding.textViewName.text = model.name

        holder.binding.textViewUpdated.text =
            (model.updated * DateUtils.SECOND_IN_MILLIS).toDuration(mContext)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}