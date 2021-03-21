package ir.androidDev.homeLandDefenders

import android.os.Bundle
import android.view.View
import ir.androidDev.homeLandDefenders.databinding.ActivityVeteransBinding

class VeteransActivity : CustomizableActivity() {
	
	/* view binding */
	private lateinit var binding: ActivityVeteransBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		binding = ActivityVeteransBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		
		/* make the title marquee */
		binding.tvVeteransActivityTitleTv.isSelected = true
		
		/* back to home button */
		binding.ivVeteransActivityBack.setOnClickListener { finish() }
	}
}