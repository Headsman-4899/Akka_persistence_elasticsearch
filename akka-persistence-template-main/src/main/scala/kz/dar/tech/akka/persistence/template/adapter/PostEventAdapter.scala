package kz.dar.tech.akka.persistence.template.adapter

import akka.persistence.typed.{EventAdapter, EventSeq}
import kz.dar.tech.akka.persistence.template.event.proto.{CreatePostEventV1, RegisterPostEventV1}
import kz.dar.tech.akka.persistence.template.event.{CreatePostEvent, PostEvent, RegisterPostEvent}


class PostEventAdapter() extends EventAdapter[PostEvent, PostWrapper] {


  override def toJournal(e: PostEvent): PostWrapper = {
    val protoEvent = e match {
      case evt: CreatePostEvent => {
        CreatePostEventV1(
          ts = evt.ts.toString,
          postId = evt.postId,
          name = evt.name,
          address = evt.address
        )
      }
      case evt: RegisterPostEvent => {
        RegisterPostEventV1(
          ts = evt.ts.toString,
          postId = evt.postId
        )
      }
    }

    PostWrapper(protoEvent)
  }


  override def manifest(event: PostEvent): String = ""


  override def fromJournal(p: PostWrapper, manifest: String): EventSeq[PostEvent] = {
    p.event match {
      case evt: CreatePostEventV1 => {
        EventSeq.single(
          CreatePostEvent(
            ts = evt.ts.toString.toLong,
            postId = evt.postId,
            name = evt.name,
            address = evt.address
          )
        )
      }

      case evt: RegisterPostEventV1 => {
        EventSeq.single(
          RegisterPostEvent(
            ts = evt.ts.toLong,
            postId = evt.postId
          )
        )
      }
    }
  }

}
