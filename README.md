grails-quickbooks-online [![Build Status](https://travis-ci.org/enrobsop/grails-quickbooks-online.png?branch=master)](https://travis-ci.org/enrobsop/grails-quickbooks-online)
========================

Grails plugin for the QuickBooksOnline API.

## Pre-requisites
1. You have signed-up to the Intuit Partner Platform (IPP) at https://developer.intuit.com/.
2. You have created a new QuickBooks API app in https://developer.intuit.com/.

## Configuration
Add the following to `grails-app/config/Config.groovy`:
```groovy
import mcomp.grails.quickbooksonline.QuickbooksOnlineApi

...

oauth {
	providers {
		intuit {
			api     = QuickbooksOnlineApi
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

