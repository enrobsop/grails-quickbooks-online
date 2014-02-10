package mcomp.grails.quickbooksonline

import org.scribe.model.Response
import org.scribe.model.Token

import static mcomp.grails.quickbooksonline.QuickBooksHelper.*

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

	def getBaseUrl() {
		quickBooksService.getBaseUrlForCompany(companyId)
	}

	def methodMissing(String name, args) {

		if( name ==~ GET_JSON_RESPONSE_FOR_PATTERN) {
			def m       = name =~ GET_JSON_RESPONSE_FOR_PATTERN
			def type    = m[0][1]

			if (type == "Query") {

				def url = "${baseUrl}/query"
				def querystringParams = args.size() > 0 ? [query: args[0]] : null
				return getJsonResponse(url, querystringParams)

			} else if (isQboType(type)) {

				def itemId  = args.size() > 0 ? args[0] : ""
				def url     = "${baseUrl}/${type.toLowerCase()}/${itemId}"
				return getJsonResponse(url)

			}

		}

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
