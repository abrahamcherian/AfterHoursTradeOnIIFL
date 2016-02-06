
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
//import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
//-----------------------------------------------------------------------------------------------------------------------
// FILES EXPORTED TO JAR WILL HAVE VERSION NUMBER ASSOCIATED AT THE END
//-----------------------------------------------------------------------------------------------------------------------
//V7 - Supports --ignore-certificate-errors to ignore unwanted errors. 
//V6- SUPPORTS FASTER SEARCH WHEN READING LEDGER BALANCE BY CHANGING THE RIGHT CLASS PATH ON THE LEDGER BALANCE TABLE.
//V5- SUPPORTS CHROME BROWSER AND SUPPORT FOR FIREFOX BROWSER IS REMOVED. INCLUDED A TIMEOUT IN LOGIN FUNCTION TO AUTCLOSE THE INITIAL POPOVER
//-----------------------------------------------------------------------------------------------------------------------
public class IIFL_AH {
	
	 static WebDriver _driver = new ChromeDriver();
//	static WebDriver _driver = new FirefoxDriver();
	static int intInterval = 3500;
	static Date now = new Date();
	@SuppressWarnings("deprecation")
	static int StDate = now.getDay();
	
	//Start of MAIN Function ----------------------------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public static void main(String args[]) throws InterruptedException{
//		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Aby\\Downloads\\selenium-2.41.0\\ChromeDriver\\chromedriver.exe");
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Aby\\Downloads\\selenium-2.41.0\\ChromeDriver\\chromedriver.exe");
		// To remove message "You are using an unsupported command-line flag: --ignore-certificate-errors.
	    // Stability and security will suffer."
	    // Add an argument 'test-type'
	    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
	    ChromeOptions options = new ChromeOptions();
	    options.addArguments("test-type");
	    capabilities.setCapability("chrome.binary","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
	    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
	    
	    // Closing first open driver
	    _driver.close();
	    
	    _driver.quit();
	    
	    //opening a new chrome driver with capabilities to remove unsupported version --ignore-certificate-errors.
	    _driver = new ChromeDriver(capabilities);
		
		Properties prop = new Properties();
		 System.out.println("-------------------------------------------------"+ now.toGMTString() +"-------------------------------------------------");
    	try {
               //load a properties file
    		prop.load(new FileInputStream("C:\\Users\\Aby\\workspace\\IIFL_AH.properties"));
    		//Login
    		fnLogin(prop.getProperty("iiflurl"), prop.getProperty("iiflusername"),prop.getProperty("iiflpassword") , prop.getProperty("iiflPAN") );
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    
    	//Fetching the minimum balance to be maintained.
		float minBal = Float.parseFloat(    prop.getProperty("minimumBalance")    ); 	
		
		System.out.println("!! IMP !!:The minimum balance of "+ minBal + " will be maintained in account. !!");
		
		Thread.sleep(intInterval);
		
		//Reading Margin values
		float getMargin = fnReadMargin();
		
		if(getMargin <= minBal)
		{
			System.out.println("InSufficient Balance. Ledger Balance = " + getMargin);
			fnLogout();
			return;
		}
		else
		{
			System.out.println("Sufficient Balance is available. Ledger Balance = " + getMargin);
//			return;
		}
    	
		//reading the inputCSV files
    	String csvFile = "C:\\Users\\Aby\\workspace\\IIFL_AH_DATA.csv";
    	BufferedReader br = null;
    	String line = "";
    	String cvsSplitBy = ",";
     
    	try {
    		br = new BufferedReader(new FileReader(csvFile));
    		while ((line = br.readLine()) != null) {
    		    // use comma as separator
    			String[] DETAILS = line.split(cvsSplitBy);
//    			System.out.println(line);
    	//		System.out.println("DETAILS [1= " + DETAILS[0] + " , 2=" + DETAILS[1] + " , 3=" + DETAILS[2] + " , 4=" + DETAILS[3] + " , 5=" + DETAILS[4] + " , 6=" + DETAILS[5] +"]");
    	//fnPlaceOrderFor condition
    			if(DETAILS[0].toLowerCase().equals("fnplaceorderfor")){
    				String sStock = DETAILS[1];
    				Float sAmt = Float.valueOf(DETAILS[2]);
    				Float sAdditional =  Float.valueOf(DETAILS[3]);
    				String sPercentorDollar = DETAILS[4];
    				fnPlaceOrderFor(sStock,sAmt,sAdditional,sPercentorDollar);
    			}

    	//fnPlaceWeeklyVariable condition
    			if(DETAILS[0].toLowerCase().equals("fnplaceweeklyvariable")){
    				String sStock = DETAILS[1];
    				Float sAmt = Float.valueOf(DETAILS[2]);
    				Float sAdditional =  Float.valueOf(DETAILS[3]);
    				String sPercentorDollar = DETAILS[4];
    			 	//checking day condition
    				if(StDate ==   Float.valueOf(DETAILS[5]))
    				{		
    					fnPlaceOrderFor(sStock,sAmt,sAdditional,sPercentorDollar);
    				}
    				else
    				{
    					System.out.println("Cannot place order today for "+ sStock);
    				}
    			}
    	//fnPlaceOrder condition
    			if(DETAILS[0].toLowerCase().equals("fnplaceorder")){
    				if(StDate ==   Float.valueOf(DETAILS[3])){		fnPlaceOrder(DETAILS[1],DETAILS[2]);		}
    			}
    		}	//End of While
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
		// Daily trade
//		fnPlaceOrderFor("SAIL",500 , 10, "$");  // Steel Authority of India Limited
//		fnPlaceOrderFor("NHPC",500 , 10, "$");  // NHPC
//		fnPlaceOrderFor("SOUTHBANK",100, 10 ,"$");	//South Indian Bank
//		fnPlaceOrderFor("SCI",200,10,"%");		// Shipping Corporation of India
//		if(StDate ==  1){		fnPlaceOrder("GOLDBEES","1");		}
//		if(StDate ==  2){		fnPlaceOrder("COALINDIA","1");		}
//		if(StDate ==  4){		fnPlaceOrder("HINDALCO","1");		}
//		if(StDate ==  5){		fnPlaceOrder("NIFTYBEES","1");  }//Purchase Niftybees every Monday only so placing AH order on Sunday
		
		//Download the latest portfolio details
//		fnDownloadPortfolio();
		
		fnLogout();
	}
	// END of Main function -----------------------------------------------------------------------------------------------
	
	public static void fnPlaceOrderFor(String strSecurity, float fAmount, float fvariance,String cVarianceDetails) throws InterruptedException{
//		cVarianceDetails  - % for percentage and $ for price
	
		int iInterval = 3500;
		float percentDown = (float) 0.2;
//		//clicking on the Dashboard link
		Thread.sleep(2000);
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='Header1_TTheader']/div/div[1]/ul/li[3]/a")) , "Dashboard", "Link", "");
		Thread.sleep(iInterval);
		
//		//clicking on AFter hours link
		WorkOnObject(_driver.findElement(By.xpath("//a[@href='Cashordersc.aspx?RequestFrom=A&BuySell=B']")) , "After Market Hours - Buy", "Link", "");
		Thread.sleep(iInterval);
		
		//Selecting Type as Cash
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='ddlMktType']")) , "Type", "Weblist", "CASH");
//		Thread.sleep(iInterval);
		
		//Selecting Market
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='radNseCash']")) , "Market", "WebRadio", "Nse");
//		Thread.sleep(iInterval);
		
