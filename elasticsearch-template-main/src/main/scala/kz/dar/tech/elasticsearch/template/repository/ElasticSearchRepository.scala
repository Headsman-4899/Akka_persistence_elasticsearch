package kz.dar.tech.elasticsearch.template.repository

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.indexes.CreateIndexResponse
import com.sksamuel.elastic4s.requests.indexes.admin.DeleteIndexResponse
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition
import com.sksamuel.elastic4s.requests.searches.{SearchRequest, SearchResponse}
import com.sksamuel.elastic4s.requests.update.UpdateResponse
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ElasticSearchRepository[T <: AnyRef] extends ElasticDsl {
  implicit val ec: ExecutionContext
  implicit val manifest: Manifest[T]

  implicit object EntityIndexable extends Indexable[T] {
    override def json(entity: T): String = encode(entity)
  }

  val log: Logger = LoggerFactory.getLogger(getClass)

  def encode: T => String

  def decode: String => T

  def elasticSearchClient: ElasticClient

  def indexName: String

  def shards: Int

  def replicas: Int

  def createIndexIfNotExists(): Unit = {
    var index = createIndex(indexName).shards(shards).replicas(replicas).indexSetting("mapping", Map("total_fields" -> Map("limit" -> totalMappingFieldLimit)))
    if (mapping.isDefined) {
      index = index.mapping(mapping.get)
    }

    val result: Try[Response[CreateIndexResponse]] = Try(elasticSearchClient.execute(index).await)

    result match {
      case Success(value) =>
        value match {
          case _: RequestSuccess[_] =>
            log.info(s"Index $indexName was successfully created")
          case r: RequestFailure =>
            log.warn(
              s"Index $indexName wasn't created, cause ${r.error.reason}"
            )
        }
      case Failure(_) =>
        log.error(s"Index $indexName already exists")
    }
  }

  def totalMappingFieldLimit: Int = 1000

  def mapping: Option[MappingDefinition] = None

  def dropIndexIfExists(): Unit = {
    val result: Try[Response[DeleteIndexResponse]] =
      Try(
        elasticSearchClient.execute(deleteIndex(indexName)).await
      )

    result match {
      case Success(value) =>
        value match {
          case _: RequestSuccess[_] =>
            log.info(s"Index $indexName was successfully deleted")
          case r: RequestFailure =>
            log.warn(
              s"Index $indexName wasn't deleted, cause ${r.error.reason}"
            )
        }
      case Failure(_) =>
        log.error(s"Index $indexName didn't exist")
    }
  }

  def edit(id: String, query: String): Future[Unit] = {
    val response: Future[Response[UpdateResponse]] = elasticSearchClient.execute {
      update(id).in(indexName).script("ctx._source." + query)
    }

    response.map { r =>
      if (r.isError) {
        throw r.error.asException
      }
    }
  }

  def merge(id: String, entity: T): Future[T] = {
    val response = elasticSearchClient.execute {
      updateById(indexName, id)
        .refresh(RefreshPolicy.IMMEDIATE)
        .docAsUpsert(entity)
    }

    response.map { r =>
      if (r.isSuccess) {
        entity
      } else {
        throw r.error.asException
      }
    }
  }

  def merge(id: String, json: String): Future[Response[UpdateResponse]] = {
    val response: Future[Response[UpdateResponse]] = elasticSearchClient.execute {
      updateById(indexName, id)
        .refresh(RefreshPolicy.IMMEDIATE)
        .docAsUpsert(json)
    }

    response
  }

  def insert(entity: T, id: String): Future[T] = {
    upsert(entity, Some(id), createOnly = true)
  }

  def upsert(entity: T,
             idOpt: Option[String] = None,
             createOnly: Boolean = false): Future[T] = {

    val response = elasticSearchClient.execute {
      var i = indexInto(indexName)
        .doc(entity)
        .createOnly(createOnly)
        .refreshImmediately

      idOpt.foreach { id =>
        i = i.withId(id)
      }

      i
    }

    response.map { r =>
      if (r.isSuccess) {
        entity
      } else {
        throw r.error.asException
      }
    }
  }

  def find(id: String): Future[Option[T]] = {
    val response: Future[Response[SearchResponse]] = elasticSearchClient.execute {
      search(indexName)
        .query(idsQuery(id))
    }

    response.map { r =>
      if (r.isSuccess && r.result.hits.total.value > 0) {
        Option(decode(r.result.hits.hits.head.sourceAsString))
      } else {
        None
      }
    }
  }

  /**
   * Search query
   *
   * @param searchRequest SearchRequest
   * @return
   */
  def search(searchRequest: SearchRequest): Future[Seq[T]] = {
    val result: Future[Response[SearchResponse]] = elasticSearchClient.execute {
      searchRequest
    }

    result.map { r =>
      val hits = r.result.hits.hits.map(_.sourceAsString)
      var res = scala.collection.mutable.ListBuffer.empty[T]
      hits.foreach { h =>
        val t: T = decode(h)
        res += t
      }

      res
    }
  }

  /**
   * Search raw query
   *
   * @param searchRequest SearchRequest
   * @return
   */
  def searchRaw(searchRequest: SearchRequest): Future[Response[SearchResponse]] = {
    val result: Future[Response[SearchResponse]] = elasticSearchClient.execute {
      searchRequest
    }

    result
  }
}
