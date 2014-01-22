package mcomp.grails.quickbooksonline

import org.scribe.model.Response
import org.scribe.model.Token

class QuickBooksControllerMixin {

	protected Token getAccessToken() {
		session[quickBooksService.sessionKeyForAccessToken]
	}

	protected Token getRequestToken() {
		session[quickBooksService.sessionKeyForRequestToken]
	}

	protected Response getJsonResponse(url, querystringParams=[:]) {
		quickBooksService.getJsonResponse(accessToken, url, querystringParams)
	}

	protected String getCompanyId() {
		session.companyId
	}

	protected String getRealmId() {
		companyId
	}

	protected String getDataSource() {
		session.dataSource
	}

	protected String getProvider() {
		session.provider
	}

	protected void debug(Token theToken) {
		println """
	Token:
	======
	Token:  ${theToken?.token}
	Secret: ${theToken?.secret}
	Raw:    ${theToken?.rawResponse}

"""
	}

	protected void debug(Response res) {
		println """
	RESPONSE:
	=========
	Headers:
	--------
	${res?.headers}

	Body:
	-----
	${res?.body}

"""
	}

}
