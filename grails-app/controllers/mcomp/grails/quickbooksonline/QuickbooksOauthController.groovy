package mcomp.grails.quickbooksonline

@Mixin(QuickBooksControllerMixin)
class QuickbooksOauthController {

	def callback() {

		session.companyId   = params.realmId
		session.dataSource  = params.dataSource
		session.provider    = params.provider

		forward controller:"oauth", action:"callback"

	}

}
