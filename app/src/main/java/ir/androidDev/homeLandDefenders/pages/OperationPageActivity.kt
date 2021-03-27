package ir.androidDev.homeLandDefenders.pages

import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import ir.androidDev.homeLandDefenders.CustomizableActivity
import ir.androidDev.homeLandDefenders.OperationActivity
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.databinding.ActivityOperationPageBinding

class OperationPageActivity : CustomizableActivity() {
	
	/* view binding */
	private lateinit var binding: ActivityOperationPageBinding
	
	/* database */
	private val database = Database(this)
	
	/* auto download permission */
	private var autoDownload = false
	
	/* image load state */
	private var imageLoaded = false
	
	/* image url */
	private lateinit var imageUrl: String
	
	/* placeholders */
	private lateinit var placeholder: Drawable
	private lateinit var dlPlaceholder: Drawable
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		binding = ActivityOperationPageBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		
		/* make the title marquee */
		binding.tvOperationPageActivityTitleTv.isSelected = true
		
		/* check parameters passed in intent */
		checkIntentParams()
		
		/* get data and show on page */
		getAndShowData()
		
		/* back to OperationActivity */
		binding.ivOperationPageActivityBack.setOnClickListener { finish() }
		
		/* save scroll position */
		binding.fabOperationPageActivitySaveState.setOnClickListener {
			val cv = ContentValues()
			
			cv.put("ext_id", intent.getIntExtra(OperationActivity.BIO_ID, 0))
			cv.put("scroll", binding.nsvOperationPageActivityMainContainer.scrollY)
			database.updateBy(Database.TBL_SCROLL, cv, "name", Database.TBL_OPERATION)
			
			Toast.makeText(this, getString(R.string.save_state_string_saved), Toast.LENGTH_SHORT).show()
		}
		
		/* download image by click */
		if (!autoDownload) binding.ivOperationPageActivityTopPhoto.setOnClickListener {
			if (!imageLoaded) {
				Picasso.get().load(imageUrl).centerInside().fit().placeholder(placeholder)
					.into(binding.ivOperationPageActivityTopPhoto, object : Callback {
						override fun onSuccess() {
							imageLoaded = true
						}
						
						override fun onError(e: Exception?) {
							binding.ivOperationPageActivityTopPhoto.setImageDrawable(dlPlaceholder)
						}
					})
			}
		}
		
		/* on scroll hide/show fab */
		var oldI = 0
		binding.apbOperationPageActivityAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, i ->
			binding.fabOperationPageActivitySaveState.let { if (i < oldI) it.hide() else it.show() }
			oldI = i + 1
		})
	}
	
	/**
	 * checks that this activity started with all required parameters
	 */
	private fun checkIntentParams() {
		if (!intent.hasExtra(OperationActivity.BIO_ID)) finish()
	}
	
	/**
	 * claims and shows the data gotten from database
	 */
	private fun getAndShowData() {
		val cursor = database.getRowById(Database.TBL_OPERATION, intent.getIntExtra(OperationActivity.BIO_ID, 0))
		
		binding.tvOperationPageActivityTitleTv.text = cursor.getString(1)
		binding.tvOperationPageActivityMainTv.text = cursor.getString(4)
		
		/* get permission for download */
		autoDownload = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download").getInt(1) == 1
		
		/* generate image url and placeholder */
		imageUrl = cursor.getString(3)
		placeholder = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_photo_24, null)!!
		dlPlaceholder = ResourcesCompat.getDrawable(resources, R.drawable.ic_round_arrow_circle_down_24, null)!!
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(imageUrl).centerInside().fit().networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.into(binding.ivOperationPageActivityTopPhoto, object : Callback {
				override fun onSuccess() {
					imageLoaded = true
				}
				
				override fun onError(e: Exception?) {
					if (autoDownload) Picasso.get().load(imageUrl).centerInside().fit().placeholder(placeholder)
						.into(binding.ivOperationPageActivityTopPhoto)
					else binding.ivOperationPageActivityTopPhoto.setImageDrawable(dlPlaceholder)
				}
			})
		
		/* scroll to saved position if provided */
		if (intent.hasExtra(OperationActivity.SCROLL_P)) {
			Handler(Looper.getMainLooper()).postDelayed({
				binding.nsvOperationPageActivityMainContainer.smoothScrollTo(0, intent.getIntExtra(OperationActivity.SCROLL_P, 0), 1500)
			}, 0)
		}
	}
}