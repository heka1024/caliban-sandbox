package space.fp

// import space.fp.{Coffee, Supplier}
import space.fp.Model.{db, getCoffee, getCoffeesN}

import cats.effect._
import cats.implicits._
import cats.data.{Nested, OptionT}

import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._

import io.circe.syntax._
import io.circe.generic.auto._

object CoffeServer {
  case class Error(message: String)
  val CoffeeService = HttpRoutes.of[IO] {
    case GET -> Root / "coffees" =>
      val x = getCoffeesN.map(_.asJson)
      x.value.flatMap(Ok(_))
    case GET -> Root / "coffee" / name =>
      val x = getCoffee(name).map(_.asJson).value
      x.flatMap {
        _.fold(NoContent())(Ok(_))
      }
  }
}