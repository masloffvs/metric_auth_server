package wireforce.mobile.metric.org

import org.ktorm.database.Database

class Context {
  /**
   * Database active connection
   */
  val databaseConnection by lazy {
    Database
      .connect(
        url = "jdbc:mysql://localhost:3306/metric_auth_database",
        user = "root",
        password = "root"
      )
  }
}