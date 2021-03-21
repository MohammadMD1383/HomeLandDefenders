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
import ir.androidDev.homeLandDefenders.dataModels.BiographyItem
import ir.androidDev.homeLandDefenders.databinding.RecyclerItemBiographyCardBinding

class BiographyItemAdapter(
	private val context: Context,
	private val biographyItems: MutableList<BiographyItem>,
	private val canDownloadPhoto: Boolean,
	private val onViewClick: (id: Int) -> Unit
) : RecyclerView.Adapter<BiographyItemAdapter.ViewHolder>() {
	
	/* items copy list */
	private val biographyItemsCopy: MutableList<BiographyItem> = ArrayList()
	
	/* get a copy of items list */
	init {
		biographyItemsCopy.addAll(biographyItems)
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(RecyclerItemBiographyCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val biographyItem = biographyItems[position]
		
		/* on item click handling */
		holder.view.setOnClickListener { onViewClick(biographyItem.id!!) }
		
		/* other views */
		holder.name.text = biographyItem.name
		holder.birth.text = biographyItem.birth
		
		/* change the "----" to "مفقودالاثر" */
		holder.death.text = biographyItem.death.let { if (it.equals("----")) context.getString(R.string.biography_string_dead_body_not_found) else it }
		holder.rank.text = biographyItem.rank
		
		/* add prefix to age */
		val mAge = context.getString(R.string.biography_adapter_age) + biographyItem.age
		holder.age.text = mAge
		
		/* generate image placeholder */
		val placeholder = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_baseline_photo_24, null)!!
		
		/* if the image url is "flower" it will be loaded locally and picasso will be skipped */
		if (biographyItem.imgUrl!!.toLowerCase() == "flower") {
			holder.photo.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.flower, null))
			return
		}
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(biographyItem.imgUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size)
			.into(holder.photo, object : Callback {
				override fun onSuccess() {}
				override fun onError(e: Exception?) {
					if (canDownloadPhoto)
						Picasso.get().load(biographyItem.imgUrl).placeholder(placeholder)
							.resizeDimen(R.dimen.thumbnail_size, R.dimen.thumbnail_size).into(holder.photo)
				}
			})
	}
	
	override fun getItemCount(): Int = biographyItems.size
	
	/**
	 * filters the data in recycler view by searched value
	 */
	fun filter(text: String?) {
		biographyItems.clear()
		
		if (text == null || text.isEmpty()) biographyItems.addAll(biographyItemsCopy)
		else {
			biographyItemsCopy.forEach {
				if (it.name!!.contains(text)) biographyItems.add(it)
			}
		}
		
		notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: RecyclerItemBiographyCardBinding) : RecyclerView.ViewHolder(itemView.root) {
		val view: CardView
		val photo: ImageView
		val name: TextView
		val birth: TextView
		val death: TextView
		val rank: TextView
		val age: TextView
		
		init {
			view = itemView.crdRecyclerItemBiographyCardCardView
			photo = itemView.ivRecyclerItemBiographyCardPhoto
			
			name = itemView.tvRecyclerItemBiographyCardName
			name.isSelected = true
			
			birth = itemView.tvRecyclerItemBiographyCardBirth
			death = itemView.tvRecyclerItemBiographyCardDeath
			
			rank = itemView.tvRecyclerItemBiographyCardRank
			rank.isSelected = true
			
			age = itemView.tvRecyclerItemBiographyCardAge
		}
	}
}
