import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.io.FileWriter;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.System.err;
import static java.lang.System.out;

public class DataMining {

    private static final int startingDateArgument = 0;
    private static final int endDateArgument = 1;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\nasgr\\IdeaProjects\\SeleniumDrivers\\chromedriver_win32\\chromedriver.exe");

        Configuration.timeout = 10000;

        if (args.length < 2) {
            err.println("Укажите в качестве аргументов начальную и конечную дату публикации извещения " +
                    "пример: program \"20.01.2018\" \"30.01.2018\"");

            return;
        }

        String startingDate = args[startingDateArgument];
        String endDate = args[endDateArgument];

        searchForTrades(startingDate, endDate);

        TradeData tradeData = new TradeData();

        SelenideElement nextPageButton = $("#next_t_BaseMainContent_MainContent_jqgTrade_toppager");

        SelenideElement currentPageInput = $(".ui-pg-input");

        int currentPage = 1;

        do {
            ElementsCollection tradeRows = $$("#BaseMainContent_MainContent_jqgTrade tr");

            tradeData.update(tradeRows);

            if (isButtonClickable(nextPageButton)) {
                nextPageButton.click();

                currentPage++;

                currentPageInput.shouldHave(value(Integer.toString(currentPage)));
            }
        } while (isButtonClickable(nextPageButton));

        String result = "Сумма всех закупок: " + tradeData.sum + " Кол-во закупок: " + tradeData.count;

        out.println(result);
        writeResultToFile(result);
    }

    private static void writeResultToFile(String result) {
        try (FileWriter writer = new FileWriter("result.txt", false)) {
            writer.write(result);

            writer.flush();
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }

    private static boolean isButtonClickable(SelenideElement nextPageButton) {
        return !nextPageButton.has(cssClass("ui-state-disabled"));
    }

    /**
     * переходит по адресу устанавливает зрачения нажимает кнопку и ждет загрузки таблицы
     */
    private static void searchForTrades(String startingDate, String endDate) {
        open("https://223.rts-tender.ru/supplier/auction/Trade/Search.aspx");
        $("#BaseMainContent_MainContent_txtPublicationDate_txtDateFrom").setValue(startingDate);
        $("#BaseMainContent_MainContent_txtPublicationDate_txtDateTo").setValue(endDate);

        $("#BaseMainContent_MainContent_chkPurchaseType_1").setSelected(true);
        $("#BaseMainContent_MainContent_chkPurchaseType_0").setSelected(true);
        $("#BaseMainContent_MainContent_txtStartPrice_txtRangeFrom").setValue("0");
        $("#BaseMainContent_MainContent_btnSearch").click();

        $(".ui-paging-info").shouldNotBe(empty);
    }


}
