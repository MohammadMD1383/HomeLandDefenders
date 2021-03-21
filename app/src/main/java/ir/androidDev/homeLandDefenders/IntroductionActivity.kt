package ir.androidDev.homeLandDefenders

import android.os.Bundle
import android.view.View
import ir.androidDev.homeLandDefenders.databinding.ActivityIntroductionBinding

class IntroductionActivity : CustomizableActivity() {
	
	/* view binding */
	private lateinit var binding: ActivityIntroductionBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		/* force direction to RTL */
		window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
		
		super.onCreate(savedInstanceState)
		binding = ActivityIntroductionBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)
		
		/* make the title marquee */
		binding.tvIntroductionActivityTitleTv.isSelected = true
		
		/* back to home page by button */
		binding.ivIntroductionActivityBack.setOnClickListener { finish() }
	}
}