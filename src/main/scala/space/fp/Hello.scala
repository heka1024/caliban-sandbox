package space.fp

import cats.effect.IO

import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._

import io.circe.syntax._
import io.circe.generic.auto._

object Hello {
  case class Hello(name: String)
  case class User(name: String)

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
    case GET -> Root / "json" =>
      Ok(User("koo").asJson)
  }
}