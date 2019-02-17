import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

public class TradeData {
    public double sum;
    public int count;

    public void update(ElementsCollection tradeRows) {
        for (int i = 1; i < tradeRows.size(); i++) {
            SelenideElement currentRow = tradeRows.get(i);
            SelenideElement oosNumber = currentRow.$("[aria-describedby='BaseMainContent_MainContent_jqgTrade_OosNumber']");
            // "[aria-describedby]='BaseMainContent_MainContent_jqgTrade_OosNumber'"));

            String oosNumberText = oosNumber.getText();
            if (!oosNumberText.trim().isEmpty()) {
                this.count++;
                String lotState = currentRow.$("[aria-describedby='BaseMainContent_MainContent_jqgTrade_LotStateString']").getText();
                if (!lotState.trim().equals("Отменена")) {
                    String priceText = currentRow.$("[aria-describedby='BaseMainContent_MainContent_jqgTrade_StartPrice']").getText();
                    double lotPrice = parseLotPrice(priceText);
                    this.sum += lotPrice;
                }
            }
        }
    }

    private static double parseLotPrice(String priceText) {
        return Double.parseDouble(priceText.replace(" руб.", "")
                .replace("EUR", "")
                .replaceAll(" ", "")
                .replace(',', '.'));
    }
}
