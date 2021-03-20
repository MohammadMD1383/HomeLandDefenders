package ir.androidDev.homeLandDefenders.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.dataModels.WarHistoryPart
import kotlinx.android.synthetic.main.recycler_item_war_history.view.*

class WarHistoryAdapter(
	private val context: Context,
	private val warHistoryParts: MutableList<WarHistoryPart>,
	private val canDownloadPhoto: Boolean
) : RecyclerView.Adapter<WarHistoryAdapter.ViewHolder>() {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_war_history, parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val warHistoryPart = warHistoryParts[position]
		
		/* other views */
		holder.text!!.text = warHistoryPart.text
		
		/* generate image placeholder */
		val placeholder = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_photo_24, null)!!
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(warHistoryPart.imgUrl).centerInside().fit().networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.into(holder.photo, object : Callback {
				override fun onSuccess() {}
				override fun onError(e: Exception?) {
					if (canDownloadPhoto)
						Picasso.get().load(warHistoryPart.imgUrl).centerInside().fit().placeholder(placeholder).into(holder.photo)
				}
			})
	}
	
	override fun getItemCount(): Int = warHistoryParts.size
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var photo: ImageView? = null
		var text: TextView? = null
		
		init {
			photo = itemView.iv_recyclerItemWarHistory_photo
			text = itemView.tv_recyclerItemWarHistory_text
		}
	}
}