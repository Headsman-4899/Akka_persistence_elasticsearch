package kz.dar.tech.akka.persistence.template.serializable.proto

import akka.serialization.SerializerWithStringManifest
import kz.dar.tech.akka.persistence.template.event.proto.EmployeeCreateEntityEventV1

class EmployeeEventSerializer extends SerializerWithStringManifest {

  final val EmployeeCreateEventManifestV1: String = classOf[EmployeeCreateEntityEventV1].getName

  override def identifier: Int = 1000

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case evt: EmployeeCreateEntityEventV1 => evt.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case EmployeeCreateEventManifestV1 => EmployeeCreateEntityEventV1.parseFrom(bytes)
  }
}
