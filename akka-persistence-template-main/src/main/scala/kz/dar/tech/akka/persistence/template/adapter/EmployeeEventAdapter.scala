package kz.dar.tech.akka.persistence.template.adapter

import akka.persistence.typed.{EventAdapter, EventSeq}
import kz.dar.tech.akka.persistence.template.command.EmployeeLayoffCommand
import kz.dar.tech.akka.persistence.template.event.proto.{EmployeeCreateEntityEventV1, EmployeeEditEntityEventV1, EmployeeLayoffEventV1, FamilyV1}
import kz.dar.tech.akka.persistence.template.event.{EmployeeCreateEntityEvent, EmployeeEditEntityEvent, EmployeeEntityEvent, EmployeeLayoffEvent}
import kz.dar.tech.akka.persistence.template.model.Family
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

class EmployeeEventAdapter extends EventAdapter[EmployeeEntityEvent, EmployeeWrapper] {

  val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  override def toJournal(e: EmployeeEntityEvent): EmployeeWrapper = {
    val protoEvent = e match {
      case evt: EmployeeCreateEntityEvent =>
        EmployeeCreateEntityEventV1(
          ts = dateFormat.print(evt.ts),
          employeeId = evt.employeeId,
          firstName = evt.firstName,
          lastName = evt.lastName,
          middleName = evt.middleName,
          birthDate = dateFormat.print(evt.birthDate),
          position = evt.position,
          isLayoffs = evt.isLayoffs,
          family = evt.family.map(m => (m._1, FamilyV1(name = m._2.name, age = m._2.age, sex = m._2.sex)))
        )

      case evt: EmployeeEditEntityEvent =>
        EmployeeEditEntityEventV1(
          ts = dateFormat.print(evt.ts),
          employeeId = evt.employeeId,
          firstName = evt.firstName,
          lastName = evt.lastName,
          middleName = evt.middleName,
          birthDate = dateFormat.print(evt.birthDate),
          position = evt.position,
          isLayoffs = evt.isLayoffs,
          family = evt.family.map(m => (m._1, FamilyV1(name = m._2.name, age = m._2.age, sex = m._2.sex)))
        )

      case evt: EmployeeLayoffEvent =>
        EmployeeLayoffEventV1(
          ts = dateFormat.print(evt.ts),
          employeeId = evt.employeeId,
          isLayoffs = evt.isLayoffs
        )
    }

    EmployeeWrapper(protoEvent)
  }

  override def manifest(event: EmployeeEntityEvent): String = ""

  override def fromJournal(p: EmployeeWrapper, manifest: String): EventSeq[EmployeeEntityEvent] = {
    p.event match {
      case evt: EmployeeCreateEntityEventV1 =>
        EventSeq.single(
          EmployeeCreateEntityEvent(
            ts = dateFormat.parseDateTime(evt.ts),
            employeeId = evt.employeeId,
            firstName = evt.firstName,
            lastName = evt.lastName,
            middleName = evt.middleName,
            birthDate = dateFormat.parseDateTime(evt.birthDate),
            position = evt.position,
            isLayoffs = evt.isLayoffs,
            family = evt.family.map(m => (m._1, Family(name = m._2.name, age = m._2.age, sex = m._2.sex)))
          )
        )

      case evt: EmployeeEditEntityEventV1 =>
        EventSeq.single(
          EmployeeEditEntityEvent(
            ts = dateFormat.parseDateTime(evt.ts),
            employeeId = evt.employeeId,
            firstName = evt.firstName,
            lastName = evt.lastName,
            middleName = evt.middleName,
            birthDate = dateFormat.parseDateTime(evt.birthDate),
            position = evt.position,
            isLayoffs = evt.isLayoffs,
            family = evt.family.map(m => (m._1, Family(name = m._2.name, age = m._2.age, sex = m._2.sex)))
          )
        )

      case evt: EmployeeLayoffEventV1 =>
        EventSeq.single(
          EmployeeLayoffEvent(
            ts = dateFormat.parseDateTime(evt.ts),
            employeeId = evt.employeeId,
            isLayoffs = evt.isLayoffs
          )
        )
    }
  }
}
