package com.israelvicars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.berryworks.edireader.demo.EDItoXML;

public class ReportEdi834 {

    private static String mInputEdiDirectory;
    private static String mOutputXmlDirectory;
    private static Document mXmlDocument;
	
    public static void main(String args[]) {
    	new ReportEdi834("edi/834.edi","edi/834.xml");
    }
    
    ReportEdi834(String inputEdiDirectory, String outputXmlDirectory) {
        mInputEdiDirectory = "edi/834.edi";
        mOutputXmlDirectory = "edi/834.xml";
        
        runReport();
    }
    
    private void runReport() {
    	System.out.println("\n----------------------------\n");
    	System.out.println("PROJECT DETAILS");
    	displayProjectDetails();
    	System.out.println("\n----------------------------\n");

    	System.out.println("PREPARING REPORT");
    	convertEdiToXml();
		parseXml();
		System.out.println("\n----------------------------\n");

    	System.out.println("GENERATED REPORT");
		generateAndDisplayReport();
		System.out.println("\n----------------------------\n");
    }

    private void displayProjectDetails() {
    	String implementation = "ASC X12";
    	String transaction = "834 - Benefit Enrollment and Maintenance";
    	String documentationTitle = "Standard Companion Guide Transaction Information";
    	String documentationPublisher = "Centers for Medicare & Medicaid Services (CMS)";
    	String documentationURL = "https://www.cms.gov/CCIIO/Resources/Regulations-and-Guidance/Downloads/companion-guide-for-ffe-enrollment-transaction-v15.pdf";
    	
    	System.out.printf("\n  EDI File Type \n\tImplementation:\t\t%s \n\tTransaction:\t\t%s\n", 
    			implementation, transaction);
    	System.out.printf("\n  Table Reference \n\tDocument:\t\t%s \n\tPublisher:\t\t%s \n\tURL:\t\t\t%s\n", 
    			documentationTitle, documentationPublisher, documentationURL);
    }
    
	private void convertEdiToXml () {
        Reader inputReader;
        if (mInputEdiDirectory == null) {
            inputReader = new InputStreamReader(System.in);
        } else {
            try {
                inputReader = new InputStreamReader(
                        new FileInputStream(mInputEdiDirectory), "ISO-8859-1");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }

        Writer generatedOutput;
        if (mOutputXmlDirectory == null) {
            generatedOutput = new OutputStreamWriter(System.out);
        } else {
            try {
            	System.out.println("\n  Converting to XML...");
                generatedOutput = new OutputStreamWriter(new FileOutputStream(
                        mOutputXmlDirectory), "ISO-8859-1");
                System.out.println("\tOutput file " + mOutputXmlDirectory + " created");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        
        boolean namespaceEnabled = true;
        boolean recover = true;
        EDItoXML theObject = new EDItoXML(inputReader, generatedOutput);
        theObject.setNamespaceEnabled(namespaceEnabled);
        theObject.setRecover(recover);
        theObject.run();

        System.out.println("\tTransformation complete");
	}
	
	private void parseXml () {
		System.out.println("  Parsing XML...");
		try {	 
			File outputXmlFile = new File(mOutputXmlDirectory);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			mXmlDocument = dBuilder.parse(outputXmlFile);
			mXmlDocument.getDocumentElement().normalize();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private void generateAndDisplayReport () {
		 
		generateAndDisplayFileInfo();
		generateAndDisplayTransactionInfo();
	}
	
	private void generateAndDisplayFileInfo () {
		System.out.println("\n  EDI File Information\n");
		
		Node senderAddress = mXmlDocument.getElementsByTagName("sender").item(0).getFirstChild();
		Element eSenderAddress = (Element) senderAddress;
		System.out.println("\tSender ID:\t\t" + eSenderAddress.getAttribute("Id"));

		Node receiverAddress = mXmlDocument.getElementsByTagName("receiver").item(0).getFirstChild();
		Element eReceiverAddress = (Element) receiverAddress;
		System.out.println("\tReceiver ID:\t\t" + eReceiverAddress.getAttribute("Id"));

		Node transaction = mXmlDocument.getElementsByTagName("transaction").item(0);
		Element eTransaction = (Element) transaction;
		System.out.println("\tDoc Type:\t\t" + eTransaction.getAttribute("DocType"));
		System.out.println("\tDoc Name:\t\t" + eTransaction.getAttribute("Name"));
	}
	
	private void generateAndDisplayTransactionInfo () {
		
		String planSponsor = null;
		String insurer = null;
		String thirdPartyAdmin = null;
		
		int maintenances = 0;
		
		String memberFirstName = null;
		String memberLastName = null;
		String memberId = null;
		String memberPriorFirstName = null;
		String memberPriorId = null;
		String memberPriorLastName = null;
		
		NodeList loops = mXmlDocument.getElementsByTagName("loop");
		
		for (int i = 0; i < loops.getLength(); i++) {
			Element loop = (Element) loops.item(i);
			switch(loop.getAttribute("Id")) {
				case "1000":
					switch(loop.getFirstChild().getFirstChild().getTextContent()) {
						case "P5":
							planSponsor = loop.getFirstChild().getFirstChild().getNextSibling().getTextContent();
							break;
						case "IN":
							insurer = loop.getFirstChild().getFirstChild().getNextSibling().getTextContent();
							break;
						case "TV":
							thirdPartyAdmin = loop.getFirstChild().getFirstChild().getNextSibling().getTextContent();
							break;
					}
					break;
				case "2000":
					maintenances++;
					break;
				case "2100":
					if (memberFirstName != null && 
							!loop.getElementsByTagName("element").item(3).getTextContent().equals(memberFirstName))
						memberPriorFirstName = loop.getElementsByTagName("element").item(3).getTextContent();
					else 
						memberFirstName = loop.getElementsByTagName("element").item(3).getTextContent();
					if (memberLastName != null && 
							!loop.getElementsByTagName("element").item(2).getTextContent().equals(memberLastName))
						memberPriorLastName = loop.getElementsByTagName("element").item(2).getTextContent();
					else 
						memberLastName = loop.getElementsByTagName("element").item(2).getTextContent();
					if (memberId != null && 
							!loop.getElementsByTagName("element").item(6).getTextContent().equals(memberId))
						memberPriorId = loop.getElementsByTagName("element").item(7).getTextContent();
					else 
						memberId = loop.getElementsByTagName("element").item(6).getTextContent();
					break;
				case "2300": 
					break;
			}
		}
		
		System.out.println("\n  Insurance Information\n");
		System.out.println("\tPlan Sponsor: \t\t" + planSponsor);
		System.out.println("\tInsurer: \t\t" + insurer);
		System.out.println("\tThird Party Admin: \t" + thirdPartyAdmin);
		
		System.out.println("\n  Member Information\n");
		System.out.println("\tMember First Name:\t" + memberFirstName);
		System.out.println("\tMember Last Name:\t" + memberLastName);
		System.out.println("\tMember ID:\t\t" + memberId);

		System.out.println("\tPrior First Name:\t" + memberPriorFirstName);
		System.out.println("\tPrior Last Name:\t" + memberPriorLastName);
		System.out.println("\tPrior Member ID:\t" + memberPriorId);

		System.out.println("\n  Maintenances \n");
		System.out.println("\tTotal Maintenances:\t" + maintenances);

	}
	
}
