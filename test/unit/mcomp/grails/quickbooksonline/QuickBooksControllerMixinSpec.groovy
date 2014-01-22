package mcomp.grails.quickbooksonline
import grails.plugin.spock.UnitSpec
import org.scribe.model.Response
import org.scribe.model.Token

class QuickBooksControllerMixinSpec extends UnitSpec {

	static controller
	static quickBooksService

	def setup() {
		quickBooksService = Mock(QuickBooksService)
		controller = new DummyController()
		controller.quickBooksService = quickBooksService
		controller.session = [:]
	}

	def "user can get the access token from the controller"() {

		given: "a session"
			def theToken = aToken()
			controller.session.oauth_access_token = theToken

		when:
			def result = controller.getAccessToken()

		then: "the correct calls are made"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "oauth_access_token"
		and: "the correct token is returned"
			result == theToken

	}

	def "user can get the request token from the controller"() {

		given: "a session"
			def theToken = aToken()
			controller.session.oauth_request_token = theToken

		when:
			def result = controller.getRequestToken()

		then: "the correct calls are made"
			1 * quickBooksService.getSessionKeyForRequestToken() >> "oauth_request_token"
		and: "the correct token is returned"
			result == theToken

	}

	def "user can get a JSON response without explicitly providing a token"() {

		given: "a session"
			def theToken = aToken()
			controller.session.oauth_access_token = theToken
		and: "params"
			def theUrl = "http://someUrl/quickbooks/etc"
			def theQuerystringParams = [query: "select something from something"]
		and: "a response"
			def expectedResponse = Mock(Response)

		when:
			def result = controller.getJsonResponse(theUrl, theQuerystringParams)

		then: "the token is requested implicitly"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "oauth_access_token"
		and: "the mixin correctly delegates to the service"
			1 * quickBooksService.getJsonResponse(theToken, theUrl, theQuerystringParams) >> expectedResponse
		and: "the response is correctly returned"
			result == expectedResponse

	}

	def "the application should not throw an exception when getting a JsonResponse without any querystringParams"() {

		when:
			controller.getJsonResponse("http://somewhere")

		then:
			notThrown MissingMethodException

	}

	def "user can access session variables via convenient accessors aliases"() {

		when: "a session"
			controller.session = [
				companyId:  "1234567890",
				dataSource: "QBO",
				provider:   "intuit"
			]

		then:
			controller.companyId    == "1234567890"
			controller.dataSource   == "QBO"
			controller.provider     == "intuit"
		and: "realmId is an alias for companyId"
			controller.realmId == controller.companyId

	}

	def "user can get the baseUrl for the current company"() {

		given: "a current company"
			def theCompanyId = "1234567890"
		and: "a session"
			controller.session = [companyId:  theCompanyId]
		and: "an expected url"
			def expectedUrl = "http://the.base.url/company/$theCompanyId"

		when:
			def result = controller.baseUrl

		then: "the controller correctly delegates"
			1 * quickBooksService.getBaseUrlForCompany(theCompanyId) >> expectedUrl
		and:
			result == expectedUrl

	}

	def "user can submit a dynamic query like getJsonResponseForQuery"() {

		given: "params"
			String theQuery = "SELECT * FROM Customer"
		and: "a session"
			def theToken = aToken()
			controller.session.oauth_access_token = theToken
			controller.session.companyId = "1234567"
		and: "a response"
			def expectedResponse = Mock(Response)

		when: "submitting a call to a dynamic method"
			Response result = controller.getJsonResponseForQuery(theQuery)

		then: "the method is found dynamically"
			notThrown MissingMethodException
		and: "the token is requested implicitly"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "oauth_access_token"
		and: "the base url is requested"
			1 * quickBooksService.getBaseUrlForCompany("1234567") >> "http://the.base.url/company/1234567"
		and: "the mixin correctly delegates to the service"
			1 * quickBooksService.getJsonResponse(
					theToken,
					"http://the.base.url/company/1234567/query",
					[query: theQuery]
			) >> expectedResponse
		and: "the response is correctly returned"
			result == expectedResponse

	}

	def "user can submit a dynamic item request like getJsonResponseForCustomer"() {

		given: "params"
			String theCustomerId = "789"
		and: "a session"
			def theToken = aToken()
			controller.session.oauth_access_token = theToken
			controller.session.companyId = "12345678"
		and: "a response"
			def expectedResponse = Mock(Response)

		when: "submitting a call to a dynamic method"
			Response result = controller.getJsonResponseForCustomer(theCustomerId)

		then: "the method is found dynamically"
			notThrown MissingMethodException
		and: "the token is requested implicitly"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "oauth_access_token"
		and: "the base url is requested"
			1 * quickBooksService.getBaseUrlForCompany("12345678") >> "http://the.base.url/company/12345678"
		and: "the mixin correctly delegates to the service"
			1 * quickBooksService.getJsonResponse(
					theToken,
					"http://the.base.url/company/12345678/customer",
					[:]
			) >> expectedResponse
		and: "the response is correctly returned"
			result == expectedResponse

	}

	private Token aToken() {
		new Token("key", "secret")
	}

	@Mixin(QuickBooksControllerMixin)
	class DummyController {

		def quickBooksService
		def session

	}

}
