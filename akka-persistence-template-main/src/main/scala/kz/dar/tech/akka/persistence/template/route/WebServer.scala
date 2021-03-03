package kz.dar.tech.akka.persistence.template.route

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.config.Config

import scala.util.{Failure, Success}
import scala.concurrent.duration._

class WebServer(routes: Route, port: Int)(implicit system: ActorSystem[_], config: Config) {

    import system.executionContext

    def start(): Unit = {
      Http().newServerAt(config.getString("http-server.interface"), port = port).bind(routes)
        .map(_.addToCoordinatedShutdown(3.seconds))
        .onComplete {
          case Success(binding) =>
            val address = binding.localAddress
            system.log.info("Akka persistence template online at http://{}:{}/", address.getHostString, address.getPort)

          case Failure(ex) =>
            system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
            system.terminate()
        }
    }
}
