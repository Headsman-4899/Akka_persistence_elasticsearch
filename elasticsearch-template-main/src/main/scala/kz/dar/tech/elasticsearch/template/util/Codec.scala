package kz.dar.tech.elasticsearch.template.util

import kz.dar.tech.elasticsearch.template.model.{BookShopModel, PostModel}

trait Codec {

  //implicit val userEncodeDecodePost: EncoderDecoder[PostModel] = DerivedEncoderDecoder[PostModel]

  implicit val userEncodeDecode: EncoderDecoder[BookShopModel] = DerivedEncoderDecoder[BookShopModel]

}
