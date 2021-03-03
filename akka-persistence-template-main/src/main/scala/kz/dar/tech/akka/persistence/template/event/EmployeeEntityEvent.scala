package kz.dar.tech.akka.persistence.template.event

import akka.Done
import akka.actor.typed.ActorRef
import kz.dar.tech.akka.persistence.template.command.EmployeeEntityCommand
import kz.dar.tech.akka.persistence.template.model.Family
import org.joda.time.DateTime

trait EmployeeEntityEvent {
  def ts: DateTime

  def employeeId: String
}

case class EmployeeCreateEntityEvent(ts: DateTime,
                                     employeeId: String,
                                     firstName: String,
                                     lastName: String,
                                     middleName: String,
                                     birthDate: DateTime,
                                     position: String,
                                     isLayoffs: Boolean,
                                     family: Map[String, Family] = Map.empty) extends EmployeeEntityEvent


case class EmployeeEditEntityEvent(ts: DateTime,
                                   employeeId: String,
                                   firstName: String,
                                   lastName: String,
                                   middleName: String,
                                   birthDate: DateTime,
                                   position: String,
                                   isLayoffs: Boolean,
                                   family: Map[String, Family] = Map.empty) extends EmployeeEntityEvent

case class EmployeeLayoffEvent(ts: DateTime,
                               employeeId: String,
                               isLayoffs: Boolean) extends EmployeeEntityEvent