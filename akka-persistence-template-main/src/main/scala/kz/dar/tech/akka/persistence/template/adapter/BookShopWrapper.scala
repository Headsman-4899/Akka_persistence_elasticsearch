package kz.dar.tech.akka.persistence.template.adapter

import kz.dar.tech.akka.persistence.template.model.protobuf.{BookShopProtoEvent}

case class BookShopWrapper(event: BookShopProtoEvent)
