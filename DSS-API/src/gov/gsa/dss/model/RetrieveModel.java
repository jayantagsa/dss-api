package gov.gsa.dss.model;

public class RetrieveModel {
	protected String strPackageId;
	protected String strBase64Zip;
	protected String strDocName;
	
	public RetrieveModel( )
  {
	  
  }
	
	public String getJSONString(String strPackageId ,String strBase64Zip,String strDocName)
	{
		this.strPackageId =strPackageId;
		this.strBase64Zip =strBase64Zip;
		this.strDocName = strDocName;
		return  "{\"Package\":{\"id\":\""+strPackageId+"\",\"Name\":\""+strDocName+"\", \"Content\": \""+strBase64Zip+"\"}}";
		
	}
	
}
