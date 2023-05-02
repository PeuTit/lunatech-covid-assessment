import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import models.Country
import models.CountryDao

class CountrySpec extends PlaySpec with GuiceOneAppPerSuite {
  def countryRepository: CountryDao =
    app.injector.instanceOf[CountryDao]

  "Country Model".should {
    "return a list of country".in {
      val actualCountries: List[Country] = countryRepository.getAll()

      actualCountries.length must equal(247)
    }
  }
}
