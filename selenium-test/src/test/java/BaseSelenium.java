import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public abstract class BaseSelenium {

	static private Duration timeout = Duration.ofSeconds(15);

	public static final Duration getTimeout() {
		return timeout;
	}

	public static final void setTimeout(Duration timeout) {
		BaseSelenium.timeout = timeout;
	}

	/**
	 * Path to use assertTrue.
	 * @param el
	 * @param timeout
	 * @return true if isDisplayed before timeout. false if not isDisplayed until timeout.
	 */
	static public final boolean waitIsDisplayed(final WebElement el, final Duration timeout) {
		final long limit = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < limit) {
			try {
				if (el.isDisplayed())
					return true;
			} catch (StaleElementReferenceException e) {
			}
			Thread.yield();
		}
		return false;
	}

	/**
	 * Path to use assertTrue.
	 * <br>
	 * timeout default is 15 seconds.
	 * @param el
	 * @return true if isDisplayed before timeout. false if not isDisplayed until timeout.
	 */
	static public final boolean waitIsDisplayed(final WebElement el) {
		return waitIsDisplayed(el, timeout);
	}

	/**
	 * Path to use assertTrue.
	 * @param el
	 * @param timeout
	 * @return true if not isDisplayed or is not attached to the DOM before timeout. false if isDisplayed until timeout.
	 */
	static public final boolean waitIsNotDisplayed(final WebElement el, final Duration timeout) {
		final long limit = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < limit) {
			try {
				if (!el.isDisplayed())
					return true;
			} catch (StaleElementReferenceException e) {
				return true;
			}
			Thread.yield();
		}
		return false;
	}

	/**
	 * Path to use assertTrue.
	 * <br>
	 * timeout default is 15 seconds.
	 * @param el
	 * @return true if not isDisplayed or is not attached to the DOM before timeout. false if isDisplayed until timeout.
	 */
	static public final boolean waitIsNotDisplayed(final WebElement el) {
		return waitIsNotDisplayed(el, timeout);
	}

	static public final String getRandomString() {
		return UUID.randomUUID().toString();
	}

	static public final String rndStr() {
		return getRandomString();
	}

	static public final void out(final String x) {
		System.out.println(x);
	}

	static public final void out(final long x) {
		System.out.println(x);
	}

	static public final void out(Object x) {
		System.out.println(x);
	}

}
