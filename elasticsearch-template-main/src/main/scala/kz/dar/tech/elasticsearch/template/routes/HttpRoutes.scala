package kz.dar.tech.elasticsearch.template.routes

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives.{complete, concat, get, pathEndOrSingleSlash, pathPrefix}
import akka.http.scaladsl.server.Route
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import kz.dar.tech.elasticsearch.template.service.PostHandleService

import scala.concurrent.ExecutionContext

class HttpRoutes(elasticSearchClient: ElasticClient)(implicit system: ActorSystem[_],
                                                     implicit val executionContext: ExecutionContext,
                                                     config: Config)  {

  val routes: Route = pathPrefix("api") {
    pathPrefix("v1") {
      concat(
        pathEndOrSingleSlash {
          get {
            complete("Health check! Hello!")
          }
        },
        //new PostRoute(elasticSearchClient).routes
        new BookShopRoute(elasticSearchClient).routes
      )
    }
  }

}
