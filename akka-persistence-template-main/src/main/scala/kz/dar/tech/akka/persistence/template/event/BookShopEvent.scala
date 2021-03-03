package kz.dar.tech.akka.persistence.template.event

trait BookShopEvent

case class CreateBookEvent(bookId: String,
                           name: String,
                           author: String) extends BookShopEvent

case class FindBookEvent(bookId: String,
                         name: String,
                         author: String) extends BookShopEvent

case class AddedToCartEvent(bookId: String) extends BookShopEvent

case class DeletedBookFromCartEvent(bookId: String) extends BookShopEvent

case class SellBookEvent(bookId: String) extends BookShopEvent

case class ReturnedBookEvent(bookId: String) extends BookShopEvent