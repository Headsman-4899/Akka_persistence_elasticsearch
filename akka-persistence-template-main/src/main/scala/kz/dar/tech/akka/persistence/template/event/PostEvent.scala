package kz.dar.tech.akka.persistence.template.event


trait PostEvent

case class CreatePostEvent(ts: Long,
                           postId: String,
                           name: String,
                           address: String) extends PostEvent


case class RegisterPostEvent(ts: Long,
                             postId: String) extends PostEvent

case class SendPostEvent(ts: Long,
                         postId: String) extends PostEvent

case class ReceivePostEvent(ts: Long,
                            postId: String) extends PostEvent
