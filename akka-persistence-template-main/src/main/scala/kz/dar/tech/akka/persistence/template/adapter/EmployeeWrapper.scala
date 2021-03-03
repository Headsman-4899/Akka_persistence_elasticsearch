package kz.dar.tech.akka.persistence.template.adapter

import kz.dar.tech.akka.persistence.template.model.protobuf.EmployeeProtoEvent

case class EmployeeWrapper(event: EmployeeProtoEvent)
