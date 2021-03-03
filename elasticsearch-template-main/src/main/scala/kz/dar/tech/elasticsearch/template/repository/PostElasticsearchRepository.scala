package kz.dar.tech.elasticsearch.template.repository

import com.sksamuel.elastic4s.{ElasticClient, Response}
import com.sksamuel.elastic4s.requests.delete.DeleteResponse
import com.sksamuel.elastic4s.requests.mappings.{KeywordField, MappingDefinition}
import com.typesafe.config.Config
import kz.dar.tech.elasticsearch.template.model.PostModel
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import kz.dar.tech.elasticsearch.template.util.Codec

import scala.concurrent.{ExecutionContext, Future}

class PostElasticsearchRepository(val elasticSearchClient: ElasticClient)
                                 (implicit val ec: ExecutionContext,
                                  implicit val config: Config) extends ElasticSearchRepository[PostModel] with Codec{


  implicit val manifest: Manifest[PostModel] = Manifest.classType[PostModel](classOf[PostModel])

  override def encode: PostModel => String = (entity: PostModel) => {
    entity.asJson.noSpaces
  }

  override def decode: String => PostModel = (jsonString: String) => {
    parse(jsonString).toTry.get.as[PostModel].toTry.get
  }

  override def indexName: String = config.getString(s"elastic.dmsUserTasks")

  override def shards: Int = config.getInt("elastic.shards")

  override def replicas: Int = config.getInt("elastic.replicas")


  def deleteById(id: String): Future[String] = {
    val response: Future[Response[DeleteResponse]] = elasticSearchClient.execute {
      deleteById(indexName, id)
    }
    response.map { r =>
      if (r.status == 200) {
        r.result.id
      } else {
        throw r.error.asException
      }
    }
  }

  override def mapping: Option[MappingDefinition] = Some(properties(
    KeywordField("id"),
    KeywordField("name"),
    KeywordField("postId"),
    KeywordField("address")
  ))
}
