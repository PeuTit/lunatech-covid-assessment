import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import models.Case
import models.CaseDao

class CaseSpec extends PlaySpec with GuiceOneAppPerSuite {
  def caseRepository: CaseDao =
    app.injector.instanceOf[CaseDao]

  "Case Model".should {
    "return a list of case".in {
      val actualCases: List[Case] = caseRepository.getAll()

      actualCases.length must equal(61579)
    }
  }
}
