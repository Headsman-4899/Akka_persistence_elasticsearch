package kz.dar.tech.akka.persistence.template.route

import akka.http.scaladsl.server.{Directives, Route}

class SwaggerSite extends Directives {
  val swaggerSiteRoute: Route =
    path("swagger") {
      getFromResource("swagger-ui/index.html")
    } ~ getFromResourceDirectory("swagger-ui")
}
