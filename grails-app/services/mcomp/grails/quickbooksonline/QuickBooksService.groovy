package mcomp.grails.quickbooksonline
import org.scribe.model.Response
import org.scribe.model.Token
import uk.co.desirableobjects.oauth.scribe.OauthService

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

}
