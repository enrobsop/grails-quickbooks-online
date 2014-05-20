grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {

	inherits("global")
	log "warn"

	repositories {
		grailsCentral()
		mavenCentral()
		mavenRepo "http://repo.desirableobjects.co.uk/"
		mavenRepo 'https://raw.github.com/fernandezpablo85/scribe-java/mvn-repo'
	}

	dependencies {
		compile 'org.scribe:scribe:1.3.6'
		test('org.spockframework:spock-grails-support:0.7-groovy-2.0',
				"org.objenesis:objenesis:1.2") {
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

		compile(":oauth:2.5") {
			excludes 'spock', 'spock-core', 'objenesis','gmock'
		}

	}

}
