import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class SeleniumControllerArea extends BaseSeleniumControllerArea {

	public final Duration timeout = Duration.ofSeconds(4);

	@BeforeEach
	public void beforeEach() {
		// Abrir o Navegador
		webDriver = new ChromeDriver();

		// Configurar Navegador
		webDriver.manage().timeouts().implicitlyWait(timeout);

		// Configurar WebDriverWait
		webDriverWait = new WebDriverWait(webDriver, timeout);

//		setTimeout(timeout);

		// Navegar para o Endereço da API
		webDriver.navigate().to("http://localhost:8080/area/");
	}

}
