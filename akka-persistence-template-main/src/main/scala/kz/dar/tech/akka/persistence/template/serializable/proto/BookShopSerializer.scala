package kz.dar.tech.akka.persistence.template.serializable.proto

import akka.serialization.SerializerWithStringManifest
import kz.dar.tech.akka.persistence.template.event.proto.{CreateBookEventV1}

class BookShopSerializer extends SerializerWithStringManifest {

  final val CreateBookEventManifestV1: String = classOf[CreateBookEventV1].getName

  override def identifier: Int = 1001

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case evt: CreateBookEventV1 => evt.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = manifest match {
    case CreateBookEventManifestV1 => CreateBookEventV1.parseFrom(bytes)
  }
}
