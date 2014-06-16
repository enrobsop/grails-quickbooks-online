grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {

	inherits("global")
	log "warn"

	repositories {
		grailsCentral()
		mavenCentral()
		mavenRepo "http://repo.desirableobjects.co.uk/"
	}

	dependencies {

		runtime 'org.scribe:scribe:1.3.5'

		test 'org.spockframework:spock-grails-support:0.7-groovy-2.0'

	}

	plugins {

		build ':release:3.0.1', ':rest-client-builder:1.0.3', {
			export = false
		}

		test(':spock:0.7') {
			exclude 'spock-grails-support'
		}

		compile(":oauth:2.5") {
			excludes 'spock', 'spock-core', 'objenesis','gmock', 'scribe'
		}

	}

}
