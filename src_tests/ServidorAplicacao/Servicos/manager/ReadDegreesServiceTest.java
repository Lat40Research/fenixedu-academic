/*
 * Created on 23/Jul/2003
 */
package ServidorAplicacao.Servicos.manager;

/**
 * @author lmac1
 */

public class ReadDegreesServiceTest extends TestCaseManagerReadServices {
	    
	/**
	 * @param testName
	 */
	 public ReadDegreesServiceTest(String testName) {
		super(testName);
	 }

	 protected String getNameOfServiceToBeTested() {
		return "ReadDegrees";
	 }

	protected int getNumberOfItemsToRetrieve() {
		return 5;
	}
}