package mcomp.grails.quickbooksonline

import grails.plugin.spock.UnitSpec
import org.scribe.model.Token
import org.springframework.mock.web.MockHttpSession

class QuickBooksSessionManagerSpec extends UnitSpec {

	QuickBooksService           quickBooksService
	QuickBooksSessionManager    sessionManager

	def setup() {
		sessionManager = new QuickBooksSessionManager()

		quickBooksService = Mock(QuickBooksService)
		sessionManager.quickBooksService = quickBooksService
	}

	def "the session is correctly initialised and cleared"() {

		given: "a session"
			def theSession = new MockHttpSession()
		and: "a token"
			def theToken = new Token("token123","secret123")
		and: "some values to store in the session"
			def theSessionConfig = new QuickBooksSessionConfig(
				accessToken:    theToken,
				companyId:      "12345678",
				dataSource:     "QBO",
				provider:       "intuit"
			)

		when: "initialising the session"
			sessionManager.initSession(theSession, theSessionConfig)

		then: "the correct calls to dependencies are made"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "accessToken"
		and: "the correct values are initialised"
			theSession.getAttribute("accessToken")  == theToken
			theSession.getAttribute("companyId")    == "12345678"
			theSession.getAttribute("dataSource")   == "QBO"
			theSession.getAttribute("provider")     == "intuit"

		when: "capturing the session"
			def capturedConfig = sessionManager.getSessionConfig(theSession)

		then: "the correct calls to dependencies are made"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "accessToken"
		and: "the correct values are returned"
			capturedConfig.accessToken  == theToken
			capturedConfig.companyId	== "12345678"
			capturedConfig.dataSource	== "QBO"
			capturedConfig.provider	    == "intuit"

		when: "clearing the session"
			sessionManager.clearSession(theSession)

		then: "the correct calls to dependencies are made"
			1 * quickBooksService.getSessionKeyForAccessToken() >> "accessToken"
		and: "the values have been correctly removed"
			theSession.getAttribute("accessToken")  == null
			theSession.getAttribute("companyId")    == null
			theSession.getAttribute("dataSource")   == null
			theSession.getAttribute("provider")     == null

	}

}
