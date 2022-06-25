package wireforce.mobile.metric.org.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.locations.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.application.*
import io.ktor.server.http.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.selects.select
import org.ktorm.dsl.*
import wireforce.mobile.metric.org.context
import wireforce.mobile.metric.org.extractGetQuery
import wireforce.mobile.metric.org.plugins.SessionData
import wireforce.mobile.metric.org.schema.AppsFlyerApplication
import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDateTime
import java.util.*

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.configureRouting() {
  install(AutoHeadResponse)
  install(DoubleReceive)
  install(Locations) {}
  install(StatusPages) {
    exception<AuthenticationException> { call, cause ->
      call.respond(HttpStatusCode.Unauthorized)
    }
    exception<AuthorizationException> { call, cause ->
      call.respond(HttpStatusCode.Forbidden)
    }
  }

  routing {
//    get<MyLocation> {
//      call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
//    }
    // Register nested routes
//    get<Type.Edit> {
//      call.respondText("Inside $it")
//    }
//    get<Type.List> {
//      call.respondText("Inside $it")
//    }

    route("/api") {
      route("/service/appsflyer") {

        /**
         * Register app
         */
        get("/procedure/registerApp") {
          val packageName = call.extractGetQuery("packageName")
          val fingerprint = call.extractGetQuery("fingerprint")

          with (call.context.databaseConnection) {
            try {
              insert(AppsFlyerApplication) {
                val uid = UUID.randomUUID().toString()

                set(it.uid, uid)
                set(it.package_name, packageName)
                set(it.fingerprint, fingerprint)
                set(it.created_at, LocalDateTime.now())
              }

              call.respond(
                HttpStatusCode.Created,
                mapOf(
                  "status" to "OK"
                )
              )

            } catch (_: SQLIntegrityConstraintViolationException) {
              call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                  "status" to "ERROR"
                )
              )

            }
          }

        }

        /**
         * My apps
         */
        get("/procedure/myApps") {
          val fingerprint = call.extractGetQuery("fingerprint")

          fingerprint?.takeIf { true }.run {
            with (call.context.databaseConnection) {
              val list = from(AppsFlyerApplication)
                .select(
                  AppsFlyerApplication.package_name,
                  AppsFlyerApplication.created_at
                )
                .where {
                  AppsFlyerApplication.fingerprint eq fingerprint.toString()
                }
                .map {
                  mapOf(
                    "package_name" to it[AppsFlyerApplication.package_name],
                    "created_at" to (it[AppsFlyerApplication.created_at] as LocalDateTime).toHttpDateString(),
                  )
                }

              call.respond(
                mapOf(
                  "list" to list
                )
              )
            }
          }
        }

        /**
         * Remove app
         */
        get("/procedure/removeApp") {
          val packageName = call.extractGetQuery("packageName")
          val fingerprint = call.extractGetQuery("fingerprint")

          fingerprint?.takeIf { packageName != null }.run {
            with (call.context.databaseConnection) {
              delete(AppsFlyerApplication) {
                (it.fingerprint eq fingerprint.toString()) and
                    (it.package_name eq packageName.toString())
              }
            }

            call.respond(mapOf(
              "status" to "OK"
            ))
          }
        }
      }
    }
  }
}

@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@Location("/type/{name}")
data class Type(val name: String) {

  @Location("/edit")
  data class Edit(val type: Type)

  @Location("/list/{page}")
  data class List(val type: Type, val page: Int)
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
