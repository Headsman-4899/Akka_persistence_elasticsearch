package kz.dar.tech.akka.persistence.template.command

import akka.actor.typed.ActorRef
import kz.dar.tech.akka.persistence.template.model.{Family, Summary}
import kz.dar.tech.akka.persistence.template.serializable.EntitySerializable
import org.joda.time.DateTime


trait EmployeeEntityCommand extends EntitySerializable {
  def ts: DateTime

  def employeeId: String
}

case class EmployeeCreateEntityCommand(ts: DateTime,
                                       employeeId: String,
                                       firstName: String,
                                       lastName: String,
                                       middleName: String,
                                       birthDate: DateTime,
                                       position: String,
                                       replyTo: ActorRef[Summary],
                                       family: Map[String, Family] = Map.empty) extends EmployeeEntityCommand


case class EmployeeEditEntityCommand(ts: DateTime,
                                     employeeId: String,
                                     firstName: String,
                                     lastName: String,
                                     middleName: String,
                                     birthDate: DateTime,
                                     position: String,
                                     replyTo: ActorRef[Summary],
                                     family: Map[String, Family] = Map.empty) extends EmployeeEntityCommand

case class EmployeeLayoffCommand(ts: DateTime,
                                 employeeId: String,
                                 replyTo: ActorRef[Summary],
                                 isLayoffs: Boolean) extends EmployeeEntityCommand


