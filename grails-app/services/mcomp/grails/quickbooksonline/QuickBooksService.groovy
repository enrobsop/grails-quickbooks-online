package mcomp.grails.quickbooksonline
import org.scribe.model.Response
import org.scribe.model.Token
import org.springframework.web.context.request.RequestContextHolder
import uk.co.desirableobjects.oauth.scribe.OauthService

import javax.servlet.http.HttpSession

import static mcomp.grails.quickbooksonline.QuickBooksHelper.*

class QuickBooksService {

	OauthService oauthService

	def userSession

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
		log.debug "Received ${name} with [${args}]"
		def response = null
		if( name ==~ GET_JSON_RESPONSE_FOR_PATTERN) {
			def m       = name =~ GET_JSON_RESPONSE_FOR_PATTERN
			def type    = m[0][1]

			if (type == "Query") {
				response = handleDynamicQuery(args)
			} else if (isQboType(type)) {
				response = handleTypeLookup(type, args)
			}
		}

		if (!response) {
			throw new MissingMethodException(name, getClass(), args)
		}
		response
	}

	private Response handleDynamicQuery(args) {
		def url, token, querystringParams
		switch(args.size()) {
			case 1:
				url                 = "${getBaseUrlForCompany(session.companyId)}/query"
				token               = session[sessionKeyForAccessToken]
				querystringParams   = [query: args[0]]
				break
			case 3:
				url                 = "${getBaseUrlForCompany(args[1])}/query"
				token               = args[0]
				querystringParams   = [query: args[2]]
		}
		if (token && url && querystringParams) {
			log.debug("calling getJsonResponse with: token:$token, $url: $url, querystringParams:$querystringParams")
			return getJsonResponse(token, url, querystringParams)
		}
	}

	private Response handleTypeLookup(type, args) {
		def url, token, itemId
		switch(args.size()) {
			case 1:
				itemId  = args[0]
				url     = "${getBaseUrlForCompany(session.companyId)}/${type.toLowerCase()}/${itemId}"
				token   = session[sessionKeyForAccessToken]
				break
			case 3:
				itemId  = args[2]
				url     = "${getBaseUrlForCompany(args[1])}/${type.toLowerCase()}/${itemId}"
				token   = args[0]
		}
		if (token && url) {
			return getJsonResponse(token, url, [:])
		}
	}

	private def getSession() {
		if (!userSession) {
			userSession = RequestContextHolder.currentRequestAttributes().getSession()
		}
		userSession
	}

}
