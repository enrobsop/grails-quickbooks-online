class QuickBooksOnlineGrailsPlugin {

	def version = "0.1.1"
	def grailsVersion = "2.0 > *"
	def pluginExcludes = [
			"grails-app/domain/**",
			"grails-app/i18n/**",
			"grails-app/views/**/*",
			"web-app/**"
	]

	def title = "Quick Books Online Plugin"
	def author = "Paul Osborne"
	def authorEmail = "hello@paulosborne.me.uk"
	def description = '''\
Grails plugin for integration with the QuickBooksOnline API.
'''

	def documentation = "https://github.com/enrobsop/grails-quickbooks-online/blob/master/README.md"
	def license = "APACHE"
	def organization = [ name: "Paul Osborne", url: "http://www.paulosborne.me.uk/" ]
	def issueManagement = [ system: "GitHub", url: "https://github.com/enrobsop/grails-quickbooks-online/issues" ]
	def scm = [ url: "https://github.com/enrobsop/grails-quickbooks-online" ]

}
