package controllers

import javax.inject.Inject
import play.api.*
import play.api.mvc.*
import play.api.db.Database

import anorm.as

import models.Case
import models.CasePopulationReport
import models.CaseCountryReport
import models.CaseDao

class CaseController @Inject() (
    caseDao: CaseDao,
    val controllerComponents: ControllerComponents,
) extends BaseController:
  def listRaw() = Action { implicit request: Request[AnyContent] =>
      val result: List[Case] = caseDao.getAll()
      Ok(views.html.infection_case.index(result))
  }

  def list() = Action { implicit request: Request[AnyContent] =>
    val result: List[CasePopulationReport]  = caseDao.getCasesPopulationReport()
    Ok(views.html.infection_case.report(result))
  }
