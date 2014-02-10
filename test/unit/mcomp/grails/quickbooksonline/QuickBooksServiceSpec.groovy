package mcomp.grails.quickbooksonline

import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import org.scribe.model.Response
import org.scribe.model.Token
import spock.lang.Unroll
import uk.co.desirableobjects.oauth.scribe.OauthService

@TestFor(QuickBooksService)
@TestMixin(BuilderHelper)
class QuickBooksServiceSpec extends UnitSpec {

	OauthService oauthService
	def session

	def setup() {
		oauthService = Mock(OauthService)
		service.oauthService = oauthService
		grailsApplication.config.quickbooksonline.api.baseurl = "https://qb.sbfinance.intuit.com/v3"
		session = [:]
		service.userSession = session
	}

	def "user can get the session key containing the access token"() {

		when:
			def result = service.sessionKeyForAccessToken

		then: "the service delegates correctly"
			1 * oauthService.findSessionKeyForAccessToken("intuit") >> "theAccessTokenKey"
		and: "the correct key is given"
			result == "theAccessTokenKey"

	}

	def "user can get the session key containing the request token"() {

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

	def "user can submit a dynamic query like getJsonResponseForQuery using params"() {

		given: "params"
			String theQuery = "SELECT * FROM Customer"
			def theToken = aToken()
			def theCompanyId = "123456789"
		and: "a response"
			def expectedResponse = Mock(Response)

		when: "submitting a call to a dynamic method"
			Response result = service.getJsonResponseForQuery(theToken, theCompanyId, theQuery)

		then: "the method is found dynamically"
			notThrown MissingMethodException
		and: "the mixin correctly delegates to the service"
			1 * oauthService.methodMissing(
				"getIntuitResourceWithQuerystringParams", {
					assert it[0] == theToken
					assert it[1] == "https://qb.sbfinance.intuit.com/v3/company/${theCompanyId}/query"
					assert it[2] == [query: theQuery]
					assert it[3].size() > 0
					true
			}) >> expectedResponse
		and: "the response is correctly returned"
			result == expectedResponse

	}

	def "user can submit a dynamic query like getJsonResponseForQuery using session values"() {

		given: "params"
			String theQuery = "SELECT * FROM Customer"
		and: "a session"
			def theToken        = aToken()
			def theCompanyId    = "1234567"
			configureSessionWith([
				token:      theToken,
				companyId:  theCompanyId
			])
		and: "a response"
			def expectedResponse = Mock(Response)

		when: "submitting a call to a dynamic method"
			Response result = service.getJsonResponseForQuery(theQuery)

		then: "the method is found dynamically"
			notThrown MissingMethodException
		and: "the mixin correctly delegates to the service"
			1 * oauthService.methodMissing(
				"getIntuitResourceWithQuerystringParams", {
				assert it[0] == theToken
				assert it[1] == "https://qb.sbfinance.intuit.com/v3/company/${theCompanyId}/query"
				assert it[2] == [query: theQuery]
				assert it[3].size() > 0
				true
			}) >> expectedResponse
		and: "the response is correctly returned"
			result == expectedResponse

	}

	@Unroll("user can read a #type using getJsonResponseFor#type")
	def "user can submit a dynamic item read request like getJsonResponseForCustomer using params"() {

		given: "params"
			String theItemId = "789"
		and: "a session"
			def theToken = aToken()
		and: "a response"
			def expectedResponse = Mock(Response)
		and: "a company"
			def theCompanyId = "123456789"

		when: "submitting a call to a dynamic method"
			Response result = service."getJsonResponseFor${type}"(theToken, theCompanyId, theItemId)

		then: "the method is found dynamically"
			notThrown MissingMethodException
		and: "the mixin correctly delegates to the service"
			1 * oauthService.methodMissing(
				"getIntuitResourceWithQuerystringParams", {
					assert it[0] == theToken
					assert it[1] == "https://qb.sbfinance.intuit.com/v3/company/${theCompanyId}/${type.toLowerCase()}/${theItemId}"
					assert it[2] == [:]
					assert it[3].size() > 0
					true
				}) >> expectedResponse
		and: "the response is correctly returned"
			result == expectedResponse

		where:
			type << QuickBooksHelper.allQboTypeNames

	}

	@Unroll("user can read a #type using getJsonResponseFor#type")
	def "user can submit a dynamic item read request like getJsonResponseForCustomer using values in the session"() {

		given: "params"
			String theItemId = "789"
		and: "a session"
			def theCompanyId = "123456789"
			def theToken = aToken()
			configureSessionWith([
				token:      theToken,
				companyId:  theCompanyId
			])
		and: "a response"
			def expectedResponse = Mock(Response)

		when: "submitting a call to a dynamic method"
			Response result = service."getJsonResponseFor${type}"(theItemId)

		then: "the method is found dynamically"
			notThrown MissingMethodException
		and: "the mixin correctly delegates to the service"
			1 * oauthService.methodMissing(
				"getIntuitResourceWithQuerystringParams", {
				assert it[0] == theToken
				assert it[1] == "https://qb.sbfinance.intuit.com/v3/company/${theCompanyId}/${type.toLowerCase()}/${theItemId}"
				assert it[2] == [:]
				assert it[3].size() > 0
				true
			}) >> expectedResponse
		and: "the response is correctly returned"
			result == expectedResponse

		where:
			type << QuickBooksHelper.allQboTypeNames

	}

	private void configureSessionWith(props) {
		oauthService.findSessionKeyForAccessToken(_) >> "oauth_access_token"
		session.with {
			oauth_access_token  = props.token
			companyId           = props.companyId
		}
	}

}
