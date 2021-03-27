package ir.androidDev.homeLandDefenders.dataModels

data class BiographyItem(
	var id: Int? = null,
	var name: String? = null,
	var birth: String? = null,
	var death: String? = null,
	var rank: String? = null,
	var age: String? = null,
	var imgUrl: String? = null,
	var imageLoaded: Boolean = false
)