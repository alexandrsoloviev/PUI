package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attachments;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import java.util.HashMap;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HoliTest {
    String beginningRoundBalance;
    String endRoundBalance;

    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        Configuration.browser = "chrome";
        Configuration.browserVersion = "117.0";
        Configuration.browserSize = "1920x1080";
        Configuration.remote = "http://localhost:4444/wd/hub";
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", new HashMap<String, Object>() {
            {
                put("enableVNC", true);
                put("enableVideo", true);
            }
        });
        Configuration.browserCapabilities = capabilities;


    }

    public String getVideoStorageUrl() {
        return "http://localhost:4444/video/";
    }


    @Test
    public void betTest() {

        step("Открыть PUI", () -> {
            open("https://operator.holi.solutions/registration/login/?next=/");
        });

        step("Авторизоваться", () -> {
            $x("//input[@name='username']").setValue("a.salauyou@arateg.com");
            $x("//input[@name='password']").setValue("Dev123123123").submit();
        });

        step("Выбрать стол и переключиться на вкладку стола", () -> {
            $x("//div/strong[text()='table-machine']/ancestor::div[@class='item']//button").click();
            switchTo().window(1);
        });

        step("Дождаться начала раунда", () -> {
            $x("//span[@class='sc-ckLdoV hNcwV']").should(visible, Duration.ofMinutes(1));
        });

        step("Дождаться начала раунда и сохранить в переменную стартовый баланс {beginningRoundBalance}", () -> {
            beginningRoundBalance = $x("//div[@class='sc-laZRCg fTPkLS']//p").getText();
        });

        step("Сделать ставку 10$", () -> {
            $x("//div[@class='sc-gsGlKL beXoQa']").click();
            $x("//*[@class='sc-evzXkX fzypgY']").click();
        });

        step("Дождаться начала следующего раунда", () -> {
            $x("//span[@class='sc-ckLdoV hNcwV']").shouldNot(visible, Duration.ofMinutes(1));
            $x("//span[@class='sc-ckLdoV hNcwV']").should(visible, Duration.ofMinutes(1));
        });

        step("Сохранить в переменную баланс после сыгранного раунда {endRoundBalance}", () -> {
            endRoundBalance = $x("//div[@class='sc-laZRCg fTPkLS']//p").getText();
        });

        step("Баланс в начале игры не должен быть равен балансу после сыгранного раунда", () -> {
            assertNotEquals(beginningRoundBalance, endRoundBalance);
        });
    }

    @AfterEach
    void addAttachments() {
        Attachments.screenshotAs("Last screenshot");
        Attachments.pageSource();
        Attachments.browserConsoleLogs();
        Attachments.addVideo(getVideoStorageUrl());
    }
}
