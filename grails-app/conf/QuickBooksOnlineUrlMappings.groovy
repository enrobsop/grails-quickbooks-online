class QuickBooksOnlineUrlMappings {

	static mappings = {
		"/qbooauth/$provider/callback"(controller: 'quickbooksOauth', action: 'callback')
	}

}
