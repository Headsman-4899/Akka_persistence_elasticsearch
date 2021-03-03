package kz.dar.tech.akka.persistence.template.util

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json, KeyDecoder, KeyEncoder}
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.encoding.DerivedAsObjectEncoder
import shapeless.Lazy

class EncoderDecoder[T](decode: Decoder[T], encode: Encoder[T]) extends Encoder[T] with Decoder[T] {
  override def apply(a: T): Json = encode(a)
  override def apply(c: HCursor): Result[T] = decode(c)
}

object DerivedEncoderDecoder {
  def apply[T](implicit decode: Lazy[DerivedDecoder[T]], encode: Lazy[DerivedAsObjectEncoder[T]]): EncoderDecoder[T] =
    new EncoderDecoder(decode.value, encode.value)
}

object ContainerEncoderDecoder {
  def apply[T, TInner](create: TInner => T, content: T => TInner)(implicit d: Decoder[TInner], e: Encoder[TInner]): EncoderDecoder[T] =
    new EncoderDecoder[T](Decoder[TInner].map(create), Encoder[TInner].contramap(content))
}

object MapEncoderDecoder {
  def apply[TKey, TValue](keyCreate: String => TKey, keyContent: TKey => String, mapper: (TKey, TValue) => (TKey, TValue) = (x: TKey, y: TValue) => x -> y)(
    implicit
    d: Decoder[TValue], e: Encoder[TValue]): EncoderDecoder[Map[TKey, TValue]] = {

    val mapDecoder = Decoder.decodeMap[TKey, TValue](KeyDecoder.instance(s => Some(keyCreate(s))), d)
      .map(_.map { case (k, v) => mapper(k, v) })
    val mapEncoder = Encoder.encodeMap[TKey, TValue](KeyEncoder.instance(keyContent), e)
    new EncoderDecoder(mapDecoder, mapEncoder)
  }
}
