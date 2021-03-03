package kz.dar.tech.akka.persistence.template.entity

import akka.actor.typed.{ActorSystem, Behavior}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, RetentionCriteria}
import kz.dar.tech.akka.persistence.template.adapter.BookShopEventAdapter
import kz.dar.tech.akka.persistence.template.command.{AddToCartCommand, BookShopCommand, CreateBookCommand, DeleteBookFromCartCommand, PayCommand, ReturnBookCommand, SearchBookCommand}
import kz.dar.tech.akka.persistence.template.event.{AddedToCartEvent, BookShopEvent, CreateBookEvent, DeletedBookFromCartEvent, FindBookEvent, SellBookEvent}
import kz.dar.tech.akka.persistence.template.model.SummaryBookShop
import kz.dar.tech.akka.persistence.template.util.EventProcessorSettings

object BookShopEntity {

  case class Book(name: Option[String] = None,
                  bookId: Option[String] = None,
                  author: Option[String] = None)

  object BookShop {
    def empty = new Book()
  }

  trait State

  trait BookShopEntityState

  object BookShopEntityState {

    case object CREATE extends BookShopEntityState

    case object FIND extends BookShopEntityState

    case object ADDTOCART extends BookShopEntityState

    case object DELETEBOOK extends BookShopEntityState

    case object PAY extends BookShopEntityState

    case object FINISH extends BookShopEntityState

  }

  case class StateHolder(content: Book, state: BookShopEntityState) {

    def update(event: BookShopEvent): StateHolder = event match {
      case evt: CreateBookEvent => {
        copy(
          content = content.copy(
            bookId = Some(evt.bookId),
            name = Some(evt.name),
            author = Some(evt.author)
          ),
          state = BookShopEntityState.FIND
        )
      }

      case evt: FindBookEvent => {
        copy(
          content = content.copy(
            bookId = Some(evt.bookId)
          ),
          state = BookShopEntityState.ADDTOCART
        )
      }

      case evt: AddedToCartEvent => {
        copy(
          content = content.copy(
            bookId = Some(evt.bookId)
          ),
          state = BookShopEntityState.DELETEBOOK
        )
      }

      case evt: AddedToCartEvent => {
        copy(
          content = content.copy(
            bookId = Some(evt.bookId)
          ),
          state = BookShopEntityState.PAY
        )
      }

      case evt: SellBookEvent => {
        copy(
          state = BookShopEntityState.FINISH
        )
      }
    }
  }


  object StateHolder {
    def empty: StateHolder = StateHolder(content = BookShop.empty, state = BookShopEntityState.CREATE)
  }

  val entityKey: EntityTypeKey[BookShopCommand] = EntityTypeKey[BookShopCommand]("Book Shop")

  def init(system: ActorSystem[_], eventProcessorSettings: EventProcessorSettings): Unit = {

    ClusterSharding(system).init(Entity(entityKey) { entityContext =>
      val n = math.abs(entityContext.entityId.hashCode % eventProcessorSettings.parallelism)
      val eventProcessorTag = eventProcessorSettings.tagPrefix + "-" + n
      BookShopEntity(entityContext.entityId, Set(eventProcessorTag))
    })
  }

  def apply(bookId: String, eventProcessorTag: Set[String]): Behavior[BookShopCommand] = {
    EventSourcedBehavior[BookShopCommand, BookShopEvent, StateHolder](
      persistenceId = PersistenceId(entityKey.name, bookId),
      StateHolder.empty,
      (state, command) => commandHandler(bookId, state, command),
      (state, event) => handleEvent(state, event)
    ).withTagger(_ => eventProcessorTag).withRetention(RetentionCriteria.snapshotEvery(numberOfEvents = 10, keepNSnapshots = 2))
      .eventAdapter(new BookShopEventAdapter)
  }

  def commandHandler(bookId: String, state: StateHolder, command: BookShopCommand): Effect[BookShopEvent, StateHolder] = {
    command match {

      case cmd: CreateBookCommand => {
        state.state match {
          case BookShopEntityState.CREATE => {
            val evt = CreateBookEvent(
              bookId = cmd.bookId,
              name = cmd.name,
              author = cmd.author
            )

            Effect.persist(evt).thenReply(cmd.replyTo)(_ => {
              SummaryBookShop(
                bookId = cmd.bookId,
                name = cmd.name,
                author = cmd.author
              )
            })
          }
          case _ => throw new RuntimeException("Error")
        }
      }

      case cmd: SearchBookCommand => {
        state.state match {
          case BookShopEntityState.FIND => {
            val evt = FindBookEvent(
              bookId = cmd.bookId,
              name = cmd.name,
              author = cmd.author
            )
            Effect.persist(evt)
          }
        }
      }

      case cmd: AddToCartCommand => {
        state.state match {
          case BookShopEntityState.ADDTOCART => {
            val evt = AddedToCartEvent(
              bookId = cmd.bookId
            )
            Effect.persist(evt)
          }
        }
      }

      case cmd: ReturnBookCommand => {
        state.state match {
          case BookShopEntityState.FIND => {
            val evt = FindBookEvent(
              bookId = cmd.bookId,
              name = cmd.name,
              author = cmd.author
            )
            Effect.persist(evt)
          }
        }
      }

      case cmd: DeleteBookFromCartCommand => {
        state.state match {
          case BookShopEntityState.DELETEBOOK => {
            val evt = DeletedBookFromCartEvent(
              bookId = cmd.bookId
            )
            Effect.persist(evt)
          }
        }
      }

      case cmd: PayCommand => {
        state.state match {
          case BookShopEntityState.PAY => {
            val evt = SellBookEvent(
              bookId = cmd.bookId
            )
            Effect.persist(evt)
          }
        }
      }
    }
  }

  def handleEvent(state: StateHolder, event: BookShopEvent): StateHolder = {
    state.update(event)
  }
}