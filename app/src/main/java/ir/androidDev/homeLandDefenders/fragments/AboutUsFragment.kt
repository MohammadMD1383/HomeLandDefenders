package ir.androidDev.homeLandDefenders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.androidDev.homeLandDefenders.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {
	
	/* view binding */
	private var binding: FragmentAboutUsBinding? = null
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (binding == null) {
			binding = FragmentAboutUsBinding.inflate(inflater, container, false)
		}
		return binding!!.root
	}
}