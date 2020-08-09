package space.fp

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router
import scala.concurrent.ExecutionContext.global
import caliban.{Http4sAdapter, CalibanError}

// object Main {

// }

object Main extends IOApp {
  
  val service = Router(
    "/" -> Hello.helloWorldService,
    "/api" -> CoffeServer.CoffeeService,
    "/graph" -> Graph.graphService
  ).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
