package mcomp.grails.quickbooksonline
import org.scribe.model.Response
import org.scribe.model.Token
import uk.co.desirableobjects.oauth.scribe.OauthService

class QuickBooksService {

	OauthService oauthService

	String getTokenSessionKey() {
		oauthService.findSessionKeyForAccessToken('intuit')
	}

	Response getJsonResponse(Token token, String url, Map<String,String> querystringParams) {
		def extraHeaders = [Accept:"application/json", "Content-Type":"application/json"]
		oauthService.getIntuitResourceWithQuerystringParams(token, url, querystringParams, extraHeaders)
	}

}
