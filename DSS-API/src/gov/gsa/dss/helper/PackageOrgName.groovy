package gov.gsa.dss.helper

import com.silanis.esl.sdk.DocumentPackage
import com.silanis.esl.sdk.DocumentPackageAttributes
import com.silanis.esl.sdk.EslClient
import gov.gsa.dss.helper.staic.OrgCodes;
import javax.jms.Session
import org.apache.chemistry.opencmis.client.api.SessionFactory
import org.apache.commons.lang3.StringUtils

class PackageOrgName {

	def getOrgName(DocumentPackage documentPackage) {
		DocumentPackageAttributes documentPackageAttributes = documentPackage.getAttributes();
		def orgName = documentPackageAttributes.getContents().get("orgName").toString();
		def orgList = OrgCodes.getOrgList();

		if ((StringUtils.isEmpty(orgName)) || (orgName == "null")) {
			for (int i = 0; i < orgList.size(); i++) {
				if (documentPackage.getName().contains(orgList[i])) {
					orgName=orgList[i];
				}
			}
		}
		return orgName
	}
}
