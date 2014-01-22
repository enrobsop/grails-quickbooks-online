package mcomp.grails.quickbooksonline

import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor
import org.scribe.model.Response
import org.scribe.model.Token
import uk.co.desirableobjects.oauth.scribe.OauthService

@TestFor(QuickBooksService)
class QuickBooksServiceSpec extends UnitSpec {

	def "user can get the session key containing the access token"() {

		given:
			OauthService oauthService = Mock(OauthService)
			service.oauthService = oauthService

		when:
			def result = service.sessionKeyForAccessToken

		then: "the service delegates correctly"
			1 * oauthService.findSessionKeyForAccessToken("intuit") >> "theAccessTokenKey"
		and: "the correct key is given"
			result == "theAccessTokenKey"

	}

	def "user can get the session key containing the request token"() {

		given:
			OauthService oauthService = Mock(OauthService)
			service.oauthService = oauthService

		when:
			def result = service.sessionKeyForRequestToken

		then: "the service delegates correctly"
			1 * oauthService.findSessionKeyForRequestToken("intuit") >> "theRequestTokenKey"
		and: "the correct key is given"
			result == "theRequestTokenKey"

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

	def "user can get the base url"() {

		when: "NO base url is configured"
		then: "the base url returned is the default"
			service.baseUrl == "https://qb.sbfinance.intuit.com/v3"
		and: "the base url for a company is correct"
			service.getBaseUrlForCompany("1234") == "https://qb.sbfinance.intuit.com/v3/company/1234"

		when: "the base url IS configured"
			grailsApplication.config.quickbooksonline.api.baseurl = "https://someurl/"
		then: "the base url returned is the one configured minus any trailing '/'"
			service.baseUrl == "https://someurl"
		and: "the base url for a company is correct"
			service.getBaseUrlForCompany("1234") == "https://someurl/company/1234"

		when: "the base url has no trailing slash"
			grailsApplication.config.quickbooksonline.api.baseurl = "https://no.trailing"
		then: "the base url is unaltered"
			service.baseUrl == "https://no.trailing"

		when: "the base url has multiple trailing slashes"
			grailsApplication.config.quickbooksonline.api.baseurl = "https://multi.trailing//"
		then: "the base url is trimmed"
			service.baseUrl == "https://multi.trailing"

	}

}
