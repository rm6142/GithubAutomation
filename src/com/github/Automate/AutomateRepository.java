package com.github.Automate;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AutomateRepository {

	WebDriver driver;
	private static String titleName;
	private static String description;
	@BeforeClass
    public void WebDriverManagerTest() throws IOException
    {
        //setup the chromedriver using WebDriverManager
		String path = System.getProperty("user.dir");
		FileInputStream fis = new FileInputStream(path+"\\config.properties");
		// create object for Properties class
		Properties prop = new Properties();
		prop.load(fis);
		String driverpath = path + "\\Resources\\drivers\\chromedriver.exe";
        //Create driver object for Chrome
		System.setProperty("webdriver.chrome.driver",driverpath );
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        String username=prop.getProperty("username");
        String password=prop.getProperty("password");
        String url=prop.getProperty("url");
        //Navigate to a URL
        driver.get(url);
        //Enter Username
      	driver.findElement(By.xpath("//input[@name='login'] ")).sendKeys(username);
      	//Enter password
      	driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
        //Clicking on Sign in
      	driver.findElement(By.xpath("//input[@value='Sign in']")).click();
    }
	
	//Challenge 1
	@Test
    public void CreateRepository()
    {
		//Clicking on new Repository
		driver.findElement(By.xpath("//h2[contains(text(),' Re')]/a[1] | (//div/a[contains(text(),'Create repository') and @href='/new'])[1]")).click();
		String RepoName="Repo"+Calendar.getInstance().getTimeInMillis();
		//Set Repository Name
		driver.findElement(By.xpath("//dd/input[@id='repository_name']")).sendKeys(RepoName);
		WebElement createRepo=driver.findElement(By.xpath("//button[@type='submit' and contains(text(),'Create')]"));
		//Waiting for button to enable
		WebDriverWait wait=new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(createRepo));
		//clicking on repo
		createRepo.click();
		String reponame=driver.findElement(By.xpath("//h1/strong[@itemprop='name']/a")).getText();
		Assert.assertEquals(reponame, RepoName);
    }
	
	//Challenge 2
	@Test(dependsOnMethods = {"CreateRepository"})
    public void CreateIssue()
    {
		//Click on issue tab
		WebDriverWait wait=new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@data-tab-item='issues-tab']/parent::li")));
		WebElement tabIssue=driver.findElement(By.xpath("//a[@data-tab-item='issues-tab']/parent::li"));
		tabIssue.click();
		//click on New issue button
		WebElement newIssue=driver.findElement(By.xpath("//a/span[contains(text(),'New issue')]"));
		newIssue.click();
		//set titlename
		titleName="Issue is"+Calendar.getInstance().getTimeInMillis();
		WebElement inputTitleName=driver.findElement(By.xpath("//input[@placeholder='Title']"));
		inputTitleName.sendKeys(titleName);
		//set description 
		description="I am creating a issue";
		WebElement inputDescription=driver.findElement(By.tagName("textarea"));
		inputDescription.sendKeys(description);
		//click on submit new
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='btn btn-primary' and contains(text(),'Submit new') ]")));
		WebElement submitNewIssue=driver.findElement(By.xpath("//button[@class='btn btn-primary' and contains(text(),'Submit new') ]"));
		submitNewIssue.click();
		//Creating Another Issue with mention description and title 
		//click on New Issue
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/following-sibling::a[contains(text(),'New issue')]")));
		driver.findElement(By.xpath("//button/following-sibling::a[contains(text(),'New issue')]")).click();
		String newTitleName="Creating Another issue with previous description and Title";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Title']")));
		driver.findElement(By.xpath("//input[@placeholder='Title']")).sendKeys(newTitleName);
		inputDescription=driver.findElement(By.tagName("textarea"));
		inputDescription.sendKeys(titleName+" "+description+" fix this");
		submitNewIssue=driver.findElement(By.xpath("//button[@class='btn btn-primary' and contains(text(),'Submit new') ]"));
		submitNewIssue.click();
		driver.manage().timeouts().implicitlyWait(10 ,TimeUnit.SECONDS);
		
    }
	
	//Challenge 3
	@Test(dependsOnMethods = {"CreateRepository","CreateIssue"})
	public void commentToIssue() {
		//Set comment to issue 2 created in challenge 2
		WebElement inputCommentBox=driver.findElement(By.xpath("//textarea[@name='comment[body]']"));
		String commentToIssue="Resolve Issue 2";
		inputCommentBox.sendKeys(commentToIssue);
		//Clicking Comment
		driver.findElement(By.xpath("//button[contains(text(),'Comment')]")).click();
		driver.navigate().refresh();
		driver.manage().timeouts().implicitlyWait(5 ,TimeUnit.SECONDS);
		
	}
	
	//Challenge 4
	@Test(dependsOnMethods = {"CreateRepository","CreateIssue","commentToIssue"})
	public void IssueMentioninCommentsLinktoIssue() {
		WebElement issueHeader=driver.findElement(By.xpath("//h1[contains(@class,'gh-header')]"));
		String issueName=issueHeader.getAttribute("innerText");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement btnClose=driver.findElement(By.xpath("//button[@name='comment_and_close']"));
		js.executeScript("arguments[0].scrollIntoView();", btnClose);
		WebElement inputCommentBox=driver.findElement(By.xpath("//textarea[@name='comment[body]']"));
		inputCommentBox.sendKeys(issueName);
		WebElement btnComment=driver.findElement(By.xpath("//button[contains(text(),'Comment')]"));
		btnComment.click();
		WebElement issueCreated=driver.findElement(By.xpath("//table//p[contains(text(),'"+issueName.substring(0, issueName.length()-3)+"')]/a"));
		issueCreated.click();
		driver.manage().timeouts().implicitlyWait(2 ,TimeUnit.SECONDS);
	}
	
	//Challenge 5
	@Test(dependsOnMethods = {"CreateRepository","CreateIssue","commentToIssue","IssueMentioninCommentsLinktoIssue"})
	public void DeleteRepository() {
		WebDriverWait wait=new WebDriverWait(driver, 10);
		driver.findElement(By.xpath("//a[@data-tab-item='settings-tab']")).click();
		WebElement btnDeleteRepo=driver.findElement(By.xpath("//details/summary[contains(text(),'Delete this ')]"));
		wait.until(ExpectedConditions.visibilityOf(btnDeleteRepo));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView();", btnDeleteRepo);
		btnDeleteRepo.click();
		driver.manage().timeouts().implicitlyWait(2 ,TimeUnit.SECONDS);
		String strRepoName=driver.getCurrentUrl().replaceAll("https://github.com/","").replaceAll("/settings", "");
		WebElement inputConfirmation=driver.findElement(By.xpath("//input[contains(@aria-label,'Type in the name of the repository to confirm that you want to delete this repository.') ]"));
		wait.until(ExpectedConditions.visibilityOf(inputConfirmation));
		inputConfirmation.sendKeys(strRepoName);
		WebElement btnSubmitConfirmation=driver.findElement(By.xpath("//button[contains(text(),'I understand the consequences, delete this repository')]"));
		btnSubmitConfirmation.click();
		
	}
	
}
