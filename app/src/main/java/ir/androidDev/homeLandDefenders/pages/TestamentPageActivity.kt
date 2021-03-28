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
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.TestamentActivity
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.databinding.ActivityTestamentPageBinding

class TestamentPageActivity : CustomizableActivity() {
	
	/* view binding */
	private lateinit var binding: ActivityTestamentPageBinding
	
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
		binding = ActivityTestamentPageBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		
		/* make the title marquee */
		binding.tvTestamentPageActivityTitleTv.isSelected = true
		
		/* check parameters passed in intent */
		checkIntentParams()
		
		/* get data and show on page */
		getAndShowData()
		
		/* back to TestamentActivity */
		binding.ivTestamentPageActivityBack.setOnClickListener { finish() }
		
		/* save scroll position */
		binding.fabTestamentPageActivitySaveState.setOnClickListener {
			val cv = ContentValues()
			cv.put("ext_id", intent.getIntExtra(TestamentActivity.BIO_ID, 0))
			cv.put("scroll", binding.nsvTestamentPageActivityMainContainer.scrollY)
			database.updateBy(Database.TBL_SCROLL, cv, "name", Database.TBL_TESTAMENT)
			
			Toast.makeText(this, getString(R.string.save_state_string_saved), Toast.LENGTH_SHORT).show()
		}
		
		/* download image by click */
		if (!autoDownload) binding.ivTestamentPageActivityTopPhoto.setOnClickListener {
			if (!imageLoaded) {
				Picasso.get().load(imageUrl).centerInside().fit().placeholder(placeholder)
					.into(binding.ivTestamentPageActivityTopPhoto, object : Callback {
						override fun onSuccess() {
							imageLoaded = true
						}
						
						override fun onError(e: Exception?) {
							binding.ivTestamentPageActivityTopPhoto.setImageDrawable(dlPlaceholder)
						}
					})
			}
		}
		
		/* on scroll hide/show fab */
		var oldI = 0
		binding.apbTestamentPageActivityAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, i ->
			binding.fabTestamentPageActivitySaveState.let { if (i < oldI) it.hide() else it.show() }
			oldI = i + 1
		})
	}
	
	/**
	 * checks that this activity started with all required parameters
	 */
	private fun checkIntentParams() {
		if (!intent.hasExtra(TestamentActivity.BIO_ID)) finish()
	}
	
	/**
	 * claims and shows the data gotten from database
	 */
	private fun getAndShowData() {
		val cursor = database.getRowById(Database.TBL_TESTAMENT, intent.getIntExtra(TestamentActivity.BIO_ID, 0))
		
		binding.tvTestamentPageActivityTitleTv.text = cursor.getString(1)
		binding.tvTestamentPageActivityMainTv.text = cursor.getString(4)
		
		/* get permission for download */
		val cursorAd = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download")
		autoDownload = cursorAd.getInt(1) == 1
		cursorAd.close()
		
		/* generate image url and placeholder */
		imageUrl = cursor.getString(3)
		cursor.close()
		
		placeholder = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_photo_24, null)!!
		dlPlaceholder = ResourcesCompat.getDrawable(resources, R.drawable.ic_round_arrow_circle_down_24, null)!!
		
		/* check if there is no photo then set the visibility to GONE */
		if (imageUrl.contains("void(0)", true)) binding.frTestamentPageActivityImageContainer.visibility = View.GONE
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(imageUrl).centerInside().fit().networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.into(binding.ivTestamentPageActivityTopPhoto, object : Callback {
				override fun onSuccess() {
					imageLoaded = true
				}
				
				override fun onError(e: Exception?) {
					if (autoDownload) Picasso.get().load(imageUrl).centerInside().fit().placeholder(placeholder)
						.into(binding.ivTestamentPageActivityTopPhoto)
					else binding.ivTestamentPageActivityTopPhoto.setImageDrawable(dlPlaceholder)
				}
			})
		
		/* load scroll position if provided */
		if (intent.hasExtra(TestamentActivity.SCROLL_P)) {
			Handler(Looper.getMainLooper()).postDelayed({
				binding.nsvTestamentPageActivityMainContainer.smoothScrollTo(0, intent.getIntExtra(TestamentActivity.SCROLL_P, 0), 1500)
			}, 0)
		}
	}
}