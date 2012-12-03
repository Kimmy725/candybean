package com.sugarcrm.voodoo.automation.control;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.sugarcrm.voodoo.automation.IInterface;
import com.sugarcrm.voodoo.automation.VInterface;
import com.sugarcrm.voodoo.automation.Voodoo;
import com.sugarcrm.voodoo.automation.control.VHook.Strategy;
import com.sugarcrm.voodoo.utilities.Utils;


/**
 * @author cwarmbold
 */
public class VControl implements IControl {
	
	protected final Voodoo voodoo;
	protected final VInterface iface;
	protected final VHook hook;

	public VControl(Voodoo voodoo, VInterface iface, Strategy strategy, String hook) throws Exception {
		this(voodoo, iface, new VHook(strategy, hook));
	}
	
	public VControl(Voodoo voodoo, VInterface iface, VHook hook) throws Exception {
		this.voodoo = voodoo;
		this.iface = iface;
		this.hook = hook;
	}
	
	@Override
	public String getAttribute(String attribute) throws Exception {
		voodoo.log.info("Selenium: getting attribute: " + attribute	+ " for control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		String value = we.getAttribute(attribute);
		if (value == null) throw new Exception("Selenium: attribute does not exist.");
		else return value;
	}

	@Override
	public String getText() throws Exception {
		voodoo.log.info("Selenium: getting text for control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		return we.getText();
	}

	@Override
	public void click() throws Exception {
		voodoo.log.info("Selenium: clicking on control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		we.click();
	}

	@Override
	public void doubleClick() throws Exception {
		voodoo.log.info("Selenium: double-clicking on control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		Actions action = new Actions(this.iface.wd);
		action.doubleClick(we).perform();
	}

	@Override
	public void dragNDrop(VControl dropControl)	throws Exception {
		voodoo.log.info("Selenium: dragging control: " + this.toString() + " to control: " + dropControl.toString());
		WebElement dragWE = this.iface.wd.findElement(this.getBy(this.hook));
		WebElement dropWE = this.iface.wd.findElement(this.getBy(dropControl.hook));
		Actions action = new Actions(this.iface.wd);
		action.dragAndDrop(dragWE, dropWE).build().perform();
	}

	@Override
	public void hover() throws Exception {
		voodoo.log.info("Selenium: hovering over control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		Actions action = new Actions(this.iface.wd);
		action.moveToElement(we).perform();
	}

	@Override
	public void rightClick() throws Exception {
		voodoo.log.info("Selenium: right-clicking control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		Actions action = new Actions(this.iface.wd);
		action.contextClick(we).perform();
	}

	@Override
	public void scroll() throws Exception {
		voodoo.log.info("Selenium: scrolling to control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		int y = we.getLocation().y;
		((JavascriptExecutor) this.iface.wd).executeScript("window.scrollBy(0," + y + ");");
	}

	@Override
	public void sendString(String input) throws Exception {
		voodoo.log.info("Selenium: sending string: " + input + " to control: " + this.toString());
		WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		we.clear();
		we.sendKeys(input);
	}

	@Override
	public void waitOn() throws Exception {
		voodoo.log.info("Selenium: waiting on visibility of control: " + this.toString());
		final WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		long explicitWait = Long.parseLong(Utils.getCascadingPropertyValue(
				voodoo.props, "12000", "perf.explicit_wait"));
		WebDriverWait wait = new WebDriverWait(this.iface.wd, explicitWait);
		wait.until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return we.isDisplayed();
			}
		});
	}

	@Override
	public void wait(String attribute, String value) throws Exception {
		voodoo.log.info("Waiting for control: " + this.toString()
				+ " to have attribute: " + attribute + " to have value: " + value);
		final WebElement we = this.iface.wd.findElement(this.getBy(this.hook));
		final String vAttribute = attribute;
		final String vValue = value;
		long explicitWait = Long.parseLong(Utils.getCascadingPropertyValue(voodoo.props, "12000", "perf.explicit_wait"));
		WebDriverWait wait = new WebDriverWait(this.iface.wd, explicitWait);
		wait.until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return we.getAttribute(vAttribute).contains(vValue);
			}
		});
	}
	
	@Override
	public String toString() {
		return "VControl(" + this.hook.toString() + ")";
	}
	
	protected By getBy(VHook hook) throws Exception {
		return this.getBy(hook.hookStrategy, hook.hookString);
	}
	
	protected By getBy(Strategy strategy, String hook) throws Exception {
		switch (strategy) {
		case CSS:
			return By.cssSelector(hook);
		case XPATH:
			return By.xpath(hook);
		case ID:
			return By.id(hook);
		case NAME:
			return By.name(hook);
		case LINK:
			return By.linkText(hook);
		case PLINK:
			return By.partialLinkText(hook);
		default:
			throw new Exception("Selenium: strategy type not recognized.");
		}
	}
}
