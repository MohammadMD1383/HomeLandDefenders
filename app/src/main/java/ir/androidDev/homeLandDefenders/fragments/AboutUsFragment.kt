package ir.androidDev.homeLandDefenders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.androidDev.homeLandDefenders.R

class AboutUsFragment : Fragment() {
	
	private var rootView: View? = null
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_about_us, container, false)
		}
		
		return rootView
	}
}