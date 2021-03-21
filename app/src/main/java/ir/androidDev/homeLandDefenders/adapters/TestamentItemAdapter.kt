package ir.androidDev.homeLandDefenders.adapters

import android.content.Context
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
import ir.androidDev.homeLandDefenders.dataModels.TestamentItem
import ir.androidDev.homeLandDefenders.databinding.RecyclerItemTestamentCardBinding

class TestamentItemAdapter(
	private val context: Context,
	private val testamentItems: MutableList<TestamentItem>,
	private val canDownloadPhoto: Boolean,
	private val onViewClick: (id: Int) -> Unit
) : RecyclerView.Adapter<TestamentItemAdapter.ViewHolder>() {
	
	/* items copy list */
	private val testamentItemsCopy: MutableList<TestamentItem> = ArrayList()
	
	/* copy the items */
	init {
		testamentItemsCopy.addAll(testamentItems)
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(RecyclerItemTestamentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val testamentItem = testamentItems[position]
		
		/* on item click handling */
		holder.view.setOnClickListener { onViewClick(testamentItem.id!!) }
		
		/* other views */
		holder.name.text = testamentItem.name
		
		/* generate image placeholder */
		val placeholder = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_photo_24, null)!!
		
		/* if the image url is "flower" it will be loaded locally and picasso will be skipped */
		if (testamentItem.imgUrl!!.toLowerCase() == "flower") {
			holder.photo.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.flower, null))
			return
		}
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(testamentItem.imgUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size)
			.into(holder.photo, object : Callback {
				override fun onSuccess() {}
				override fun onError(e: Exception?) {
					if (canDownloadPhoto)
						Picasso.get().load(testamentItem.imgUrl).placeholder(placeholder)
							.resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size)
							.into(holder.photo)
				}
			})
	}
	
	override fun getItemCount(): Int = testamentItems.size
	
	/**
	 * filters the data of recycler view by the searched value
	 */
	fun filter(text: String?) {
		testamentItems.clear()
		
		if (text == null || text.isEmpty()) testamentItems.addAll(testamentItemsCopy)
		else {
			testamentItemsCopy.forEach {
				if (it.name!!.contains(text)) testamentItems.add(it)
			}
		}
		
		notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: RecyclerItemTestamentCardBinding) : RecyclerView.ViewHolder(itemView.root) {
		var view: CardView
		var photo: ImageView
		var name: TextView
		
		init {
			view = itemView.crdRecyclerItemTestamentCardCardView
			photo = itemView.ivRecyclerItemTestamentCardPhoto
			name = itemView.tvRecyclerItemTestamentCardName
		}
	}
}