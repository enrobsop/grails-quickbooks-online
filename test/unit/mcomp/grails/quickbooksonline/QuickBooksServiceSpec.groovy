package mcomp.grails.quickbooksonline

import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor
import org.scribe.model.Response
import org.scribe.model.Token
import uk.co.desirableobjects.oauth.scribe.OauthService

@TestFor(QuickBooksService)
class QuickBooksServiceSpec extends UnitSpec {

	def "user can get the session key containing the access token"() {

		given: "args"
			OauthService oauthService = Mock(OauthService)
			service.oauthService = oauthService

		when:
			def result = service.tokenSessionKey

		then: "the service delegates correctly"
			1 * oauthService.findSessionKeyForAccessToken("intuit") >> "theKey"
		and: "the correct key is given"
			result == "theKey"

	}

	def "user can execute a request and get results as JSON"() {

		given: "args"
			def theUrl = "https://qb.sbfinance.intuit.com/v3/company/1234567890/query"
			def theQuerystringParms = [query: "SELECT Id, DisplayName, Active, Balance FROM Customer"]
		and: "an access token"
			def theToken = new Token("token","secret")
		and: "a service"
			OauthService oauthService = Mock(OauthService)
			service.oauthService = oauthService
		and: "the expected response"
			def expectedResponse = Mock(Response)

		when:
			Response response = service.getJsonResponse(theToken, theUrl, theQuerystringParms)

		then: "the plugin delegates correctly"
			1 * oauthService.methodMissing('getIntuitResourceWithQuerystringParams', [
					theToken,
					theUrl,
					theQuerystringParms,
					[Accept:"application/json", "Content-Type":"application/json"]
			]) >> expectedResponse
		and: "the correct response is returned"
			response == expectedResponse

	}

}
