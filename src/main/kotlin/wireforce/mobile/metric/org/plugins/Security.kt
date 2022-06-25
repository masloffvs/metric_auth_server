package wireforce.mobile.metric.org.plugins

import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import wireforce.mobile.metric.org.context
import wireforce.mobile.metric.org.extractGetQuery

fun Application.configureSecurity() {

  authentication {
    jwt {
      val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
      realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
      verifier(
        JWT
          .require(Algorithm.HMAC256("secret"))
          .withAudience(jwtAudience)
          .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString())
          .build()
      )
      validate { credential ->
        if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
      }
    }
  }

  data class MySession(val count: Int = 0)


  install(Sessions) {
    cookie<MySession>("MY_SESSION") {
      cookie.extensions["SameSite"] = "lax"
    }

    cookie<SessionData.RegisterAppsFlyerApps>("RegisterAppsFlyerApps") {
      cookie.extensions["SameSite"] = "lax"
    }
  }
}
