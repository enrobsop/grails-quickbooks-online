grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

def inlinePluginsDir = ".."
grails.plugin.location.'oauth' = "${inlinePluginsDir}/grails-oauth-scribe" // Fork and clone: https://github.com/aiten/grails-oauth-scribe

grails.project.dependency.resolution = {

	inherits("global")
	log "warn"

	repositories {
		grailsCentral()
		mavenCentral()
	}

	dependencies {
		compile 'org.scribe:scribe:1.3.5'
		test('org.spockframework:spock-grails-support:0.7-groovy-2.0') {
			export = false
		}
	}

	plugins {

		build(":release:2.2.1",
				":rest-client-builder:1.0.3") {
			export = false
		}

		test(':spock:0.7') {
			export = false
			exclude 'spock-grails-support'
		}

	}

}
