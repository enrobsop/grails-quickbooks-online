package mcomp.grails.quickbooksonline

import org.scribe.model.Token

class BuilderHelper {

	static Token aToken() {
		new Token("key", "secret")
	}

}
