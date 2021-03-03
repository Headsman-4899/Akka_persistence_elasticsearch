package kz.dar.tech.akka.persistence.template.adapter

import kz.dar.tech.akka.persistence.template.model.protobuf.{PostProtoEvent}

case class PostWrapper(event: PostProtoEvent)
