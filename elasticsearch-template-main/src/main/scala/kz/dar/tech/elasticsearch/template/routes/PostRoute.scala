package kz.dar.tech.elasticsearch.template.routes

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.Route
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import kz.dar.tech.elasticsearch.template.model.PostModel
import kz.dar.tech.elasticsearch.template.util.Codec
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import kz.dar.tech.elasticsearch.template.service.PostHandleService

import scala.concurrent.{ExecutionContext, Future}

/**
 * Create by Yerke
 * 01.03.2020
 * @param elasticSearchClient
 * @param system
 * @param executionContext
 * @param config
 */
class PostRoute(elasticSearchClient: ElasticClient)(implicit system: ActorSystem[_],
                                                    implicit val executionContext: ExecutionContext,
                                                    config: Config) extends Codec with FailFastCirceSupport {

  val postService = new PostHandleService(elasticSearchClient)

  val routes: Route = {
    createPost~
    getPost~
    editPost~
    deletePost
  }

  def createPost: Route = {
    pathPrefix("post" / "create") {
      post {
        entity(as[PostModel]) { entity =>
          complete(postService.create(entity))
        }
      }
    }
  }

  def getPost: Route = {
      pathPrefix("post" / "get" / Segment) { id =>
        get {
          complete(postService.findPostById(id))
        }
      }
  }

  def editPost: Route = {
    pathPrefix("post" / "edit") {
      put {
        entity(as[PostModel]) { entity =>
          complete(postService.edit(entity))
        }
      }
    }
  }

  def deletePost: Route = {
    pathPrefix("post" / "delete" / Segment) { id =>
      get {
        complete(postService.delete(id))
      }
    }
  }




}
