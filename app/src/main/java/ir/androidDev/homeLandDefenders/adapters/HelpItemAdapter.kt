package ir.androidDev.homeLandDefenders.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import ir.androidDev.homeLandDefenders.dataModels.HelpItem
import ir.androidDev.homeLandDefenders.databinding.RecyclerItemHelpBinding

class HelpItemAdapter(
	private val context: Context,
	private val helpItems: MutableList<HelpItem>
) : RecyclerView.Adapter<HelpItemAdapter.ViewHolder>() {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(RecyclerItemHelpBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val helpItem = helpItems[position]
		
		/* set views texts */
		holder.title.text = helpItem.title
		holder.text.text = HtmlCompat.fromHtml(helpItem.text!!, HtmlCompat.FROM_HTML_MODE_COMPACT)
		
		/* get image id */
		val imageUri = "drawable/help_image_$position"
		val imageId = context.resources.getIdentifier(imageUri, null, context.packageName)
		
		/* set imageView's image */
		holder.image.setImageResource(imageId)
	}
	
	override fun getItemCount(): Int = helpItems.size
	
	class ViewHolder(itemView: RecyclerItemHelpBinding) : RecyclerView.ViewHolder(itemView.root) {
		val title: TextView
		val image: ImageView
		val text: TextView
		
		init {
			title = itemView.tvRecyclerItemHelpTitle
			image = itemView.ivRecyclerItemHelpImage
			text = itemView.tvRecyclerItemHelpText
		}
	}
}