package kz.dar.tech.akka.persistence.template.adapter

import akka.persistence.typed.{EventAdapter, EventSeq}
import kz.dar.tech.akka.persistence.template.event.proto.{CreateBookEventV1, FindBookEventV1}
import kz.dar.tech.akka.persistence.template.event.{CreateBookEvent, BookShopEvent, FindBookEvent}


class BookShopEventAdapter() extends EventAdapter[BookShopEvent, BookShopWrapper] {
  override def toJournal(e: BookShopEvent): BookShopWrapper = {
    val protoEvent = e match {
      case evt: CreateBookEvent => {
        CreateBookEventV1(
          bookId = evt.bookId,
          name = evt.name,
          author = evt.author
        )
      }
      case evt: FindBookEvent => {
        FindBookEventV1(
          bookId = evt.bookId
        )
      }
    }

    BookShopWrapper(protoEvent)
  }

  override def manifest(event: BookShopEvent): String = ""

  override def fromJournal(p: BookShopWrapper, manifest: String): EventSeq[BookShopEvent] = {
    p.event match {
      case evt: CreateBookEventV1 => {
        EventSeq.single(
          CreateBookEvent(
            bookId = evt.bookId,
            name = evt.name,
            author = evt.author
          )
        )
      }

      case evt: FindBookEventV1 => {
        EventSeq.single(
          FindBookEvent(
            bookId = evt.bookId,
            name = evt.name,
            author = evt.author
          )
        )
      }
    }
  }
}
