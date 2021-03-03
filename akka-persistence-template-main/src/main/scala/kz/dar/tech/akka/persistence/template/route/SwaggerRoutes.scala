package kz.dar.tech.akka.persistence.template.route

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import com.typesafe.config.Config

class SwaggerRoutes(implicit config: Config)  extends SwaggerHttpService {

  override def apiClasses = Set(
    classOf[EmployeeRoutes]
  )

  override val schemes: List[String] = List(config.getString("http-server.swagger-schemes"))
  override val host: String = config.getString("http-server.swagger-host") + "/api/v1"
  override val info: Info = Info(version = "0.0.1")
  //override val externalDocs: Option[ExternalDocumentation] = Some(new ExternalDocumentation().description("Core Docs").url("http://acme.com/docs"))
  //override val securitySchemeDefinitions = Map("basicAuth" -> authenticateOAuth2Async("asa-pro", authenticate))
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")

}
