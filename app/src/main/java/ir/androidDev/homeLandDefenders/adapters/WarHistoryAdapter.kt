package ir.androidDev.homeLandDefenders.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
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
import ir.androidDev.homeLandDefenders.databinding.RecyclerItemWarHistoryBinding

class WarHistoryAdapter(
	private val context: Context,
	private val warHistoryParts: MutableList<WarHistoryPart>,
	private val canDownloadPhoto: Boolean
) : RecyclerView.Adapter<WarHistoryAdapter.ViewHolder>() {
	
	/* placeholders */
	private val placeholder: Drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_photo_24, null)!!
	private val dlPlaceholder: Drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_round_arrow_circle_down_24, null)!!
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(RecyclerItemWarHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val warHistoryPart = warHistoryParts[position]
		
		/* on image click download */
		if (!canDownloadPhoto) holder.photo.setOnClickListener {
			if (!warHistoryPart.imageLoaded) {
				Picasso.get().load(warHistoryPart.imgUrl).centerInside().fit().placeholder(placeholder)
					.into(holder.photo, object : Callback {
						override fun onSuccess() {
							warHistoryPart.imageLoaded = true
						}
						
						override fun onError(e: Exception?) {
							holder.photo.setImageDrawable(dlPlaceholder)
						}
					})
			}
		}
		
		/* other views */
		holder.text.text = warHistoryPart.text
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(warHistoryPart.imgUrl).centerInside().fit().networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.into(holder.photo, object : Callback {
				override fun onSuccess() {
					warHistoryPart.imageLoaded = true
				}
				
				override fun onError(e: Exception?) {
					if (canDownloadPhoto) Picasso.get().load(warHistoryPart.imgUrl).centerInside().fit().placeholder(placeholder).into(holder.photo)
					else holder.photo.setImageDrawable(dlPlaceholder)
				}
			})
	}
	
	override fun getItemCount(): Int = warHistoryParts.size
	
	class ViewHolder(itemView: RecyclerItemWarHistoryBinding) : RecyclerView.ViewHolder(itemView.root) {
		var photo: ImageView
		var text: TextView
		
		init {
			photo = itemView.ivRecyclerItemWarHistoryPhoto
			text = itemView.tvRecyclerItemWarHistoryText
		}
	}
}