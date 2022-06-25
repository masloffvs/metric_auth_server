package wireforce.mobile.metric.org

import io.ktor.server.application.*
import io.ktor.util.*
import wireforce.mobile.metric.org.plugins.*

val computedContext = Context()

fun main(args: Array<String>): Unit =
  io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
  configureRouting()
  configureSecurity()
  configureHTTP()
  configureMonitoring()
  configureSerialization()
  configureSockets()
}

/**
 * Application
 */
val ApplicationCall.context: Context get() = computedContext

/**
 * Application
 */
val Application.context: Context get() = computedContext

fun ApplicationCall.extractGetQuery(name: String) = request.queryParameters.toMap().getOrDefault(name, null)?.first()