		//Selecting  Order type as Regular Lot
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='ddlOrderTypeCash']")) , "Order Type","Weblist", "Regular Lot");
//		Thread.sleep(iInterval);
		
		//Enter Scrip
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='txtSymbolCash']")) , "Scrip","WebTextEdit", strSecurity);
//		Thread.sleep(iInterval);
		//Pressing the enter key after entering the stock symbol to invoke the 
		_driver.findElement(By.xpath("//*[@id='txtSymbolCash']")).sendKeys(Keys.TAB);
		Thread.sleep(iInterval + 2000);

		//Fetching the new Last Trade PRICE
		String newLTP= new String();
		newLTP="0";
		
		//Fetching Last Trade Price
		if(_driver.findElement(By.xpath("//*[@id='spnVerMwCurPrc']")).isDisplayed()){
			DecimalFormat  dp = new DecimalFormat("#.#");
			
				Float LTP = Float.parseFloat(_driver.findElement(By.xpath("//*[@id='spnVerMwCurPrc']")).getText());
				LTP = (float) (LTP * ((100 - percentDown )/100)) ;		
				//Converting back LTP to string format
				LTP = Float.valueOf(dp.format(LTP));
				newLTP = Float.toString(LTP);
				
				System.out.println("Trade price entered is " + newLTP);
		}
		else
		{
			System.out.println("ERROR !! Could not read the stock data");
		}
		
		//Proceed to place the order only if newLTP is greater than 0
		if(Float.parseFloat(newLTP) > 0){
			//Enter Quantity
			float lowerLimit =fAmount; 
			float upperLimit =0; 
			//Setting Variance parameters
			if(cVarianceDetails.equals("$")){ 		upperLimit = lowerLimit +  fvariance;		}
			if(cVarianceDetails.equals("%")){		upperLimit = lowerLimit +  (lowerLimit * (fvariance/100));	}
			
			System.out.println("Lower Limit :" + lowerLimit + " and Upper Limit :" + upperLimit);
			System.out.println("Price of the security is " + newLTP);
			
			//Fetching lower quantity and upper quantity
			int lowerqty = (int) (lowerLimit / Float.parseFloat(newLTP));
			int upperqty = (int) (upperLimit / Float.parseFloat(newLTP));
			
			System.out.println("Lower Quantity: " + lowerqty + " Upper Quanity is :"+ upperqty);
			
			if(lowerqty > upperqty){
				System.out.println("Lower quantity is selected " + lowerqty);
				WorkOnObject(_driver.findElement(By.xpath("//*[@id='txtTotalQtyCash']")) , "Quantity","WebTextEdit", "" + lowerqty);
			}
			else
			{
				System.out.println("Upper quantity is selected " + upperqty);
				WorkOnObject(_driver.findElement(By.xpath("//*[@id='txtTotalQtyCash']")) , "Quantity","WebTextEdit", "" + upperqty);
			}
			// Entering the Price, clicking on Place and Send button only if the quantity is > 0
			if(lowerqty > 0 || upperqty > 0){
			//Enter Trade Price
			WorkOnObject(_driver.findElement(By.xpath("//*[@id='txtLimtPriceCash']")) , "Price","WebTextEdit", newLTP);
			Thread.sleep(iInterval);
					
			//Clicking on the Place button
			WorkOnObject(_driver.findElement(By.xpath("//*[@id='btnplace']")) , "Place","button", "");
			Thread.sleep(iInterval);
	
			//clicking on the send button
			WorkOnObject(_driver.findElement(By.xpath(".//*[@id='btnSend']")) , "Send","button", "");
			Thread.sleep(iInterval);
			}
		}// end of newLTP > 0 Check
			
	}

	//Function to read the ledger balance and report to user
	public static float fnReadMargin()throws InterruptedException{
		
		//clicking on the Dashboard link
		WorkOnObject(_driver.findElement(By.linkText("Dashboard")) , "Dashboard", "Link", "");
		Thread.sleep(2000);
				
		//Clicking on Portfolio link
		WorkOnObject(_driver.findElement(By.linkText("Margin")) , "Margin", "Link", "");
		Thread.sleep(5000);
		
		//Reading the Ledger Balance available
		System.out.println("Starting the read margin.");
		
		//Creating a list of web elements with tag name td
		List<WebElement> linkElements = _driver.findElements(By.xpath("//*[@id='tblMarg']/tbody/tr[2]/td"));
		
		//Reporting the number of td elements
		System.out.println("The number of Td elements are " +linkElements.size());
		
        int i = 0;
        int getno=0;
        String linkTexts[] =  new String[linkElements.size()] ;
        
        //extract the link texts of each link element
        for (WebElement e : linkElements) {
            linkTexts[i] = e.getText();
            //System.out.println("Reading " + linkTexts[i]);
            if(linkTexts[i].trim().contains("Ledger Balance")){
            	 getno= i+1;
            }
            i++;
        }
       	return Float.parseFloat(linkTexts[getno]);
	}
	
	//Download Detailed portfolio in xls format
	public static void fnDownloadPortfolio()throws InterruptedException{
		
		//clicking on the Dashboard link
		WorkOnObject(_driver.findElement(By.linkText("Dashboard")) , "Dashboard", "Link", "");
		Thread.sleep(intInterval);
				
		//Clicking on Portfolio link
		WorkOnObject(_driver.findElement(By.linkText("Portfolio")) , "Portfolio", "Link", "");
		Thread.sleep(intInterval);
		
		//Selecting the Cash under Product
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='ddlBusiness']")) , "Cash", "Weblist", "Cash");
		//Thread.sleep(intInterval);
		
		//Entering the From Date
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='DtFrm_dateInput_text']")) , "From Date","WebTextEdit", "01/01/2012");
//		
		//Tapping on Show button
		WorkOnObject(_driver.findElement(By.xpath(".//*[@id='btnpltracker']")) , "Show","button", "");
		Thread.sleep(intInterval+intInterval);
		
		//clicking on Download Detail PL
		WorkOnObject(_driver.findElement(By.xpath(".//*[@id='btnptDetailedexcel']")) , "Download Detailed Excel","Link", "");
		Thread.sleep(intInterval);
		
	}
	
	//Login
	public static void fnLogin(String strURL,String strUsername, String strPassword, String strPan) throws InterruptedException{
		//Opening IIFL trade site
				_driver.get(strURL);
				
				//Thread.sleep(2000);
				_driver.manage().window().maximize();
				
				//Waiting for any opened popup's to close automatically
				//Thread.sleep(10000);
				WorkOnObject(_driver.findElement(By.xpath("//*[@id='content']/div[4]/div[2]/div/div/div[1]/a/img")) , "Login","Button","" );
				
				//Entering the username
				WorkOnObject(_driver.findElement(By.name("txtLogin")) , "Login", "webtextedit", strUsername);
				
				//Entering password
				WorkOnObject(_driver.findElement(By.name("txtPassword")) , "Password", "webtextedit", strPassword);
				
				//Entering PAN card number
				WorkOnObject(_driver.findElement(By.name("txtPanDob")) , "PAN or DOB", "webtextedit", strPan);

//				//Selecting Product type to Equity
//				WorkOnObject(_driver.findElement(By.name("rdoProduct")) , "Product", "Weblist", "Equity");
				
				//clicking on Submit button to login
				WorkOnObject(_driver.findElement(By.name("btnLogin")) , "Submit", "Button", "");
				
				Thread.sleep(5000);
	}

	//Place Order
	public static void fnPlaceOrder(String strSecurity, String strQuantity) throws InterruptedException{
		
		int iInterval = 5000;
		float percentDown = (float) 0.3;
//		//clicking on the Dashboard link
		WorkOnObject(_driver.findElement(By.linkText("Dashboard")) , "Dashboard", "Link", "");
		Thread.sleep(iInterval);
		
//		//clicking on AFter hours link
		WorkOnObject(_driver.findElement(By.xpath("//a[@href='Cashordersc.aspx?RequestFrom=A&BuySell=B']")) , "After Market Hours - Buy", "Link", "");
		Thread.sleep(iInterval);
		
		//Selecting Type as Cash
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='ddlMktType']")) , "Type", "Weblist", "CASH");
//		Thread.sleep(iInterval);
		
		//Selecting Market
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='radNseCash']")) , "Market", "WebRadio", "Nse");
//		Thread.sleep(iInterval);
		
		//Selecting  Order type as Regular Lot
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='ddlOrderTypeCash']")) , "Order Type","Weblist", "Regular Lot");
//		Thread.sleep(iInterval);
		
		//Enter Scrip
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='txtSymbolCash']")) , "Scrip","WebTextEdit", strSecurity);
//		Thread.sleep(iInterval);
		//Pressing the enter key after entering the stock symbol to invoke the 
		_driver.findElement(By.xpath("//*[@id='txtSymbolCash']")).sendKeys(Keys.TAB);
		Thread.sleep(iInterval);
		
		//Enter Quantity
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='txtTotalQtyCash']")) , "Quantity","WebTextEdit", strQuantity);
//		Thread.sleep(intInterval);

		String newLTP= new String();
		newLTP="0";
		//Fetching Last Trade Price
		if(_driver.findElement(By.xpath("//*[@id='spnVerMwCurPrc']")).isDisplayed()){
			DecimalFormat  dp = new DecimalFormat("#.#");
			
				Float LTP = Float.parseFloat(_driver.findElement(By.xpath("//*[@id='spnVerMwCurPrc']")).getText());
				LTP = (float) (LTP * ((100 - percentDown)/100)) ;		
				//Converting back LTP to string format
				LTP = Float.valueOf(dp.format(LTP));
				newLTP = Float.toString(LTP);
				
				System.out.println("Trade price entered is " + newLTP);
		}
		else
		{
			System.out.println("ERROR !! Could not read the stock data");
		}
		
		//Enter Trade Price
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='txtLimtPriceCash']")) , "Price","WebTextEdit", newLTP);
		Thread.sleep(iInterval);
		
				
		//Clicking on the Place button
		WorkOnObject(_driver.findElement(By.xpath("//*[@id='btnplace']")) , "Place","button", "");
		Thread.sleep(iInterval);

		//clicking on the send button
		WorkOnObject(_driver.findElement(By.xpath(".//*[@id='btnSend']")) , "Send","button", "");
		Thread.sleep(iInterval);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//Function to work on any web object
	public static void WorkOnObject(WebElement obj , String objName, String objTyp, String objData){
		
		// create a java calendar instance
		Calendar calendar = Calendar.getInstance();
		
		//Fetching the current time
		java.util.Date now = calendar.getTime();
		
		//Formatting the timestamp
		String myTimestamp = "[" + now.toString() + "] - ";
		
		//changing the Objtype string to lower case
		objTyp = objTyp.toLowerCase();
//		System.out.println(objTyp);
		
		//Checking if the object is displayed on the webpage
		if(((WebElement) obj).isDisplayed())
		{
			if(objTyp.equals("webtextedit")){
				//CLICK ON THE TEXT BOX
				obj.click();
				//checking if the data text is not null. Write the text on the text box only if null
				if(objData.trim().length() !=0 ){	
					for(int myloop=0; myloop<15;myloop++){
						obj.sendKeys(Keys.BACK_SPACE);
					}
					obj.sendKeys(objData); 
					System.out.println(myTimestamp + " Text " + objData + " is entered in the Text field " + objName);
				}
			}
			//Link code
			else if(objTyp.equals("link")){
				//CLICK ON THE TEXT BOX
				obj.click();
				System.out.println(myTimestamp + " " + objName + " link is clicked.");
			}
			//Button code
			else if(objTyp.equals("button")){
				//CLICK ON THE TEXT BOX
				obj.click();
				System.out.println(myTimestamp + " " + objName + " button is clicked.");
			}
			// Select Item from a Web List
			else if(objTyp.equals("weblist")){
				Select productMenu = new Select(obj);
				String allMenuObjects = obj.getText();
				if(allMenuObjects.contains(objData)){
					productMenu.selectByVisibleText(objData);
					System.out.println(myTimestamp + " Item '" + objData + "' is available and selected in the Weblist-" + objName);
				}
				else{
					System.out.println(myTimestamp + " Item '" + objData + "' is not available in the Weblist-" + objName);
				}
				productMenu = null;
				
			}
		}
		else
		{
			System.out.println(myTimestamp +"The Object " + objName + " is not displayed on the webpage");
		}
	}		//END of Function WorkonObject
	//---------------------------------------------------------------------------------------------------------------------------------------
	//Public function to synchronize the response of UAT with objects.
	public static void SynchronizeTillObject(String syncObj) throws InterruptedException{
		Thread.sleep(1000);
		System.out.println("Waiting...");
		while(true){
			if(  _driver.findElement(By.xpath(syncObj)).isDisplayed()  == false){
				System.out.println("Waiting for synchornization");
				Thread.sleep(1000);
			}
			else{
				break;
			}
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------------
	//Logout Function
	public static void fnLogout() throws InterruptedException{
		//clicking on the Logout
		WorkOnObject(_driver.findElement(By.linkText("Logout")) , "Logout", "Link", "");
		Thread.sleep(intInterval);
//								
		//closing the browser
		_driver.close();
		
		//Quitting the Firefox Driver
		_driver.quit();	
			
	}

}  // End of IIFL_SAIL  class
	//---------------------------------------------------------------------------------------------------------------------------------------

