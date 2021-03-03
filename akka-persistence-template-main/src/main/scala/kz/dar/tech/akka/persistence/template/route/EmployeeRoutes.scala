package kz.dar.tech.akka.persistence.template.route

import akka.http.scaladsl.model.ErrorInfo
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import kz.dar.tech.akka.persistence.template.model.{EmployeeDTO, EmployeeLayoff, Family, Summary}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.swagger.v3.oas.annotations.media.{Content, ExampleObject, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{Operation, Parameter, parameters}
import io.swagger.v3.oas.annotations.tags.Tag
import kz.dar.tech.akka.persistence.template.command.{EmployeeCreateEntityCommand, EmployeeEditEntityCommand, EmployeeLayoffCommand}
import kz.dar.tech.akka.persistence.template.entity.EmployeeEntityProto
import kz.dar.tech.akka.persistence.template.util.Codec
import org.joda.time.DateTime
import akka.actor.typed.ActorSystem
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.util.Timeout

import javax.ws.rs.{POST, Path}
import scala.concurrent.Future

@Path("/")
@Tag(name = "Employee")
class EmployeeRoutes()(implicit system: ActorSystem[_]) extends Codec with FailFastCirceSupport {

  implicit private val timeout: Timeout = Timeout.create(system.settings.config.getDuration("askTimeout"))

  private val sharding = ClusterSharding(system)

  val routes: Route = {
    createEmployeeRoute ~ editEmployeeRoute
  }


  @POST
  @Path("employee/create")
  @parameters.RequestBody(
    description = "Request body for creating employee",
    content = Array(
      new Content(
        schema = new Schema(implementation = classOf[EmployeeDTO]),
        mediaType = "application/json",
        examples = Array(
          new ExampleObject(
            name = "request body ex 1",
            value = "{\n    \"employeeId\": \"awjdljawj-alwdjla123\",\n    \"firstName\": \"Yerke\",\n    \"lastName\": \"Yessenov\",\n    \"middleName\": \"Bolatovish\",\n    \"birthDate\": \"2021-01-04T17:41:35.013Z\",\n    \"position\": \"Senior Software Developer\",\n    \"family\": {\n        \"wife\": {\n            \"name\": \"Aidana\",\n            \"age\": 28,\n            \"sex\": \"famale\"\n        },\n        \"son\": {\n            \"name\": \"Danial\",\n            \"age\": 0,\n            \"sex\": \"male\"\n        }\n    }\n}"
          )
        )
      )
    )
  )
  @Operation(
    summary = "Create new employee for persistence",
    description = "Create new employee for persistence",
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "Created task instance",
        content = Array(
          new Content(
            schema = new Schema(implementation = classOf[EmployeeDTO]),
            mediaType = "application/json"
          )
        )
      ),
      new ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[ErrorInfo]), mediaType = "application/json"))
      )
    )
  )
  def createEmployeeRoute: Route = {
    pathPrefix("employee" / "create") {
      post {
        entity(as[EmployeeDTO]) { entity =>

          val entityRef = sharding.entityRefFor(EmployeeEntityProto.EntityKey, entity.employeeId)

          val reply: Future[Summary] = entityRef.ask(
            EmployeeCreateEntityCommand(
              ts = DateTime.now(),
              employeeId = entity.employeeId,
              firstName = entity.firstName,
              lastName = entity.lastName,
              middleName = entity.middleName,
              birthDate = entity.birthDate,
              position = entity.position,
              _,
              family = entity.family
            )
          )

          onSuccess(reply) { summary =>
            complete(summary)
          }
        }
      }
    }
  }


  def editEmployeeRoute: Route = {
    pathPrefix("employee" / "edit") {
      put {
        entity(as[EmployeeDTO]) { entity =>

          val entityRef = sharding.entityRefFor(EmployeeEntityProto.EntityKey, entity.employeeId)

          val reply: Future[Summary] = entityRef.ask(
            EmployeeEditEntityCommand(
              ts = DateTime.now(),
              employeeId = entity.employeeId,
              firstName = entity.firstName,
              lastName = entity.lastName,
              middleName = entity.middleName,
              birthDate = entity.birthDate,
              position = entity.position,
              _,
              family = entity.family
            )
          )

          onSuccess(reply) { summary =>
            complete(summary)
          }
        }
      }
    }
  }


  def employeeLayoffRoute: Route = {
    pathPrefix("employee" / "layoff") {
      put {
        entity(as[EmployeeLayoff]) { entity =>

          val entityRef = sharding.entityRefFor(EmployeeEntityProto.EntityKey, entity.employeeId)

          val reply: Future[Summary] = entityRef.ask(
            EmployeeLayoffCommand(
              ts = DateTime.now(),
              employeeId = entity.employeeId,
              _,
              isLayoffs = entity.isLayoffs
            )
          )

          onSuccess(reply) { summary =>
            complete(summary)
          }

        }
      }
    }
  }

}
