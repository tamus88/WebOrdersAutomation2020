package com.weborders.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.weborders.utilities.BrowserUtilities;
import com.weborders.utilities.ConfigurationReader;
import com.weborders.utilities.Driver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.IOException;

public class AbstractBaseTest {

    protected WebDriver driver = Driver.getDriver();
    protected static ExtentReports extentReports;
    protected static ExtentHtmlReporter extentHtmlReporter;
    private static ExtentTest extentTest;

   @BeforeTest
   public void beforeTest(){
    extentReports = new ExtentReports();
       String reportPath = "";
       //location of report file
       if (System.getProperty("os.name").toLowerCase().contains("win")) {
           reportPath = System.getProperty("user.dir") + "\\test-output\\report.html";
       } else {
           reportPath = System.getProperty("user.dir") + "/test-output/report.html";
       }
        extentHtmlReporter = new ExtentHtmlReporter(reportPath);
       extentReports.attachReporter(extentHtmlReporter);
        extentHtmlReporter.config().setReportName("WebOrders Automation");
   }

   @AfterTest
   public void afterTest(){
    extentReports.flush();
   }
    @BeforeMethod
    public void setUp(){
       driver.get(ConfigurationReader.getProperty("url"));
        driver.manage().window().maximize();

    }
    @AfterMethod
    public void tearDown(ITestResult testResult){
        if(testResult.getStatus()== ITestResult.FAILURE) {
            String screenShotLocation = BrowserUtilities.getScreenshot(testResult.getName());

            try {
                extentTest.fail(testResult.getName());//test name that failed
                extentTest.addScreenCaptureFromPath(screenShotLocation);//screenshot as an evidence
                extentTest.fail(testResult.getThrowable());//error message
            }catch (IOException e){
                e.printStackTrace();
           throw new RuntimeException("Failed to attach screenshot");
            }
        }else if(testResult.getStatus() == ITestResult.SUCCESS){
            extentTest.pass(testResult.getName());
        }else if(testResult.getStatus() == ITestResult.SKIP){
            extentTest.skip(testResult.getName());
        }
        BrowserUtilities.wait(3);
        Driver.closeDriver();
    }
}
