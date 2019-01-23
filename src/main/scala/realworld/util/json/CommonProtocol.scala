package realworld.util.json

import io.circe._
import io.circe.syntax._

trait CommonProtocol {

  protected def wrappedDecoder[T](wrapName: String)(decoder: Decoder[T]): Decoder[T] =
    (c: HCursor) => for {
      wrapper <- c.downField(wrapName).as[HCursor]
      result <- decoder.tryDecode(wrapper)
    } yield result

  protected def wrappedEncoder[T](wrapName: String)(implicit encoder: Encoder[T]): Encoder[T] =
    (t: T) => Json.obj(
      wrapName -> t.asJson
    )

}
