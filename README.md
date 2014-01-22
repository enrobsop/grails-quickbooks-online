grails-quickbooks-online [![Build Status](https://travis-ci.org/enrobsop/grails-quickbooks-online.png?branch=master)](https://travis-ci.org/enrobsop/grails-quickbooks-online)
========================

Grails plugin for the QuickBooksOnline API.

## Pre-requisites
1. You have signed-up to the Intuit Partner Platform (IPP) at https://developer.intuit.com/.
2. You have created a new QuickBooks API app in https://developer.intuit.com/.

## Configuration
### OAuth Configuration
Add the following to `grails-app/config/Config.groovy`:
```groovy
import mcomp.grails.quickbooksonline.oauth.QuickBooksOnlineApi

...

oauth {
	providers {
		intuit {
			api     = QuickBooksOnlineApi
			key     = "APP OAUTH CONSUMER KEY"
			secret  = "APP OAUTH CONSUMER SECRET"
			successUri  = '/success'
			failureUri  = '/failure'
			callback    = "http://HOST.NAME.DOMAIN:8080/APP_CONTEXT/oauth/intuit/callback"
		}
	}
	debug = true
}
```

The _key_ and _secret_ values for your app are provided at https://developer.intuit.com/Application/List (or My Apps > Manage Apps > [App Name]).

The _callback_ url should use the same _Host Name Domain_ defined in _Manage Apps_ and **NOT _localhost_**. Editing your hosts file is an easy way to achieve this during development.

An example callback URL is: `http://mytestapp.co.uk:8080/QuickbooksDemo1/oauth/intuit/callback`

If in doubt, test the callback, success and failure URLs using a browser.

### Additional Configuration
Add the following to `grails-app/config/Config.groovy`:
```groovy
quickbooksonline {
	api {
		baseurl = "https://qb.sbfinance.intuit.com/v3"
	}
}
```

## Usage
Please also see the Intuit Partner Platform documentation:
  - [Query Language Operations](https://developer.intuit.com/docs/0025_quickbooksapi/0050_data_services/020_key_concepts/00300_query_operations/0100_key_topics)
  - [API Explorer](https://developer.intuit.com/apiexplorer?apiname=V3QBO)
  - [API Reference](https://developer.intuit.com/docs/0025_quickbooksapi/0050_data_services)


### Sample Controller
```groovy

@Mixin(QuickBooksControllerMixin)		// 1. Add mixin
class MyController {

	def quickBooksService			// 2. Add service which is used by the mixin
	
	def index() {}

	def listCustomers() {

		// Redirect to connect page if no access token is available.
		if (!accessToken) {
			flash.message = "Please connect to Quickbooks"
			redirect action:"index"
			return
		}
		debug(accessToken)
		debugSession()

		// Get Customers. 		// 3. Execute query
		Response res = getJsonResponseForQuery("SELECT Id, DisplayName, Active, Balance FROM Customer")
		debug(res)

		def customers = []
		if (res.isSuccessful()) {
						// 4. Parse JSON response
			def result = new JsonSlurper().parseText(res.body)
			customers = result."QueryResponse"."Customer"
		}

		[res: res, customers: customers]

	}

	def listInvoices() {
		if (!accessToken) {
			....
		}

		def filter = params.customerId ? "WHERE CustomerRef='${params.customerId}'" : ""
		Response res = getJsonResponseForQuery("SELECT TxnDate, Id, DocNumber, CustomerRef, TotalAmt, Balance, Line FROM Invoice ${filter} ORDERBY TxnDate DESC")

		def invoices = []
		if (res.isSuccessful()) {
			def result = new JsonSlurper().parseText(res.body)
			invoices = result."QueryResponse"."Invoice"
		}

		[res: res, invoices: invoices]

	}

	def showCustomer() {
		if (!accessToken) {
			...
		}

		Response res = getJsonResponseForCustomer(params.customerId)
		def customer
		if (res.isSuccessful()) {
			def result = new JsonSlurper().parseText(res.body)
			customer = result."Customer"
		}

		[res: res, customer: customer]

	}

}

```

### Sample View
```gsp
<html>
<head>
	<meta name="layout" content="main"/>
</head>
<body>

<h1>Quickbooks Demo </h1>
<h2>Welcome</h2>

<g:if test="${flash.message}">
	<div class="message" style="display: block">${flash.message}</div>
</g:if>

<!-- Connect to Quickbooks Link - this will return to the 'callback' url specified in the config -->
<oauth:connect provider="intuit">Connect to Quickbooks...</oauth:connect>

...

<hr />
Response status: ${res?.code}
<hr />

<h2>Invoices (${invoices?.size})</h2>
<table>
	<thead>
	<tr>
		<th>ID</th>
		<th>Date</th>
		<th>Doc Number</th>
		<th>Customer</th>
		<th>Total</th>
		<th>Balance</th>
		<th>Lines</th>
	</tr>
	</thead>
	<tbody>
	<g:each in="${invoices}" var="invoice">
		<tr>
			<td>${invoice.Id}</td>
			<td><g:formatDate date="${dateFormat.parse(invoice.TxnDate)}" type="date" dateStyle="medium" /></td>
			<td>${invoice.DocNumber}</td>
			<td><g:link action="showCustomer" params="${[customerId:invoice.CustomerRef.value]}">${invoice.CustomerRef.name}</g:link></td>
			<td><g:formatNumber number="${invoice.TotalAmt}" currencyCode="GBP" type="currency" /></td>
			<td><g:formatNumber number="${invoice.Balance}" currencyCode="GBP" type="currency" /></td>
			<td>
				<ol>
				<g:each in="${invoice.Line}" var="line">
					<li>${line}</li>
				</g:each>
				</ol>
			</td>
		</tr>
	</g:each>
	</tbody>
</table>

</body>
</html>
```
