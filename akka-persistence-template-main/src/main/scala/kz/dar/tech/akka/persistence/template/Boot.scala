package kz.dar.tech.akka.persistence.template

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.persistence.cassandra.testkit.CassandraLauncher
import com.typesafe.config.{Config, ConfigFactory}
import kz.dar.tech.akka.persistence.template.entity.{BookShopEntity, EmployeeEntityProto}
import kz.dar.tech.akka.persistence.template.route.{HttpRoutes, WebServer}
import kz.dar.tech.akka.persistence.template.util.EventProcessorSettings
import java.io.File
import java.util.concurrent.CountDownLatch


/**
 * Template persistence with emdedded cassandra.
 * Created by Yerke
 */
object Boot {

  implicit val config: Config = ConfigFactory.load()

  def main(args: Array[String]): Unit = {
    args.headOption match {

      case Some(portString) if portString.matches("""\d+""") =>
        val port = portString.toInt
        val httpPort = ("80" + portString.takeRight(2)).toInt
        startNode(port, httpPort)

      case Some("cassandra") =>
        startCassandraDatabase()
        println("Started Embedded Cassandra")
        new CountDownLatch(1).await()

      case None =>
        throw new IllegalArgumentException("port number, or cassandra required argument")
    }
  }

  def apply(): Behavior[NotUsed] = {
    Behaviors.setup { context =>
      implicit val system = context.system

      implicit val executionContext = system.executionContext

      val httpPort = context.system.settings.config.getInt("http-server.port")

      val settings = EventProcessorSettings(system)

      EmployeeEntityProto.init(system, settings)

      BookShopEntity.init(system, settings)

      val httpRoutes = new HttpRoutes()
      new WebServer(httpRoutes.routes, httpPort).start()


      Behaviors.receiveSignal {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
    }
  }


  def config(port: Int, httpPort: Int): Config =
    ConfigFactory.parseString(s"""
      akka.remote.artery.canonical.port = $port
      http-server.port = $httpPort
       """).withFallback(ConfigFactory.load())


  def startNode(port: Int, httpPort: Int): Unit = {
    val system = ActorSystem(Boot(), "Template", config(port, httpPort))
  }


  def startCassandraDatabase(): Unit = {
    val databaseDirectory = new File("target/cassandra-db")
    CassandraLauncher.start(databaseDirectory, CassandraLauncher.DefaultTestConfigResource, clean = false, port = 9042)
  }

}
