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
import kotlinx.android.synthetic.main.fragment_contact_us.view.*


class ContactUsFragment : Fragment() {
	
	private var rootView: View? = null
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_contact_us, container, false)
			
			/* setup the social media links */
			setupLinks()
		}
		return rootView
	}
	
	/**
	 * binds the on click event with the social media link to open
	 */
	private fun setupLinks() {
		rootView!!.iv_contactUsFragment_telegram.setOnClickListener { openLink(Url.let { it.LINK + it.TELEGRAM }) }
		rootView!!.iv_contactUsFragment_soroush.setOnClickListener { openLink(Url.let { it.LINK + it.SOROUSH }) }
		rootView!!.iv_contactUsFragment_gap.setOnClickListener { openLink(Url.let { it.LINK + it.GAP }) }
	}
	
	/**
	 * opens the given link
	 *
	 * @param link the given link
	 */
	private fun openLink(link: String) {
		startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(link)))
	}
}