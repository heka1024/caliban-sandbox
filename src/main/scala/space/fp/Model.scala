package space.fp

import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.util.{Success, Failure}
import scala.concurrent.duration._

import cats.effect.IO
import cats.data.{Nested, OptionT}

case class Supplier(
  id: Int, 
  name: String,
  street: String,
  city: String,
  state: String,
  zip: String
)

class Suppliers(tag: Tag) extends Table[Supplier](tag, "SUPPLIERS") {
  def id = column[Int]("SUP_ID", O.PrimaryKey)
  def name = column[String]("SUP_NAME")
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def state = column[String]("STATE")
  def zip = column[String]("ZIP")

  def * = (id, name, street, city, state, zip).mapTo[Supplier]
}

case class Coffee(
  name: String,
  supID: Int,
  price: Double,
  sales: Int,
  total: Int
)

class Coffees(tag: Tag) extends Table[Coffee](tag, "COFFEES") {
  def name = column[String]("COF_NAME", O.PrimaryKey)
  def supID = column[Int]("SUP_ID")
  def price = column[Double]("PRICE")
  def sales = column[Int]("SALES")
  def total = column[Int]("TOTAL")

  def * = (name, supID, price, sales, total).mapTo[Coffee]
}

object Model {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  val suppliers = TableQuery[Suppliers]
  val coffees = TableQuery[Coffees]
  // val db = Database.forConfig("test.db")
  val db = Database.forURL("jdbc:sqlite:./dev.db", driver = "org.sqlite.JDBC")

  def convert[A](fa: => Future[A])(implicit ec: ExecutionContext): IO[A] =
    IO.async { cb =>
      // This triggers evaluation of the by-name param and of onComplete, 
      // so it's OK to have side effects in this callback
      fa.onComplete {
        case Success(a) => cb(Right(a))
        case Failure(e) => cb(Left(e))
      }
    }

  def getCoffees(name: Option[String] = None): IO[List[Coffee]] = {
    val q = name match {
      case Some(v) => coffees.filter(_.name === v).result
      case None => coffees.result
    }
    convert(db.run(q).map(_.toList))
  }

  def getCoffeesN: Nested[IO, List, Coffee] = {
    val q = coffees.result
    val x = convert(db.run(q).map(_.toList))
    Nested(x)    
  }
  
  def getCoffee(name: String): OptionT[IO, Coffee] = {
    val q = coffees.filter(_.name === name).result.headOption
    val x = convert(db.run(q))
    OptionT(x)
  }

  def addCoffee(name: String, supID: Int, price: Double, sales: Int, total: Int): IO[Int] = {
    val pnew = Coffee(name, supID, price, sales, total)
    val q = coffees += pnew
    convert(db.run(q))
  }

  def getSuppliers(id: Option[Int]): IO[List[Supplier]] = {
    val q = id.fold(suppliers)(i => suppliersfilter(_.id === i)).map(_.result)
    convert(db.run(q).map(_.toList))
  }

  val setup = DBIO.seq(
    (suppliers.schema ++ coffees.schema).create,
    suppliers ++= Seq(
      Supplier(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
      Supplier(49, "Superior Coeffee", "1 Party Place", "Mendocino", "CA", "95460"),
      Supplier(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")
    ),
    coffees ++= Seq(
      Coffee("Columbian", 101, 7.99, 0, 0),
      Coffee("French_Roast", 49, 8.99, 0, 0),
      Coffee("Espresso", 150, 9.99, 0, 0),
      Coffee("Colombian_Decaf", 101, 8.99, 0, 0),
      Coffee("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
  )
}