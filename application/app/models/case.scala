package models

import java.util.Date
import java.sql.Connection
import javax.inject.Inject
import play.api.db.Database

import anorm.{RowParser, ResultSetParser, ~, SqlQuery, SQL, as, on}
import anorm.SqlParser.{date, int, str, long}

case class Case(
    recordedDate: Date,
    infections: Int,
    deaths: Int,
    isoCountry: String
)

case class CasePopulationReport(
    isoCountry: String,
    name: String,
    population: Long,
    sumInfections: Long,
    infectionsPer100kPop: Long,
    sumDeaths: Long,
    deathsPer100kPop: Long
)

case class CaseCountryReport(
    isoCountry: String,
    name: String,
    recordedDate: Date,
    infections: Int,
    deaths: Int
)

class CaseDao @Inject (database: Database):
  val selectCases: SqlQuery =
    SQL("Select recorded_date, infections, deaths, iso_country from cases")

  val caseParser: RowParser[Case] =
    date("recorded_date") ~
      int("infections") ~
      int("deaths") ~
      str("iso_country") map:
      case recordedDate ~ infections ~ deaths ~ isoCountry =>
        Case(
          recordedDate,
          infections,
          deaths,
          isoCountry
        )

  val casesParser: ResultSetParser[List[Case]] =
    caseParser.*

  def getAll(): List[Case] =
    database.withConnection { implicit c: Connection =>
      selectCases.as(casesParser)
    }

  val selectCasesSumAndPopulationInnerJoinGroupByCountry: SqlQuery =
    SQL(
      """
        select
          iso_country,
          countries.name,
          countries.population,
          sum(infections) sum_infections,
          sum(deaths) sum_deaths
        from cases
        inner join countries
          on iso_country = countries.code
        group by iso_country,
          countries.code,
          countries.name,
          countries.population
      """
    )

  val selectCasesSumAndPopulationInnerJoinGroupByCountryOrderByDescLimit: SqlQuery =
    SQL(
      """
        select
          iso_country,
          countries.name,
          countries.population,
          sum(infections) sum_infections,
          sum(deaths) sum_deaths
        from cases
        inner join countries
          on iso_country = countries.code
        group by iso_country,
          countries.code,
          countries.name,
          countries.population
        order by
          sum_infections DESC
        limit {limit}
      """
    )

  val selectCasesSumAndPopulationInnerJoinGroupByCountryOrderByAscLimit: SqlQuery =
    SQL(
      """
        select
          iso_country,
          countries.name,
          countries.population,
          sum(infections) sum_infections,
          sum(deaths) sum_deaths
        from cases
        inner join countries
          on iso_country = countries.code
        group by iso_country,
          countries.code,
          countries.name,
          countries.population
        order by
          sum_infections ASC
        limit {limit}
      """
    )

  val casePopulationReportParser: RowParser[CasePopulationReport] =
    str("iso_country") ~
      str("name") ~
      long("population") ~
      long("sum_infections") ~
      long("sum_deaths") map:
      case isoCountry ~ name ~ population ~ sumInfections ~ sumDeaths =>
        CasePopulationReport(
          isoCountry,
          name,
          population,
          sumInfections,
          (100_000 * sumInfections / population),
          sumDeaths,
          (100_000 * sumDeaths / population)
        )

  val casesPopulationReportParser: ResultSetParser[List[CasePopulationReport]] =
    casePopulationReportParser.*

  def getCasesPopulationReport(): List[CasePopulationReport] =
    database.withConnection { implicit c: Connection =>
      selectCasesSumAndPopulationInnerJoinGroupByCountry.as(
        casesPopulationReportParser
      )
    }

  def getCasesPopulationReportOrderByDescLimit(
      limit: Int
  ): List[CasePopulationReport] =
    database.withConnection { implicit c: Connection =>
      selectCasesSumAndPopulationInnerJoinGroupByCountryOrderByDescLimit
        .on("limit" -> limit)
        .as(
          casesPopulationReportParser
        )
    }

  def getCasesPopulationReportOrderByAscLimit(
      limit: Int
  ): List[CasePopulationReport] =
    database.withConnection { implicit c: Connection =>
      selectCasesSumAndPopulationInnerJoinGroupByCountryOrderByAscLimit
        .on("limit" -> limit)
        .as(
          casesPopulationReportParser
        )
    }

  def getCasesPopulationReportSorted(
      lt: (CasePopulationReport, CasePopulationReport) => Boolean
  ): List[CasePopulationReport] =
    getCasesPopulationReport().sortWith(lt)

  def getMostInfectionsByCountry(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(_.sumInfections > _.sumInfections)

  def getLeastInfectionsByCountry(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(_.sumInfections < _.sumInfections)

  def getMostDeathsByCountry(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(_.sumDeaths > _.sumDeaths)

  def getLeastDeathsByCountry(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(_.sumInfections < _.sumInfections)

  def getMostPopulationByCountry(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(_.population > _.population)

  def getLeastPopulationByCountry(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(_.population < _.population)

  def getMostInfectionsPer100kPopulation(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(
      _.infectionsPer100kPop > _.infectionsPer100kPop
    )

  def getLeastInfectionsPer100kPopulation(): List[CasePopulationReport] =
    getCasesPopulationReportSorted(
      _.infectionsPer100kPop < _.infectionsPer100kPop
    )

  val selectCasesByCountryAndUpUntilDate: SqlQuery =
    SQL(
      """
        select
          iso_country,
          countries.name,
          recorded_date,
          infections,
          deaths
        from cases
        inner join countries
          on iso_country = countries.code
        where
          countries.name = {country}
        and
          recorded_date <= {date}
        order by recorded_date ASC
      """
    )

  val caseCountryAndDateParser: RowParser[CaseCountryReport] =
    str("iso_country") ~
      str("name") ~
      date("recorded_date") ~
      int("infections") ~
      int("deaths") map:
      case isoCountry ~ name ~ recordedDate ~ infections ~ deaths =>
        CaseCountryReport(
          isoCountry,
          name,
          recordedDate,
          infections,
          deaths
        )

  val casesCountryAndDateParser: ResultSetParser[List[CaseCountryReport]] =
    caseCountryAndDateParser.*

  def getByCountryUpUntilDate(
      country: String,
      date: Date
  ): List[CaseCountryReport] =
    database.withConnection { implicit c: Connection =>
      selectCasesByCountryAndUpUntilDate
        .on("country" -> country, "date" -> date)
        .as(
          casesCountryAndDateParser
        )
    }
