package ir.androidDev.homeLandDefenders.connections

/**
 * the Url class provides all urls that are used in the app
 */
object Url {
	/* cookie server */
	const val SERVER = "http://hld-repo.ihweb.ir/"
	
	/* repositories */
	const val REPOSITORY_V1 = "${SERVER}api/v1/"
	
	/* app info */
	const val APP_INFO = "app-info/"
	
	/* links redirect */
	const val LINK = "${SERVER}links/channel/"
	const val DOWNLOAD = "${SERVER}links/download/app/"
	
	/* app version */
	const val APP_VERSION_CODE = "${SERVER + APP_INFO}version-code"
	
	/* option availability */
	const val OPTIONS = "${SERVER}options/get/"
	
	/* send data */
	const val COMMENT = "${SERVER}send-comment/"
	const val CONTENT = "${SERVER}send-content/"
	
	/* modes */
	const val GET = "get-data/"
	const val NEW = "new-data/"
	const val CHECK_NEW = "check-new-data/"
	
	/* items */
	const val BIOGRAPHY = "biography/"
	const val TESTAMENT = "testament/"
	const val OPERATION = "operation/"
	const val WAR_HISTORY = "war-history/"
	
	/* social media */
	const val TELEGRAM = "telegram/"
	const val SOROUSH = "soroush/"
	const val INSTAGRAM = "instagram/"
	
	/* option values */
	const val SEND_COMMENT = "can_send_comment/"
	const val SEND_CONTENT = "can_send_content/"
}