import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// @Execution(value = ExecutionMode.CONCURRENT) // despises @TestMethodOrder(OrderAnnotation.class)
@TestMethodOrder(OrderAnnotation.class)
public abstract class BaseSeleniumControllerArea extends BaseSelenium {

	static private final String uniqueData = rndStr(); // needs to be static because it generates a new instance by @Test

	protected WebDriver webDriver;
	protected WebDriverWait webDriverWait;

	@Test
	@DisplayName("createArea-CREATED")
	@Order(1)
	protected final void createArea_CREATED() {
		try {
			// Clicar no Botão "Criar Área"
			webDriver.findElement(By.className("create-area")).click();

			// Escrever no campo "Raw Data"
			webDriver.findElement(By.id("rawData")).sendKeys(rndStr());

			// Escrever no campo "Unique Data"
			webDriver.findElement(By.id("uniqueData")).sendKeys(uniqueData);

			// Marcar o campo "Highlighted"
			webDriver.findElement(By.id("highlighted")).click();

			// Clicar no Botão de Submit "Criar Área"
			webDriver.findElement(By.className("submit-area")).click();

			// Esperar Resposta de Rede
			final WebElement elModal = webDriver.findElement(By.className("modal-actions-response"));
			webDriverWait.until(ExpectedConditions.visibilityOf(elModal));
//			assertTrue(waitIsDisplayed(elModal));

			// Validar Mensagem de Sucesso
			final String textModalLead = elModal.findElement(By.className("lead")).getText();
			assertTrue(textModalLead.toLowerCase().contains("sucesso"));
		} finally {
			// Fechar o Navegador
			webDriver.quit();
		}
	}

	@Test
	@DisplayName("createArea-NOT_MODIFIED")
	@Order(2)
	protected final void createArea_NOT_MODIFIED() {
		try {
			webDriver.findElement(By.className("create-area")).click();
			webDriver.findElement(By.id("rawData")).sendKeys(rndStr());
			webDriver.findElement(By.id("uniqueData")).sendKeys(uniqueData);
			webDriver.findElement(By.id("highlighted")).click();
			webDriver.findElement(By.className("submit-area")).click();

			final WebElement elModal = webDriver.findElement(By.className("modal-actions-response"));
			webDriverWait.until(ExpectedConditions.visibilityOf(elModal));
//			assertTrue(waitIsDisplayed(elModal));

			final String textModalLead = elModal.findElement(By.className("lead")).getText();
			assertTrue(textModalLead.toLowerCase().contains("error"));

			final String textModalMessage = elModal.findElement(By.className("message")).getText();
			assertTrue(textModalMessage.toLowerCase().contains("not modified"));
		} finally {
			webDriver.quit();
		}
	}

	@Test
	@DisplayName("updateByIdArea-OK")
	@Order(2)
	protected final void updateByIdArea_OK() {
		try {
			webDriver.findElement(By.xpath(String.format("//td[text()='%s']", uniqueData))).click();

			webDriverWait.until(ExpectedConditions.elementToBeClickable(By.className("submit-area")));

			webDriver.findElement(By.id("rawData")).sendKeys(Keys.CONTROL + "a");
			webDriver.findElement(By.id("rawData")).sendKeys(Keys.DELETE);
			webDriver.findElement(By.id("highlighted")).click();

			webDriver.findElement(By.className("submit-area")).click();

			final WebElement elModal = webDriver.findElement(By.className("modal-actions-response"));
			webDriverWait.until(ExpectedConditions.visibilityOf(elModal));
//			assertTrue(waitIsDisplayed(elModal));

			final String textModalLead = elModal.findElement(By.className("lead")).getText();
			assertTrue(textModalLead.toLowerCase().contains("sucesso"));
		} finally {
			webDriver.quit();
		}
	}

	@Test
	@DisplayName("removeArea-OK")
	@Order(3)
	protected final void removeArea_OK() {
		try {
			WebElement elTr = webDriver.findElement(By.xpath(String.format("//tr[td/text()='%s']", uniqueData)));
			elTr.findElement(By.className("delete-area")).click();

			webDriverWait.until(ExpectedConditions.stalenessOf(elTr));
//			assertTrue(waitIsNotDisplayed(elTr));
		} finally {
			webDriver.quit();
		}
	}
}
