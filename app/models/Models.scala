package models

import scala.slick.driver.H2Driver.simple._

import java.util.Date
import java.sql.{ Date => SqlDate }
import scala.slick.lifted.Tag

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

/**
 * Data Access Object trait
 *
 *  Used to create the DAOs: Companies and Computers
 */
private[models] trait DAO {
  val Companies = TableQuery[Companies]
  val Computers = TableQuery[Computers]
  val RealEstates = TableQuery[RealEstates]
}

case class Company(id: Option[Long], name: String)

class Companies(tag: Tag) extends Table[Company](tag, "COMPANY") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)

  def * = (id.?, name) <> (Company.tupled, Company.unapply _)
}

object Companies extends DAO {
  /**
   * Construct the Map[String,String] needed to fill a select options set
   */
  def options(implicit s: Session): Seq[(String, String)] = {
    val query = (for {
      company <- Companies
    } yield (company.id, company.name)).sortBy(_._2)
    query.list.map(row => (row._1.toString, row._2))
  }

  /**
   * Insert a new company
   * @param company
   */
  def insert(company: Company)(implicit s: Session) {
    Companies.insert(company)
  }
}

case class Computer(id: Option[Long] = None, name: String, introduced: Option[Date] = None, discontinued: Option[Date] = None, companyId: Option[Long] = None)

class Computers(tag: Tag) extends Table[Computer](tag, "COMPUTER") {

  implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def introduced = column[Date]("introduced", O.Nullable)
  def discontinued = column[Date]("discontinued", O.Nullable)
  def companyId = column[Long]("companyId", O.Nullable)

  def * = (id.?, name, introduced.?, discontinued.?, companyId.?) <>(Computer.tupled, Computer.unapply _)
}

object Computers extends DAO {
  /**
   * Retrieve a computer from the id
   * @param id
   */
  def findById(id: Long)(implicit s: Session): Option[Computer] =
    Computers.where(_.id === id).firstOption

  /**
   * Count all computers
   */
  def count(implicit s: Session): Int =
    Query(Computers.length).first

  /**
   * Count computers with a filter
   * @param filter
   */
  def count(filter: String)(implicit s: Session): Int =
    Query(Computers.where(_.name.toLowerCase like filter.toLowerCase).length).first

  /**
   * Return a page of (Computer,Company)
   * @param page
   * @param pageSize
   * @param orderBy
   * @param filter
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")(implicit s: Session): Page[(Computer, Option[Company])] = {

    val offset = pageSize * page
    val query =
      (for {
        (computer, company) <- Computers leftJoin Companies on (_.companyId === _.id)
        if computer.name.toLowerCase like filter.toLowerCase()
      } yield (computer, company.id.?, company.name.?))
        .drop(offset)
        .take(pageSize)

    val totalRows = count(filter)
    val result = query.list.map(row => (row._1, row._2.map(value => Company(Option(value), row._3.get))))

    Page(result, page, offset, totalRows)
  }

  /**
   * Insert a new computer
   * @param computer
   */
  def insert(computer: Computer)(implicit s: Session) {
    Computers.insert(computer)
  }

  /**
   * Update a computer
   * @param id
   * @param computer
   */
  def update(id: Long, computer: Computer)(implicit s: Session) {
    val computerToUpdate: Computer = computer.copy(Some(id))
    Computers.where(_.id === id).update(computerToUpdate)
  }

  /**
   * Delete a computer
   * @param id
   */
  def delete(id: Long)(implicit s: Session) {
    Computers.where(_.id === id).delete
  }
}


case class RealEstate(id: Option[Long], name: String, street: String, city: String, state: String, country: String, zip: String)

class RealEstates(tag: Tag) extends Table[RealEstate](tag, "REAL_ESTATE_T") {
  def id = column[Long]("REAL_ESTATE_ID", O.PrimaryKey)
  def name = column[String]("NAME")
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def state = column[String]("STATE")
  def country = column[String]("COUNTRY")
  def zip = column[String]("ZIP")

  def * = (id, name, street, city, state, country, zip)
}

object RealEstates extends DAO {
  /**
   * Retrieve a computer from the id
   * @param id
   */
  def findById(id: Long)(implicit s: Session): Option[RealEstate] =
    RealEstates.where(_.id === id).firstOption

  /**
   * Count all computers
   */
  def count(implicit s: Session): Int =
    Query(RealEstates.length).first

  /**
   * Count computers with a filter
   * @param filter
   */
  def count(filter: String)(implicit s: Session): Int =
    Query(RealEstates.where(_.name.toLowerCase like filter.toLowerCase).length).first

  /**
   * Return a page of (Computer,Company)
   */
//  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%")(implicit s: Session): Page[(Computer, Option[Company])] = {
//
//    val offset = pageSize * page
//    val query =
//      (for {
//        (computer, company) <- Computers leftJoin Companies on (_.companyId === _.id)
//        if computer.name.toLowerCase like filter.toLowerCase()
//      } yield (computer, company.id.?, company.name.?))
//        .drop(offset)
//        .take(pageSize)
//
//    val totalRows = count(filter)
//    val result = query.list.map(row => (row._1, row._2.map(value => Company(Option(value), row._3.get))))
//
//    Page(result, page, offset, totalRows)
//  }

  /**
   * Insert a new computer
   * @param realEstate
   */
  def insert(realEstate: RealEstate)(implicit s: Session) {
    RealEstates.insert(realEstate)
  }

  /**
   * Update a computer
   * @param id
   * @param realEstate
   */
  def update(id: Long, realEstate: RealEstate)(implicit s: Session) {
    val realEstateToUpdate: RealEstate = realEstate.copy(Some(id))
    RealEstates.where(_.id === id).update(realEstateToUpdate)
  }

  /**
   * Delete a computer
   * @param id
   */
  def delete(id: Long)(implicit s: Session) {
    RealEstates.where(_.id === id).delete
  }
}