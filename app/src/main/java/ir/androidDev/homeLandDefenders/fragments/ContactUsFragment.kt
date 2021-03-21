package ir.androidDev.homeLandDefenders.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.connections.Url
import ir.androidDev.homeLandDefenders.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {
	
	/* view binding */
	private var binding: FragmentContactUsBinding? = null
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (binding == null) {
			binding = FragmentContactUsBinding.inflate(inflater, container, false)
			
			/* setup the social media links */
			setupLinks()
		}
		return binding!!.root
	}
	
	/**
	 * binds the on click event with the social media link to open
	 */
	private fun setupLinks() {
		binding!!.ivContactUsFragmentTelegram.setOnClickListener { openLink(Url.let { it.LINK + it.TELEGRAM }) }
		binding!!.ivContactUsFragmentSoroush.setOnClickListener { openLink(Url.let { it.LINK + it.SOROUSH }) }
		binding!!.ivContactUsFragmentGap.setOnClickListener { openLink(Url.let { it.LINK + it.GAP }) }
	}
	
	/**
	 * opens the given link
	 *
	 * @param link the given link
	 */
	private fun openLink(link: String) {
		startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(link)))
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		binding = null
	}
}