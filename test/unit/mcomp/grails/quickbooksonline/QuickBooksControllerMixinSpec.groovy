package mcomp.grails.quickbooksonline

import grails.plugin.spock.UnitSpec
import org.scribe.model.Response
import org.scribe.model.Token

class QuickBooksControllerMixinSpec extends UnitSpec {

	def "user can get the token from the controller"() {

		given: "a controller"
			def controller = new DummyController()
			def quickBooksService = Mock(QuickBooksService)
			controller.quickBooksService = quickBooksService
		and: "a session"
			def theToken = new Token("key","secret")
			def session = [oauth_access_token: theToken]
			controller.session = session

		when:
			def result = controller.getAccessToken()

		then: "the correct calls are made"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "oauth_access_token"
		and: "the correct token is returned"
			result == theToken

	}

	def "user can get a JSON response without explicitly providing a token"() {

		given: "a controller"
			def controller = new DummyController()
			def quickBooksService = Mock(QuickBooksService)
			controller.quickBooksService = quickBooksService
		and: "a session"
			def theToken = new Token("key","secret")
			def session = [oauth_access_token: theToken]
			controller.session = session
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

	@Mixin(QuickBooksControllerMixin)
	class DummyController {

		def quickBooksService
		def session

	}

}
