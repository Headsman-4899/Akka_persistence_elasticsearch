package kz.dar.tech.akka.persistence.template.entity

import akka.Done
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import akka.cluster.client.ClusterClient.SendToAll
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import akka.event.slf4j.Logger
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, ReplyEffect, RetentionCriteria}
import kz.dar.tech.akka.persistence.template.adapter.EmployeeEventAdapter
import kz.dar.tech.akka.persistence.template.command.{EmployeeCreateEntityCommand, EmployeeEditEntityCommand, EmployeeEntityCommand}
import kz.dar.tech.akka.persistence.template.entity.EmployeeEntityProto.Layoffs
import kz.dar.tech.akka.persistence.template.event.{EmployeeCreateEntityEvent, EmployeeEditEntityEvent, EmployeeEntityEvent}
import kz.dar.tech.akka.persistence.template.model.{Family, Summary}
import kz.dar.tech.akka.persistence.template.serializable.EntitySerializable
import kz.dar.tech.akka.persistence.template.util.EventProcessorSettings
import org.joda.time.DateTime

import scala.concurrent.duration._


/**
 * This is an event sourced actor. Created according to Akka documentation by
 * https://doc.akka.io/docs/akka/current/typed/index-persistence.html
 * Example for protobuf.
 *
 * Created by Yerke
 */
object EmployeeEntityProto {

  final case class Employee(ts: DateTime,
                            employeeId: String,
                            firstName: String,
                            lastName: String,
                            middleName: String,
                            birthDate: DateTime,
                            position: String,
                            family: Map[String, Family] = Map.empty,
                            isLayoffs: Boolean)


  sealed trait State extends EntitySerializable

  case object InitState extends State

  final case class EditEmployee(content: Employee) extends State

  case object Layoffs extends State

  private val logger = Logger(getClass.getSimpleName)

  val EntityKey: EntityTypeKey[EmployeeEntityCommand] = EntityTypeKey[EmployeeEntityCommand]("Employee")

  def init(system: ActorSystem[_],
           eventProcessorSettings: EventProcessorSettings): Unit = {

    logger.info("Cluster sharding initialization")

    ClusterSharding(system).init(Entity(EntityKey) { entityContext =>
      val n = math.abs(entityContext.entityId.hashCode % eventProcessorSettings.parallelism)
      val eventProcessorTag = eventProcessorSettings.tagPrefix + "-" + n
      EmployeeEntityProto(entityContext.entityId, Set(eventProcessorTag))
    })

  }


  def apply(entityId: String, eventProcessorTag: Set[String]): Behavior[EmployeeEntityCommand] = {
    EventSourcedBehavior
      .withEnforcedReplies[EmployeeEntityCommand, EmployeeEntityEvent, State](
        PersistenceId(EntityKey.name, entityId),
        InitState,
        (state, command) => commandHandler(entityId, state, command),
        (state, event) => handleEvent(state, event)
      ).withTagger(_ => eventProcessorTag)
      .withRetention(RetentionCriteria.snapshotEvery(numberOfEvents = 2, keepNSnapshots = 2))
      .onPersistFailure(SupervisorStrategy.restartWithBackoff(200.millis, 5.seconds, 0.1))
      .eventAdapter(new EmployeeEventAdapter)
  }

  def commandHandler(entityId: String, state: State, command: EmployeeEntityCommand): ReplyEffect[EmployeeEntityEvent, State] = {

    logger.info(s"Starting employee with state: $state and persistenceId: $entityId")

    state match {
      case InitState =>
        command match {
          case cmd: EmployeeCreateEntityCommand =>
            logger.info(s"Recived EmployeeCreateEntityCommand for id $entityId")

            logger.info("Creating Employee")

            val evt = EmployeeCreateEntityEvent(
              ts = cmd.ts,
              employeeId = cmd.employeeId,
              firstName = cmd.firstName,
              lastName = cmd.middleName,
              middleName = cmd.middleName,
              birthDate = cmd.birthDate,
              position = cmd.position,
              isLayoffs = false,
              family = cmd.family)

            Effect.persist(evt).thenReply(cmd.replyTo)(_ =>
              Summary(
                ts = evt.ts,
                employeeId = evt.employeeId,
                firstName = evt.firstName,
                lastName = cmd.middleName,
                middleName = cmd.middleName,
                birthDate = cmd.birthDate,
                position = cmd.position,
                isLayoffs = false,
                family = cmd.family
              )
            )

          case cmd =>
            logger.info("[InitState] unhandle message: " + cmd.toString)
            Effect.noReply
        }

      case editState: EditEmployee =>
        command match {

          case cmd: EmployeeEditEntityCommand => {
            val evt = EmployeeEditEntityEvent(
              ts = cmd.ts,
              employeeId = cmd.employeeId,
              firstName = cmd.firstName,
              lastName = cmd.middleName,
              middleName = cmd.middleName,
              birthDate = cmd.birthDate,
              position = cmd.position,
              family = cmd.family,
              isLayoffs = false
            )

            Effect.persist(evt).thenReply(cmd.replyTo)(_ =>
              Summary(
                ts = evt.ts,
                employeeId = evt.employeeId,
                firstName = evt.firstName,
                lastName = cmd.middleName,
                middleName = cmd.middleName,
                birthDate = cmd.birthDate,
                position = cmd.position,
                isLayoffs = false,
                family = cmd.family
              )
            )
          }

          case cmd => logger.info("[InitState] unhandle message: " + cmd.toString)
            Effect.noReply
        }


    }

  }


  def handleEvent(state: State, event: EmployeeEntityEvent): State = {
    state match {
      case InitState =>
        event match {
          case e: EmployeeCreateEntityEvent => EditEmployee(
            Employee(
              ts = e.ts,
              employeeId = e.employeeId,
              firstName = e.firstName,
              lastName = e.lastName,
              middleName = e.middleName,
              birthDate = e.birthDate,
              position = e.position,
              family = e.family,
              isLayoffs = false
            )
          )

        }

      case editState: EditEmployee =>
        event match {
          case e: EmployeeEditEntityEvent => EditEmployee(
            Employee(
              ts = e.ts,
              employeeId = e.employeeId,
              firstName = e.firstName,
              lastName = e.lastName,
              middleName = e.middleName,
              birthDate = e.birthDate,
              position = e.position,
              family = e.family,
              isLayoffs = false
            )
          )
        }

    }
  }

}
