package gov.gsa.dss.helper

import com.silanis.esl.sdk.DocumentId
import com.silanis.esl.sdk.DocumentPackage
import com.silanis.esl.sdk.EslClient
import com.silanis.esl.sdk.PackageId
import com.silanis.esl.sdk.Signature
import com.silanis.esl.sdk.builder.SignatureBuilder;


class PackageOperations {
	def signerSubstitute (PackageId packageId, 
							int numOfAttachments/*Number of attachments that the package has*/, 
							DocumentPackage originalPackage/*DocumentPackage before applying the layout*/) {
		Authenticator auth = new Authenticator();
		EslClient dssEslClient = auth.getAuth();
		DocumentPackage createdPackageAfterLayout = dssEslClient.getPackage(packageId);
		List<com.silanis.esl.sdk.Document> packageDocumentsAfterLayout= dssEslClient.getPackage(packageId).getDocuments();

		for (int x = numOfAttachments+1; x < packageDocumentsAfterLayout.size(); x++) {

			DocumentId currentDocId = createdPackageAfterLayout.getDocuments().get(x).getId(); //grab document
			Collection mysigs = createdPackageAfterLayout.getDocuments().get(x).getSignatures(); //get all signatures
			List sigsToMove = new ArrayList(); //create a new List to store all signatures in for "update".
			Signature mynewsig = null; //create signature object to use to create new signatures to transfer from signer3 to signer1.
			for(Signature sig : mysigs){ //walk through signatures
				String email = sig.getSignerEmail();
				if ((email != null) && (email.startsWith("dsssignerph"))){ //check signature email against known temporary email
					Collection myfields = sig.getFields(); //grab fields from signature
					def repeatedEmail = email.substring(11);//grab email id which comes after the word 'dsssignerph'
					def toBeDeletedEmail = email;//grab the signer which has a temporary email signer
					mynewsig = SignatureBuilder.signatureFor(repeatedEmail) //create new signature for signer1 and transfer all values
							.atPosition(sig.getX(), sig.getY())
							.withSize(sig.getWidth(), sig.getHeight())
							.withId(sig.getId())
							.onPage(sig.getPage())
							.build();
					mynewsig.addFields(myfields); //add all fields from old signature to new signature
					sigsToMove.add(mynewsig); //add new signature to signature list
					dssEslClient.getPackageService().removeSigner(packageId, originalPackage.getSigner(toBeDeletedEmail).getId()); //remove the temporary signer

				}
				else{
					sigsToMove.add(sig);//add unchanged signature to signature list
				}
			}
			originalPackage = dssEslClient.getPackage(packageId); //get updated package
			dssEslClient.getApprovalService().updateSignatures(originalPackage, currentDocId.getId(), sigsToMove); //update all signatures for document
		}
	}

}
