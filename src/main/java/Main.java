import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    private static String PROXY_HOST = "";
    private static Integer PROXY_PORT = 0;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        Bot bot = null;
        HttpHost httpHost = new HttpHost(PROXY_HOST, PROXY_PORT);
        if (httpHost == null) {
            bot = new Bot();
        } else {
            DefaultBotOptions instance = ApiContext
                    .getInstance(DefaultBotOptions.class);
            RequestConfig rc = RequestConfig.custom()
                    .setProxy(httpHost).build();
            instance.setRequestConfig(rc);
            bot = new Bot(instance);
        }

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException ex) {
            ex.printStackTrace();
        }
    }
}

