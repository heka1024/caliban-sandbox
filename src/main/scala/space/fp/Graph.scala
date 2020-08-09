package space.fp

import caliban.GraphQL.graphQL
import caliban.RootResolver

import cats.effect.{ ExitCode, IO, IOApp }
import zio.{ Runtime, ZEnv }

import caliban.{Http4sAdapter, CalibanError}

import scala.collection.mutable.ArrayBuffer
import util.{Try, Success, Failure}

object Graph {
  import caliban.interop.cats.implicits._
  implicit val runtime: Runtime[ZEnv] = Runtime.default

  case class Character(name: String, age: Int)

  var db: ArrayBuffer[Character] = ArrayBuffer(
    Character("foo", 20),
    Character("bar", 46),
    Character("koo", 23)
  )

  def getCharacters(arg: Option[String]): List[Character] = {
    arg.fold(db.toList)(name => db.filter(_.name == name).toList)
  }

  // schema
  case class AuxStr(str: Option[String])
  case class CharacterName(name: String)
  case class CharactersArgs(name: Option[String])
  case class CoffeeArgs(name: Option[String])

  case class Queries(
    characters: AuxStr => List[Character],
    coffees: AuxStr => IO[List[Coffee]]
  )
  // resolver
  val queries = Queries(
    args => getCharacters(args.str),
    args => Model.getCoffees(args.str)
  )

  case class CoffeeArg(name: String, supID: Int, price: Double, sales: Int, total: Int)

  // schema
  case class Mutations(
    deleteCharacter: CharacterName => Int,
    addCharacter: Character => Option[Character],
    addCoffee: CoffeeArg => IO[Int]
  )

  def addCharacter(name: String, age: Int): Option[Character] = {
    if (db.map(_.name) contains name) {
      None
    } else {
      val pnew = Character(name, age)
      db += pnew
      println("add", db)
      Some(pnew)
    }
  }

  def delCharacter(name: String): Int = {
    val goal = db.filter(_.name == name).headOption
    goal map { x =>
      db -= x
    }
    goal.fold(0)(_ => 1)
  }
  
  val mutations = Mutations(
    args => delCharacter(args.name),
    args => addCharacter(args.name, args.age),
    CoffeeArg => Model.addCoffee(
      CoffeeArg.name,
      CoffeeArg.supID,
      CoffeeArg.price,
      CoffeeArg.sales,
      CoffeeArg.total
    )
  )

  val api = graphQL(RootResolver(queries, mutations))

  val query = """
  {
    character(name: "koo") {
      name
    }
  }"""

  val interpreter = api.interpreterAsync[IO]

  val graphService = Http4sAdapter.makeHttpServiceF[IO, CalibanError](interpreter.unsafeRunSync)


  // override def run(args: List[String]): IO[ExitCode] =
  //   for {
  //     interpreter <- api.interpreterAsync[IO]
  //     _           <- interpreter.checkAsync[IO](query)
  //     result      <- interpreter.executeAsync[IO](query)
  //     _           <- IO(println(result.data))
  //   } yield ExitCode.Success

  // for { 
  //   i <- api.interpreterAsync[IO]
  //   serv <- Http4sAdapter.makeHttpServiceF[IO, CalibanError](i)
  //   x <- IO { serv }
  // } yield { x }

}