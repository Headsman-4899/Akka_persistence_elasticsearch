package kz.dar.tech.elasticsearch.template.service

import akka.actor.typed.ActorSystem
import com.sksamuel.elastic4s.ElasticClient
import com.typesafe.config.Config
import kz.dar.tech.elasticsearch.template.model.{BookShopModel}
import kz.dar.tech.elasticsearch.template.repository.BookShopElasticsearchRepository

import scala.concurrent.{ExecutionContext, Future}

class BookShopHandleService (elasticSearchClient: ElasticClient)(implicit system: ActorSystem[_],
                                                                 implicit val executionContext: ExecutionContext,
                                                                 config: Config) {

  implicit val bookShopElasticRepository: BookShopElasticsearchRepository = new BookShopElasticsearchRepository(elasticSearchClient)

  def create(book: BookShopModel): Future[BookShopModel] = {
    bookShopElasticRepository.createIndexIfNotExists()
    bookShopElasticRepository.upsert(book)
  }

  def findPostById(id: String): Future[Option[BookShopModel]] = {
    bookShopElasticRepository.find(id)
  }

  def edit(book: BookShopModel): Future[BookShopModel] = {
    bookShopElasticRepository.upsert(book)
  }

  def delete(id: String): Future[String] = {
    bookShopElasticRepository.deleteById(id)
  }

}
