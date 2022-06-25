package wireforce.mobile.metric.org.plugins

sealed interface SessionData {

  /**
   *
   */
  data class RegisterAppsFlyerApps(
    val list: List<RegisterAppsFlyerApp> = listOf()
  ) {
    companion object {
      data class RegisterAppsFlyerApp(
        val packageName: String = "",
        val fingerprint: String = "",
      )
    }
  }
}