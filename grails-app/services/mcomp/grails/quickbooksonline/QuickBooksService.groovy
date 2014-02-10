package mcomp.grails.quickbooksonline
import org.scribe.model.Response
import org.scribe.model.Token
import uk.co.desirableobjects.oauth.scribe.OauthService

import static mcomp.grails.quickbooksonline.QuickBooksHelper.*

class QuickBooksService {

	OauthService oauthService

	def grailsApplication

	String getSessionKeyForAccessToken() {
		oauthService.findSessionKeyForAccessToken('intuit')
	}

	String getSessionKeyForRequestToken() {
		oauthService.findSessionKeyForRequestToken('intuit')
	}

	Response getJsonResponse(Token token, String url, Map<String,String> querystringParams) {
		def extraHeaders = [Accept:"application/json", "Content-Type":"application/json"]
		oauthService.getIntuitResourceWithQuerystringParams(token, url, querystringParams, extraHeaders)
	}

	String getBaseUrl() {
		def url = grailsApplication.config.quickbooksonline?.api?.baseurl ?: "https://qb.sbfinance.intuit.com/v3"
		removeTrailingSlashes(url)
	}

	String getBaseUrlForCompany(String companyId) {
		"${baseUrl}/company/${companyId}"
	}

	private def removeTrailingSlashes(def str) {
		str.replaceAll(/\/+$/, "")
	}

	def methodMissing(String name, args) {
		if( name ==~ GET_JSON_RESPONSE_FOR_PATTERN) {
			def m       = name =~ GET_JSON_RESPONSE_FOR_PATTERN
			def type    = m[0][1]

			if (type == "Query" && args.size() == 3) {

				def url                 = "${getBaseUrlForCompany(args[1])}/query"
				def token               = args[0]
				def querystringParams   = [query: args[2]]
				return getJsonResponse(token, url, querystringParams)

			} else if (args.size() == 3 && isQboType(type)) {

				def itemId  = args[2]
				def url     = "${getBaseUrlForCompany(args[1])}/${type.toLowerCase()}/${itemId}"
				def token   = args[0]
				return getJsonResponse(token, url, [:])

			}

		}

		throw new MissingMethodException(name, getClass(), args)
	}

}
