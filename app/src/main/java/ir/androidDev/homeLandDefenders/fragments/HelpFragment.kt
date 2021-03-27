package ir.androidDev.homeLandDefenders.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ir.androidDev.homeLandDefenders.R
import ir.androidDev.homeLandDefenders.adapters.HelpItemAdapter
import ir.androidDev.homeLandDefenders.dataModels.HelpItem
import ir.androidDev.homeLandDefenders.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {
	
	/* view binding */
	private var binding: FragmentHelpBinding? = null
	
	/* adapter */
	private var adapter: HelpItemAdapter? = null
	
	/* help items */
	private val helpItems: MutableList<HelpItem> = ArrayList()
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (binding == null) {
			binding = FragmentHelpBinding.inflate(inflater, container, false)
			
			/* fetch the help strings */
			prepareData()
			
			/* setup the recyclerView */
			setupRecyclerView()
		}
		return binding!!.root
	}
	
	/**
	 * fetches the help section data
	 */
	private fun prepareData() {
		val titles = resources.getStringArray(R.array.help_titles)
		val texts = resources.getStringArray(R.array.help_texts)
		
		if (titles.size != texts.size) {
			Toast.makeText(context!!, "مشکلی پیش آمد!", Toast.LENGTH_SHORT).show()
			return
		}
		
		for (i in titles.indices) {
			val helpItem = HelpItem()
			helpItem.title = titles[i]
			helpItem.text = texts[i]
			
			helpItems.add(helpItem)
		}
	}
	
	/**
	 * sets up the recyclerView
	 */
	private fun setupRecyclerView() {
		binding!!.rvHelpFragmentRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		adapter = HelpItemAdapter(context!!, helpItems)
		binding!!.rvHelpFragmentRecyclerView.adapter = adapter
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		binding = null
	}
}