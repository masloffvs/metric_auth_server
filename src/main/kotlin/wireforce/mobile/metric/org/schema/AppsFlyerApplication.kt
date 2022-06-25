package wireforce.mobile.metric.org.schema

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar

/**
 * Application
 */
object AppsFlyerApplication : Table<Nothing>("t_application") {
  val uid = varchar("uid").primaryKey()
  val package_name = varchar("package_name")
  val fingerprint = varchar("fingerprint")
  val created_at = datetime("created_at")
}