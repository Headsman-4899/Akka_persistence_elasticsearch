package kz.dar.tech.akka.persistence.template.util

import kz.dar.tech.akka.persistence.template.model.{BookShopDTO, EmployeeDTO, EmployeeLayoff, Family, Summary}
import io.circe.{Decoder, Encoder}
import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import org.joda.time.DateTime

trait Codec {

  implicit val employeeCreateEncodeDecode: EncoderDecoder[EmployeeDTO] = DerivedEncoderDecoder[EmployeeDTO]

  implicit val familyEncodeDecode: EncoderDecoder[Family] = DerivedEncoderDecoder[Family]

  implicit val datetimeED: EncoderDecoder[DateTime] = ContainerEncoderDecoder[DateTime, String](DateTime.parse, _.toString)

  implicit val summaryEncodeDecode: EncoderDecoder[Summary] = DerivedEncoderDecoder[Summary]

  implicit val layoffEncodeDecode: EncoderDecoder[EmployeeLayoff] = DerivedEncoderDecoder[EmployeeLayoff]

  implicit val bookShopEncodeDecode: EncoderDecoder[BookShopDTO] = DerivedEncoderDecoder[BookShopDTO]
}
