package mcomp.grails.quickbooksonline

import org.scribe.model.Response
import org.scribe.model.Token

class QuickBooksControllerMixin {

	protected Token getAccessToken() {
		session[quickBooksService.sessionKeyForAccessToken]
	}

	protected Response getJsonResponse(url, querystringParams) {
		quickBooksService.getJsonResponse(accessToken, url, querystringParams)
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
