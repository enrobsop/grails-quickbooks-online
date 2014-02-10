package mcomp.grails.quickbooksonline

import grails.plugin.spock.UnitSpec
import spock.lang.Unroll

class QuickBooksHelperSpec extends UnitSpec {

	def "can a list of QuickBooks type names"() {
		expect: QuickBooksHelper.allQboTypeNames.containsAll([
				"Account", "Attachable", "Bill", "BillPayment", "Class", "CompanyInfo", "CreditMemo", "Customer",
				"Department", "Estimate", "Invoice", "Item", "JournalEntry", "Payment", "PaymentMethod", "Preferences",
				"Purchase", "PurchaseOrder", "SalesReceipt", "TaxCode", "TaxRate", "Term", "TimeActivity", "Vendor",
				"VendorCredit"
			])
	}

	@Unroll("correctly assesses whether #typeName is a valid QBO type")
	def "correctly assesses whether a name represents a valid type"() {

		expect:
			QuickBooksHelper.isQboType(typeName) == isQboType

		where:
			typeName    | isQboType
			"Account"   | true
			"account"   | true
			"Vendor"    | true
			""          | false
			"Xyz"       | false

	}

}
