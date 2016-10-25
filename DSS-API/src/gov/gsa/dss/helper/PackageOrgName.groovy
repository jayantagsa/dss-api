package gov.gsa.dss.helper

import com.silanis.esl.sdk.DocumentPackage
import com.silanis.esl.sdk.DocumentPackageAttributes
import com.silanis.esl.sdk.EslClient
import gov.gsa.dss.helper.staic.OrgCodes;
import org.apache.commons.lang3.StringUtils

class PackageOrgName {

	def getOrgName(DocumentPackage documentPackage) {
		
		DocumentPackageAttributes documentPackageAttributes = documentPackage.getAttributes();
		def orgName = documentPackageAttributes.getContents().get("orgName").toString();
		if ((StringUtils.isEmpty(orgName)) || (orgName == "null")) {
			if (documentPackage.getName().contains('ACP')) {
				orgName="IACP";
			}
		}
		return orgName
	}
}
