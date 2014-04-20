package mcomp.grails.quickbooksonline

import javax.servlet.http.HttpSession

class QuickBooksSessionManager {

	QuickBooksService quickBooksService

	void initSession(HttpSession session, QuickBooksSessionConfig config) {
		session.setAttribute(accessTokenKey,    config.accessToken)
		session.setAttribute("companyId",       config.companyId)
		session.setAttribute("dataSource",      config.dataSource)
		session.setAttribute("provider",        config.provider)
	}

	void clearSession(HttpSession session) {
		[accessTokenKey, "companyId", "dataSource", "provider"].each {
			session.removeAttribute(it)
		}
	}

	QuickBooksSessionConfig getSessionConfig(HttpSession session) {
		new QuickBooksSessionConfig(
			accessToken:    session.getAttribute(accessTokenKey),
			companyId:      session.getAttribute("companyId"),
			dataSource:     session.getAttribute("dataSource"),
			provider:       session.getAttribute("provider")
		)
	}

	private String getAccessTokenKey() {
		quickBooksService.sessionKeyForAccessToken
	}

}

