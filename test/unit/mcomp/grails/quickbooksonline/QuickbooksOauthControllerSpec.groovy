package mcomp.grails.quickbooksonline

import grails.plugin.spock.UnitSpec
import grails.test.mixin.TestFor

@TestFor(QuickbooksOauthController)
class QuickbooksOauthControllerSpec extends UnitSpec {

	void "the oauth call back works correctly"() {

		given: "params"
			controller.params.with {
				realmId      = "123456"
				dataSource   = "QBO"
				provider     = "intuit"
			}

		when:
			controller.callback()

		then: "it gets the QBO specific data from the params"
			session.companyId   == "123456"
			session.dataSource  == "QBO"
			session.provider    == "intuit"
		and: "it delegates to the oauth plugin callback"
			response.forwardedUrl == "/grails/oauth/callback.dispatch"

	}

}