# HelloEDI

Reader and report generator for EDI (specifically ASC X12 type 834 Benefit Enrollment and Maintenance)

Uses EDIReader library from Berryworks Software

Generates the following report from a sample EDI file

```
----------------------------

PROJECT DETAILS

  EDI File Type 
	Implementation:		ASC X12 
	Transaction:		834 - Benefit Enrollment and Maintenance

  Table Reference 
	Document:			Standard Companion Guide Transaction Information 
	Publisher:			Centers for Medicare & Medicaid Services (CMS) 
	URL:				https://www.cms.gov/CCIIO/Resources/Regulations-and-Guidance/Downloads/companion-guide-for-ffe-enrollment-transaction-v15.pdf

----------------------------

PREPARING REPORT

  Converting to XML...
	Output file edi/834.xml created
	Transformation complete
  Parsing XML...

----------------------------

GENERATED REPORT

  EDI File Information

	Sender ID:		386028429      
	Receiver ID:	382328142      
	Doc Type:		834
	Doc Name:		Benefit Enrollment and Maintenance

  Insurance Information

	Plan Sponsor: 		WAYNE STATE UNIVERSITY
	Insurer: 		WAYNE STATE UNIVERSITY
	Third Party Admin: 	WEYCO

  Member Information

	Member First Name:	John
	Member Last Name:	Doe
	Member ID:			123456
	Prior First Name:	Samuel
	Prior Last Name:	Prior
	Prior Member ID:	19330706

  Maintenances 

	Total Maintenances:	43

----------------------------
```
