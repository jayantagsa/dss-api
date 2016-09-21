package gov.gsa.dss.helper.staic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
 
public class OrgCodes
{
 
  private static String props;
 
  static
  {
    //props = new Properties();
    try
    {
      OrgCodes util = new OrgCodes();
      BufferedReader reader = new BufferedReader(new InputStreamReader(util.getPropertiesFromClasspath("orgcode.json")));
      StringBuilder out = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
    	  //System.out.println(line);
          out.append(line);
          //break;
          
      }
      props = out.toString();
      //System.out.println("ssGJASJDJASDJGH"+out.toString()+"");   //Prints the string content read from input stream
      reader.close();
      //props = util.getPropertiesFromClasspath("errorcodes.properties");
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
 
  // private constructor
  private OrgCodes()
  {
  }
 
  public static String getOrg(String key) 
  {
	  try{
	  JSONObject obj = new JSONObject(props);
	 // System.out.println(obj.getString(key)+"\tnnnnnnnnnnnnnn");
	  ;
	  return obj.getString(key);
	  }
	  catch(JSONException e)
	  {
		  e.printStackTrace();
		  return null;
	  }
    
  }
 

 
 
 
  /**
   * loads properties file from classpath
   *
   * @param propFileName
   * @return
   * @throws IOException
   */
  private InputStream getPropertiesFromClasspath(String propFileName)
                                                                    throws IOException
  {
    //Properties props = new Properties();
    InputStream inputStream = null;
    
    try
    {
      inputStream =
          this.getClass().getClassLoader().getResourceAsStream(propFileName);
 
      if (inputStream == null)
      {
        throw new FileNotFoundException("property file '" + propFileName
            + "' not found in the classpath");
      }
      else {
    	  return inputStream;
      }
      //props.load(inputStream);
    }
    finally
    {
      //inputStream.close();
    }
    
  }
}