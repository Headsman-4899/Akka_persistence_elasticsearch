package kz.dar.tech.elasticsearch.template


import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.akka.{AkkaHttpClient, AkkaHttpClientSettings}
import com.typesafe.config.{Config, ConfigFactory}
import kz.dar.tech.elasticsearch.template.routes.HttpRoutes
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object Boot extends App {

  implicit val config: Config = ConfigFactory.load()

  val log = LoggerFactory.getLogger(getClass)

  implicit val actorSystem = ActorSystem(Behaviors.empty, config.getString("akka.actor.system"))

  implicit def executionContext: ExecutionContext = actorSystem.executionContext

  val elasticSearchHosts = config.getString("elastic.addresses").split(",").map(_.trim).filter(!_.isEmpty).toSeq
  val elasticSearchClient: ElasticClient = ElasticClient(AkkaHttpClient(AkkaHttpClientSettings(config.getConfig("elastic")).copy(hosts = elasticSearchHosts.toVector))(actorSystem.classicSystem))

  val httpRoutes = new HttpRoutes(elasticSearchClient)

  runHttpServer() onComplete {
    case Success(serverBinding) =>
      log.info(s"ServiceName http server has been started. $serverBinding")

    case Failure(exception) =>
      throw exception
  }

  private def runHttpServer(): Future[Http.ServerBinding] = {
    Http().newServerAt(config.getString("http-server.interface"), port = config.getInt("http-server.port")).bind(httpRoutes.routes)
  }



}