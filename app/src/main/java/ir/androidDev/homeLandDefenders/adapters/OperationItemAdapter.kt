package ir.androidDev.homeLandDefenders.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.dataModels.OperationItem
import ir.androidDev.homeLandDefenders.databinding.RecyclerItemOperationCardBinding

class OperationItemAdapter(
	context: Context,
	private val operationItems: MutableList<OperationItem>,
	private val canDownloadPhoto: Boolean,
	private val onViewClick: (id: Int) -> Unit
) : RecyclerView.Adapter<OperationItemAdapter.ViewHolder>() {
	
	/* items copy list */
	private val operationItemsCopy: MutableList<OperationItem> = ArrayList()
	
	/* placeholders */
	private val placeholder: Drawable
	private val dlPlaceholder: Drawable
	
	/* get copy from items */
	init {
		operationItemsCopy.addAll(operationItems)
		
		placeholder = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_photo_24, null)!!
		dlPlaceholder = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_round_arrow_circle_down_24, null)!!
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(RecyclerItemOperationCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val operationItem = operationItems[position]
		
		/* on item click handling */
		holder.view.setOnClickListener { onViewClick(operationItem.id!!) }
		
		/* on image click download */
		if (!canDownloadPhoto) holder.photo.setOnClickListener {
			if (!operationItem.imageLoaded) {
				Picasso.get().load(operationItem.imgUrl).placeholder(placeholder)
					.resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size).into(holder.photo, object : Callback {
						override fun onSuccess() {
							operationItem.imageLoaded = true
						}
						
						override fun onError(e: Exception?) {
							holder.photo.setImageDrawable(dlPlaceholder)
						}
					})
			}
		}
		
		/* other views */
		holder.name.text = operationItem.name
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(operationItem.imgUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.resizeDimen(R.dimen.zero_dp, R.dimen.operation_thumbnail_size).into(holder.photo, object : Callback {
				override fun onSuccess() {
					operationItem.imageLoaded = true
				}
				
				override fun onError(e: Exception?) {
					if (canDownloadPhoto) Picasso.get().load(operationItem.imgUrl).placeholder(placeholder)
						.resizeDimen(R.dimen.zero_dp, R.dimen.operation_thumbnail_size).into(holder.photo)
					else holder.photo.setImageDrawable(dlPlaceholder)
				}
			})
	}
	
	override fun getItemCount(): Int = operationItems.size
	
	/**
	 * filters the data of recycler view by the searched value
	 */
	fun filter(text: String?) {
		operationItems.clear()
		
		if (text == null || text.isEmpty()) operationItems.addAll(operationItemsCopy)
		else {
			operationItemsCopy.forEach {
				if (it.name!!.contains(text)) operationItems.add(it)
			}
		}
		
		notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: RecyclerItemOperationCardBinding) : RecyclerView.ViewHolder(itemView.root) {
		val view: CardView
		val photo: ImageView
		val name: TextView
		
		init {
			view = itemView.crdRecyclerItemOperationCardCardView
			photo = itemView.ivRecyclerItemOperationCardPhoto
			name = itemView.tvRecyclerItemOperationCardName
		}
	}
}