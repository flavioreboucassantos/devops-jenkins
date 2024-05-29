import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(OrderAnnotation.class)
public class SeleniumControllerArea extends BaseSelenium {

	static public ChromeDriver chromeDriver;
	static public final Duration timeout = Duration.ofSeconds(3);
	static public WebDriverWait webDriverWait;

	static public String uniqueData;

	@BeforeEach
	public void beforeEach() {
		// Abrir o Navegador
		chromeDriver = new ChromeDriver();

		// Configurar Navegador
		chromeDriver.manage().timeouts().implicitlyWait(timeout);

		// Configurar WebDriverWait
		webDriverWait = new WebDriverWait(chromeDriver, timeout);

		// Navegar para o Endereço da API
		chromeDriver.navigate().to("http://localhost:8080/area/");
	}

	@Test
	@DisplayName("createArea-CREATED")
	@Order(1)
	public void createArea_CREATED() {
		try {
			// Clicar no Botão "Criar Área"
			chromeDriver.findElement(By.className("create-area")).click();

			// Escrever no campo "Raw Data"
			chromeDriver.findElement(By.id("rawData")).sendKeys(rndStr());

			// Escrever no campo "Unique Data"
			uniqueData = rndStr();
			chromeDriver.findElement(By.id("uniqueData")).sendKeys(uniqueData);

			// Marcar o campo "Highlighted"
			chromeDriver.findElement(By.id("highlighted")).click();

			// Clicar no Botão de Submit "Criar Área"
			chromeDriver.findElement(By.className("submit-area")).click();

			// Esperar Resposta de Rede
			final WebElement elModal = chromeDriver.findElement(By.className("modal-actions-response"));
			webDriverWait.until(ExpectedConditions.visibilityOf(elModal));

			// Validar Mensagem de Sucesso
			final String textModalLead = elModal.findElement(By.className("lead")).getText();
			assertTrue(textModalLead.toLowerCase().contains("sucesso"));
		} finally {
			// Fechar o Navegador
			chromeDriver.quit();
		}
	}

	@Test
	@DisplayName("createArea-NOT_MODIFIED")
	@Order(2)
	public void createArea_NOT_MODIFIED() {
		try {
			chromeDriver.findElement(By.className("create-area")).click();
			chromeDriver.findElement(By.id("rawData")).sendKeys(rndStr());
			chromeDriver.findElement(By.id("uniqueData")).sendKeys(uniqueData);
			chromeDriver.findElement(By.id("highlighted")).click();
			chromeDriver.findElement(By.className("submit-area")).click();

			final WebElement elModal = chromeDriver.findElement(By.className("modal-actions-response"));
			webDriverWait.until(ExpectedConditions.visibilityOf(elModal));

			final String textModalLead = elModal.findElement(By.className("lead")).getText();
			assertTrue(textModalLead.toLowerCase().contains("error"));

			final String textModalMessage = elModal.findElement(By.className("message")).getText();
			assertTrue(textModalMessage.toLowerCase().contains("not modified"));
		} finally {
			chromeDriver.quit();
		}
	}

	@Test
	@DisplayName("updateByIdArea-OK")
	@Order(3)
	public void updateByIdArea_OK() {
		try {
			chromeDriver.findElement(By.xpath(String.format("//td[text()='%s']", uniqueData))).click();

			webDriverWait.until(ExpectedConditions.elementToBeClickable(By.className("submit-area")));

			chromeDriver.findElement(By.id("rawData")).sendKeys(Keys.CONTROL + "a");
			chromeDriver.findElement(By.id("rawData")).sendKeys(Keys.DELETE);
			chromeDriver.findElement(By.id("highlighted")).click();

			chromeDriver.findElement(By.className("submit-area")).click();

			final WebElement elModal = chromeDriver.findElement(By.className("modal-actions-response"));
			webDriverWait.until(ExpectedConditions.visibilityOf(elModal));

			final String textModalLead = elModal.findElement(By.className("lead")).getText();
			assertTrue(textModalLead.toLowerCase().contains("sucesso"));
		} finally {
			chromeDriver.quit();
		}
	}

	@Test
	@DisplayName("removeArea-OK")
	@Order(4)
	public void removeArea_OK() {
		try {
			WebElement elTr = chromeDriver.findElement(By.xpath(String.format("//tr[td/text()='%s']", uniqueData)));
			elTr.findElement(By.className("delete-area")).click();

			webDriverWait.until(ExpectedConditions.stalenessOf(elTr));
		} finally {
			chromeDriver.quit();
		}
	}

}
