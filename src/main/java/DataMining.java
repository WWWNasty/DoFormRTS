import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.System.out;

public class DataMining {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\nasgr\\IdeaProjects\\SeleniumDrivers\\chromedriver_win32\\chromedriver.exe");


        searchForTrades();
        //Thread.sleep(4000); шуд би нажал на кнопку след страница1 следующая стр 1+1

        double sumOfPrices = 0;

        SelenideElement nextPageButton = $("#next_t_BaseMainContent_MainContent_jqgTrade_toppager");

        SelenideElement currentPageInput = $(".ui-pg-input");

        int currentPage = 1;

        do {
            ElementsCollection tradeRows = $$("#BaseMainContent_MainContent_jqgTrade tr");

            sumOfPrices += getSumOfPrices(tradeRows);


            //TODO исправить если страниц всего одна

            if (isButtonClickable(nextPageButton)) {
                nextPageButton.click();

                currentPage++;

                currentPageInput.shouldHave(value(Integer.toString(currentPage)));
            }
        } while (isButtonClickable(nextPageButton));

        out.println(sumOfPrices);


        //$(".tracking-card__history-status").shouldHave(text("Получено адресатом"));

    }

    private static boolean isButtonClickable(SelenideElement nextPageButton) {
        return !nextPageButton.has(cssClass("ui-state-disabled"));
    }

    /**
     * переходит по адресу устанавливает зрачения нажимает кнопку и ждет загрузки таблицы
     */
    private static void searchForTrades() {
        open("https://223.rts-tender.ru/supplier/auction/Trade/Search.aspx");
        $("#BaseMainContent_MainContent_txtPublicationDate_txtDateFrom").setValue("01.01.2019");
        $("#BaseMainContent_MainContent_txtPublicationDate_txtDateTo").setValue("01.01.2019");

        $("#BaseMainContent_MainContent_chkPurchaseType_1").setSelected(true);
        $("#BaseMainContent_MainContent_chkPurchaseType_0").setSelected(true);
        $("#BaseMainContent_MainContent_txtStartPrice_txtRangeFrom").setValue("0");
        $("#BaseMainContent_MainContent_btnSearch").click();

        $(".ui-paging-info").shouldNotBe(empty);
    }

    private static double getSumOfPrices(ElementsCollection tradeRows) {
        double sumOfPrices = 0;

        for (int i = 1; i < tradeRows.size(); i++) {
            SelenideElement currentRow = tradeRows.get(i);
            SelenideElement oosNumber = currentRow.$("[aria-describedby='BaseMainContent_MainContent_jqgTrade_OosNumber']");
            // "[aria-describedby]='BaseMainContent_MainContent_jqgTrade_OosNumber'"));

            String oosNumberText = oosNumber.getText();
            if (!oosNumberText.trim().isEmpty()) {
                String lotState = currentRow.$("[aria-describedby='BaseMainContent_MainContent_jqgTrade_LotStateString']").getText();
                if (!lotState.trim().equals("Отменена")) {
                    String priceText = currentRow.$("[aria-describedby='BaseMainContent_MainContent_jqgTrade_StartPrice']").getText();
                    double lotPrice = parseLotPrice(priceText);
                    sumOfPrices += lotPrice;
                }
            }
        }
        return sumOfPrices;
    }

    private static double parseLotPrice(String priceText) {
        return Double.parseDouble(priceText.replace(" руб.", "")
                .replace("EUR", "")
                .replaceAll(" ", "")
                .replace(',', '.'));
    }

}
