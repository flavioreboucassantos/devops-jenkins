import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public abstract class BaseSelenium {

	/**
	 * 
	 * @param webElement
	 * @param duration
	 * @return true if isDisplayed before duration. false if not isDisplayed until duration.
	 */
	static public boolean waitIsDisplayed(final WebElement webElement, final Duration duration) {
		final long limit = System.currentTimeMillis() + duration.toMillis();
		while (System.currentTimeMillis() < limit) {
			try {
				if(webElement.isDisplayed())
					return true;
			} catch (StaleElementReferenceException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
		return false;
	}

	/**
	 * 
	 * @param webElement
	 * @param duration
	 * @return true if not isDisplayed before duration. false if isDisplayed until duration.
	 */
	static public boolean waitIsNotDisplayed(final WebElement webElement, final Duration duration) {
		final long limit = System.currentTimeMillis() + duration.toMillis();
		while (System.currentTimeMillis() < limit) {
			try {
				if(!webElement.isDisplayed())
					return true;
			} catch (StaleElementReferenceException e) {
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
		return false;
	}

	static public String getRandomString() {
		return UUID.randomUUID().toString();
	}

	static public String rndStr() {
		return getRandomString();
	}

	static public void out(final String x) {
		System.out.println(x);
	}

	static public void out(final long x) {
		System.out.println(x);
	}

	static public void out(Object x) {
		System.out.println(x);
	}

}
