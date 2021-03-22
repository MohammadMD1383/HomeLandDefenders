package ir.androidDev.homeLandDefenders.pages

import android.content.ContentValues
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
import ir.androidDev.homeLandDefenders.BiographyActivity
import ir.androidDev.homeLandDefenders.CustomizableActivity
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.database.Database
import ir.androidDev.homeLandDefenders.databinding.ActivityBiographyPageBinding

class BiographyPageActivity : CustomizableActivity() {
	
	/* view binding */
	private lateinit var binding: ActivityBiographyPageBinding
	
	/* database */
	private val database = Database(this)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		binding = ActivityBiographyPageBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		
		/* make the title marquee */
		binding.tvBiographyPageActivityTitleTv.isSelected = true
		
		/* check parameters passed in intent */
		checkIntentParams()
		
		/* get data and show on page */
		getAndShowData()
		
		/* back to BiographyActivity */
		binding.ivBiographyPageActivityBack.setOnClickListener { finish() }
		
		/* save scroll position */
		binding.fabBiographyPageActivitySaveState.setOnClickListener {
			val cv = ContentValues()
			cv.put("ext_id", intent.getIntExtra("bio_id", 0))
			cv.put("scroll", binding.nsvBiographyPageActivityMainContainer.scrollY)
			database.updateBy(Database.TBL_SCROLL, cv, "name", Database.TBL_BIOGRAPHY)
			
			Toast.makeText(this, getString(R.string.save_state_string_saved), Toast.LENGTH_SHORT).show()
		}
		
		/* on scroll hide/show fab */
		var oldI = 0
		binding.apbBiographyPageActivityAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, i ->
			binding.fabBiographyPageActivitySaveState.let { if (i < oldI) it.hide() else it.show() }
			oldI = i + 1
		})
	}
	
	/**
	 * checks that this activity started with all required parameters
	 */
	private fun checkIntentParams() {
		if (!intent.hasExtra(BiographyActivity.BIO_ID)) finish()
	}
	
	/**
	 * claims and shows the data gotten from database
	 */
	private fun getAndShowData() {
		val cursor = database.getRowById(Database.TBL_BIOGRAPHY, intent.getIntExtra(BiographyActivity.BIO_ID, 0))
		
		binding.tvBiographyPageActivityTitleTv.text = cursor.getString(1)
		binding.tvBiographyPageActivityMainTv.text = cursor.getString(8)
		
		/* get permission for download */
		val ad = database.getRowBy(Database.TBL_SETTINGS, "name", "auto_download").getInt(1) == 1
		
		/* generate image url and placeholder */
		val url = cursor.getString(7)
		val placeholder = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_photo_24, null)!!
		
		/* check if there is no photo then set the visibility to GONE */
		if (url.contains("void(0)", true)) binding.frBiographyPageActivityImageContainer.visibility = View.GONE
		
		/* sets photo if cached else downloads it and then shows */
		Picasso.get().load(url).centerInside().fit().networkPolicy(NetworkPolicy.OFFLINE).placeholder(placeholder)
			.into(binding.ivBiographyPageActivityTopPhoto, object : Callback {
				override fun onSuccess() {}
				override fun onError(e: Exception?) {
					if (ad)
						Picasso.get().load(url).centerInside().fit().placeholder(placeholder)
							.into(binding.ivBiographyPageActivityTopPhoto)
				}
			})
		
		if (intent.hasExtra(BiographyActivity.SCROLL_P)) {
			Handler(Looper.getMainLooper()).postDelayed({
				binding.nsvBiographyPageActivityMainContainer.smoothScrollTo(0, intent.getIntExtra(BiographyActivity.SCROLL_P, 0), 1500)
			}, 0)
		}
	}
}