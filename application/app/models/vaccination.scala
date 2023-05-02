package models

import java.util.Date

import anorm.{RowParser, ~, SqlQuery, SQL, as, on}
import anorm.SqlParser.{date, int, str, long}
import javax.inject.Inject
import play.api.db.Database
import anorm.ResultSetParser
import java.sql.Connection
import scala.compiletime.ops.boolean

case class Vaccination(
    recordedDate: Date,
    dailyVaccinationsRaw: Int,
    dailyVaccinations: Int,
    isoCountry: String
)

case class VaccinationPopulationReport(
    isoCountry: String,
    name: String,
    population: Long,
    sumDailyVaccinationsRaw: Long,
    percentageVaccinatedPopulation: Int
)

class VaccinationDao @Inject (database: Database):
  val selectVaccinations: SqlQuery =
    SQL(
      "Select recorded_date, daily_vaccinations_raw, daily_vaccinations, iso_country from vaccinations"
    )

  val vaccinationParser: RowParser[Vaccination] =
    date("recorded_date") ~
      int("daily_vaccinations_raw") ~
      int("daily_vaccinations") ~
      str("iso_country") map:
      case recordedDate ~ dailyVaccinationsRaw ~ dailyVaccinations ~ isoCountry =>
        Vaccination(
          recordedDate,
          dailyVaccinationsRaw,
          dailyVaccinations,
          isoCountry
        )

  val vaccinationsParser: ResultSetParser[List[Vaccination]] =
    vaccinationParser.*

  def getAll(): List[Vaccination] =
    database.withConnection { implicit c: Connection =>
      selectVaccinations.as(vaccinationsParser)
    }

  val selectVaccinationsSumAndPopulationInnerJoinGroupByCountry: SqlQuery =
    SQL(
      """
        select
          iso_country,
          countries.name,
          countries.population population,
          sum(daily_vaccinations_raw) sum_daily_raw
        from vaccinations
        inner join countries
        on iso_country = countries.code
        group by
          iso_country,
          countries.code,
          countries.name,
          countries.population
      """
    )

  val selectVaccinationsSumAndPopulationInnerJoinGroupByCountryOrderByDescLimit
      : SqlQuery =
    SQL(
      """
        select
          iso_country,
          countries.name,
          countries.population population,
          sum(daily_vaccinations_raw) sum_daily_raw
        from vaccinations
        inner join countries
        on iso_country = countries.code
        group by
          iso_country,
          countries.code,
          countries.name,
          countries.population
        order by sum_daily_raw DESC
        limit {limit}
      """
    )

  val selectVaccinationsSumAndPopulationInnerJoinGroupByCountryOrderByAscLimit
      : SqlQuery =
    SQL(
      """
        select
          iso_country,
          countries.name,
          countries.population population,
          sum(daily_vaccinations_raw) sum_daily_raw
        from vaccinations
        inner join countries
        on iso_country = countries.code
        group by
          iso_country,
          countries.code,
          countries.name,
          countries.population
        order by sum_daily_raw ASC
        limit {limit}
      """
    )

  val vaccinationPopulationReportParser
      : RowParser[VaccinationPopulationReport] =
    str("iso_country") ~
      str("name") ~
      long("population") ~
      long("sum_daily_raw") map:
      case isoCountry ~ name ~ population ~ sumDailyRaw =>
        VaccinationPopulationReport(
          isoCountry,
          name,
          population,
          sumDailyRaw,
          (sumDailyRaw.toDouble / population.toDouble * 100).round.toInt
        )

  val vaccinationsPopulationReportParser
      : ResultSetParser[List[VaccinationPopulationReport]] =
    vaccinationPopulationReportParser.*

  def getVaccinationsPopulationReport(): List[VaccinationPopulationReport] =
    database.withConnection { implicit c: Connection =>
      selectVaccinationsSumAndPopulationInnerJoinGroupByCountry
        .as(
          vaccinationsPopulationReportParser
        )
    }

  def getVaccinationsPopulationReportOrderByDescLimit(
      limit: Int
  ): List[VaccinationPopulationReport] =
    database.withConnection { implicit c: Connection =>
      selectVaccinationsSumAndPopulationInnerJoinGroupByCountryOrderByDescLimit
        .on("limit" -> limit)
        .as(
          vaccinationsPopulationReportParser
        )
    }

  def getVaccinationsPopulationReportOrderByAscLimit(
      limit: Int
  ): List[VaccinationPopulationReport] =
    database.withConnection { implicit c: Connection =>
      selectVaccinationsSumAndPopulationInnerJoinGroupByCountryOrderByAscLimit
        .on("limit" -> limit)
        .as(
          vaccinationsPopulationReportParser
        )
    }

  def getCountryWithUnvaccinatedPopulation(
      threshold: Int,
      order_by: String,
      limit: Int
  ): List[VaccinationPopulationReport] =
    getVaccinationsPopulationReport()
      .filter(
        _.percentageVaccinatedPopulation < threshold
      )
      .sortWith(
        matchOrderByUnvaccinationCountry(order_by)
      )
      .take(limit)

  private def matchOrderByUnvaccinationCountry(
      order_by: String
  ): (VaccinationPopulationReport, VaccinationPopulationReport) => Boolean =
    order_by match
      case s: String if s.toUpperCase() == "DESC" =>
        (_.percentageVaccinatedPopulation > _.percentageVaccinatedPopulation)
      case s: String if s.toUpperCase() == "ASC" =>
        (_.percentageVaccinatedPopulation < _.percentageVaccinatedPopulation)
      case _: String =>
        (_, _) => false
