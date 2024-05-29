import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public class SeleniumGridControllerArea extends BaseSeleniumControllerArea {

	public final Duration timeout = Duration.ofSeconds(4);

	public final String strUrlRemoteWebDriver = "http://localhost:4444";

	@BeforeEach
	public void beforeEach() throws MalformedURLException {
		final ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setCapability("browserName", "chrome");
//		chromeOptions.setCapability("browserVersion", "do not use");
		chromeOptions.setCapability("platformName", "Windows");
		chromeOptions.setCapability("se:name", "Selenium Grid For Controller Area");
		chromeOptions.setCapability("se:workingClass", "SeleniumGridControllerArea");

		webDriver = new RemoteWebDriver(URI.create(strUrlRemoteWebDriver).toURL(), chromeOptions);
		webDriver.manage().timeouts().implicitlyWait(timeout);
		webDriverWait = new WebDriverWait(webDriver, timeout);
		webDriver.navigate().to("http://localhost:8080/area/");
	}

}
