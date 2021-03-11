package kz.dar.tech.elasticsearch.template.routes

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.Route
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import kz.dar.tech.elasticsearch.template.model.{BookShopModel, PostModel}
import kz.dar.tech.elasticsearch.template.util.Codec
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import kz.dar.tech.elasticsearch.template.service.{BookShopHandleService, PostHandleService}
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}


class BookShopRoute (elasticSearchClient: ElasticClient)(implicit system: ActorSystem[_],
                                                         implicit val executionContext: ExecutionContext,
                                                         config: Config) extends Codec with FailFastCirceSupport {

  val bookShopService = new BookShopHandleService(elasticSearchClient)

  val routes: Route = {
      createPost~
      getPost~
      editPost~
      deletePost
  }

  def createPost: Route = {
    pathPrefix("bookShop" / "create") {
      post {
        entity(as[BookShopModel]) { entity =>
          complete(bookShopService.create(entity))
        }
      }
    }
  }

  def getPost: Route = {
    pathPrefix("bookShop" / "get" / Segment) { id =>
      get {
        complete(bookShopService.findPostById(id))
      }
    }
  }

  def editPost: Route = {
    pathPrefix("bookShop" / "edit") {
      put {
        entity(as[BookShopModel]) { entity =>
          complete(bookShopService.edit(entity))
        }
      }
    }
  }

  def deletePost: Route = {
    pathPrefix("bookShop" / "delete" / Segment) { id =>
      get {
        complete(bookShopService.delete(id))
      }
    }
  }


}
