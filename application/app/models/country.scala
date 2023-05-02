package models

import anorm.{RowParser, ~, SqlQuery, SQL, as, on}
import anorm.SqlParser.{int, str}
import javax.inject.Inject
import play.api.db.Database
import java.sql.Connection
import anorm.ResultSetParser

case class Country(
    id: Int,
    code: String,
    name: String,
    population: Int,
    continent: String,
    wikipediaLink: String
)

class CountryDao @Inject (database: Database):
  val selectCountries: SqlQuery =
    SQL(
      "Select id, code, name, population, continent, wikipedia_link from countries"
    )

  val selectCountryByName: SqlQuery =
    SQL(
      """
        Select id, code, name, population, continent, wikipedia_link
        from countries
        where name = {name}
      """
    )

  val selectCountryByContinent: SqlQuery =
    SQL(
      """
        Select id, code, name, population, continent, wikipedia_link
        from countries
        where continent = {continent}
      """
    )

  val countryParser: RowParser[Country] =
    int("id") ~ str("code") ~ str("name") ~ int("population") ~ str("continent") ~ str("wikipedia_link") map:
      case id ~ code ~ name ~ population ~ continent ~ wikipediaLink =>
        Country(
          id,
          code,
          name,
          population,
          continent,
          wikipediaLink
        )

  val countriesParser: ResultSetParser[List[Country]] =
    countryParser.*

  def getAll(): List[Country] =
    database.withConnection { implicit c: Connection =>
      selectCountries.as(countriesParser)
    }

  def getByName(name: String): Option[Country] =
    database.withConnection { implicit c: Connection =>
      selectCountryByName.on("name" -> name).as(countryParser.singleOpt)
    }

  def getByContinent(continent: String): List[Country] =
    database.withConnection { implicit c: Connection =>
      selectCountryByContinent.on("continent" -> continent).as(countriesParser)
    }
