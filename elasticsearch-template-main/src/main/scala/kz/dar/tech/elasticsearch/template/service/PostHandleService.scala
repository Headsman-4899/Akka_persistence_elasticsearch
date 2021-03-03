package kz.dar.tech.elasticsearch.template.service

import akka.actor.typed.ActorSystem
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import kz.dar.tech.elasticsearch.template.model.PostModel
import kz.dar.tech.elasticsearch.template.repository.PostElasticsearchRepository

import scala.concurrent.{ExecutionContext, Future}

/**
 * Create by Yerke
 * @param elasticSearchClient
 * @param system
 * @param executionContext
 * @param config
 */
class PostHandleService(elasticSearchClient: ElasticClient)(implicit system: ActorSystem[_],
                                                            implicit val executionContext: ExecutionContext,
                                                            config: Config) {

  implicit val postElasticRepository: PostElasticsearchRepository = new PostElasticsearchRepository(elasticSearchClient)

  def create(post: PostModel): Future[PostModel] = {
    postElasticRepository.createIndexIfNotExists()
    postElasticRepository.upsert(post)
  }

  def findPostById(id: String): Future[Option[PostModel]] = {
    postElasticRepository.find(id)
  }

  def edit(post: PostModel): Future[PostModel] = {
    postElasticRepository.upsert(post)
  }

  def delete(id: String): Future[String] = {
    postElasticRepository.deleteById(id)
  }

  /*def findAll: Future[Seq[PostModel]] = {
    //postElasticRepository.fi
    null
  }*/

}
