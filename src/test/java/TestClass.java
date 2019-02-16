import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;


public class TestClass {

    @Test
    public void Test1() {

        open("https://www.pochta.ru");
        $(".input--search input").setValue("RI750632171CN");
        $(".input__btn-search").click();
        $(".tracking-card__history-status").shouldHave(text("Получено адресатом"));

    }

    public TestClass() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\nasgr\\IdeaProjects\\SeleniumDrivers\\chromedriver_win32\\chromedriver.exe");
    }
}
