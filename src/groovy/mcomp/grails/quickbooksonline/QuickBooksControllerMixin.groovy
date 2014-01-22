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

	def getBaseUrl() {
		quickBooksService.getBaseUrlForCompany(companyId)
	}

	def methodMissing(String name, args) {

		if( name ==~ /^getJsonResponseFor(\w+)/) {
			def m       = name =~ /^getJsonResponseFor(\w+)/
			def type    = m[0][1]

			if (type == "Query") {

				def url = "${baseUrl}/query"
				def querystringParams = args.size() > 0 ? [query: args[0]] : null
				return getJsonResponse(url, querystringParams)

			} else if (type in ["Account", "Attachable", "Bill", "BillPayment", "Class", "CompanyInfo", "CreditMemo", "Customer", "Department", "Estimate", "Invoice", "Item", "JournalEntry", "Payment", "PaymentMethod", "Preferences", "Purchase", "PurchaseOrder", "SalesReceipt", "TaxCode", "TaxRate", "Term", "TimeActivity", "Vendor", "VendorCredit"]) {

				def itemId  = args.size() > 0 ? args[0] : ""
				def url     = "${baseUrl}/${type.toLowerCase()}/${itemId}"
				return getJsonResponse(url)

			}

		}

		throw new MissingMethodException(name, getClass(), args)
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
