package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RedBusCalendarTest {

    private WebDriver driver;

    //Locators
    private static final By OPEN_CALENDAR_BUTTON = By.cssSelector("#onwardCal");
    private static final By CALENDAR_WIDGET = By.cssSelector(".sc-jzJRlG.dPBSOp");
    private static final By LEFT_ARROW = By.cssSelector(".DayNavigator__IconBlock-qj8jdz-2.iZpveD:nth-child(1)");
    private static final By RIGHT_ARROW = By.cssSelector(".DayNavigator__IconBlock-qj8jdz-2.iZpveD:nth-child(3)");
    private static final By MONTH_YEAR = By.cssSelector(".DayNavigator__IconBlock-qj8jdz-2.iZpveD:nth-child(2)");
    private static final By HOLIDAY_COUNT = By.cssSelector(".DayNavigator__IconBlock-qj8jdz-2.iZpveD:nth-child(2)>div");
    private static final By WEEKEND_DAYS = By.cssSelector(".DayTiles__CalendarDaysSpan-sc-1xum02u-1.bwoYtA,.DayTiles__CalendarDaysSpan-sc-1xum02u-1.fgdqFw");


    @BeforeMethod
    public void setup() {

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBrowserVersion("stable");
        chromeOptions.addArguments("--disable-notifications");
        driver = new ChromeDriver(chromeOptions);
        driver.get("https://www.redbus.in/");
        driver.manage().window().maximize();
        openCalendar();
    }

    @Test
    public void printHolidays1() {
        navigateCalendar("Aug 2024");
    }

    @Test
    public void printHolidays2() {
        navigateCalendar("Mar 2025");
    }

    @Test
    public void printHolidays3() {
        navigateCalendar("Jan 2024");
    }

    @Test
    public void printHolidays4() {
        navigateCalendar("Mar 2024");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("-----------------------------------------------------\n");
        driver.quit();
    }

    public void explicitWaitMethod(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private void openCalendar() {

        if (!isElementDisplayed(CALENDAR_WIDGET)) {
            explicitWaitMethod(OPEN_CALENDAR_BUTTON);
            driver.findElement(OPEN_CALENDAR_BUTTON).click();
            explicitWaitMethod(CALENDAR_WIDGET);
        }
    }

    private String getMonthYear() {
        explicitWaitMethod(MONTH_YEAR);
        return driver.findElement(MONTH_YEAR).getText().trim().split("\n")[0];
    }

    private String getHolidayCount() {
        if (isElementDisplayed(HOLIDAY_COUNT)) {
            explicitWaitMethod(HOLIDAY_COUNT);
            return driver.findElement(HOLIDAY_COUNT).getText().trim();
        }
        return "NO Holidays :( ";
    }

    private boolean isElementDisplayed(By by) {

        try {
            driver.findElement(by).isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void navigateCalendar(String period) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        YearMonth anotherYearMonth = YearMonth.parse(period, formatter);

        if (anotherYearMonth.isBefore(getCurrentMonthYear())) {
            System.out.println("Can not run this test for past dates");
            return;
        }

        if (period.equals(getMonthYear())) {
            printHolidayAndMonthYear();
            System.out.println(getWeekends());
            return;
        }

        do {
            printHolidayAndMonthYear();
            driver.findElement(RIGHT_ARROW).click();
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        } while (!period.equals(getMonthYear()));
        System.out.println(getWeekends());
    }


    private YearMonth getCurrentMonthYear() {
        LocalDate currentDate = LocalDate.now();
        return YearMonth.from(currentDate);
    }

    private void printHolidayAndMonthYear() {
        System.out.println("MONTH YEAR :: " + getMonthYear());
        System.out.println("HOLIDAY COUNT :: " + getHolidayCount());
    }

    private List<String> getWeekends() {

        List<String> weekends = driver.findElements(WEEKEND_DAYS)
                .stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());

        return weekends;
    }

}
