package mcomp.grails.quickbooksonline.oauth

import org.scribe.builder.api.DefaultApi10a
import org.scribe.model.Token

class QuickBooksOnlineApi extends DefaultApi10a {

	@Override
	String getRequestTokenEndpoint() {
		"https://oauth.intuit.com/oauth/v1/get_request_token"
	}

	@Override
	String getAccessTokenEndpoint() {
		"https://oauth.intuit.com/oauth/v1/get_access_token"
	}

	@Override
	String getAuthorizationUrl(Token token) {
		"https://appcenter.intuit.com/Connect/Begin?oauth_token=${token.token}"
	}

}

