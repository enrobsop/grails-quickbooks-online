package mcomp.grails.quickbooksonline

import org.scribe.model.Response
import org.scribe.model.Token

class QuickBooksControllerMixin {

	protected Token getToken() {
		session[quickbooksService.tokenSessionKey]
	}

	protected Response getJsonResponse(url, querystringParams) {
		quickbooksService.getJsonResponse(token, url, querystringParams)
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
