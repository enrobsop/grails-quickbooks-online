package mcomp.grails.quickbooksonline

class QuickBooksHelper {

	static final ALL_QBO_TYPE_NAMES = [
		"Account",
		"Attachable",
		"Bill",
		"BillPayment",
		"Class",
		"CompanyInfo",
		"CreditMemo",
		"Customer",
		"Department",
		"Estimate",
		"Invoice",
		"Item",
		"JournalEntry",
		"Payment",
		"PaymentMethod",
		"Preferences",
		"Purchase",
		"PurchaseOrder",
		"SalesReceipt",
		"TaxCode",
		"TaxRate",
		"Term",
		"TimeActivity",
		"Vendor",
		"VendorCredit"
	]

	static final String GET_JSON_RESPONSE_FOR_PATTERN = /^getJsonResponseFor(\w+)/

	static List<String> getAllQboTypeNames() {
		ALL_QBO_TYPE_NAMES
	}

	static boolean isQboType(String typeName) {
		allQboTypeNames.contains(typeName.capitalize())
	}

}
