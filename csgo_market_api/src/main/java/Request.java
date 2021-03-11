import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class Request {
    private String url;
    private CloseableHttpClient httpClient;

    public Request(String url) {
        this.url = url;
        this.httpClient = HttpClients.createDefault();
    }

    public List<Item> doRequest(Function<InputStream, List<Item>> function) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(
                url);
        request.addHeader("accept", "application/json");

        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        return function.apply(inputStream);
    }

    public List<Item> getSteamItems(List<Item> items) throws ParseException, IOException, URISyntaxException {
        List<Item> result = new ArrayList<>();

        Iterator iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();
            result.add(doRequest(item.getName()));
        }

        return result;
    }

    private Item doRequest(String name) throws URISyntaxException, IOException, ParseException {
        httpClient = HttpClients.createDefault();
        URIBuilder builder = new URIBuilder(url);
        builder
                .setParameter("currency", "1")
                .setParameter("appid", "730")
                .setParameter("market_hash_name", name);

        HttpGet httpGet = new HttpGet(builder.build());

        HttpResponse response = httpClient.execute(httpGet);
        InputStream inputStream = response.getEntity().getContent();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        httpClient.close();
        Object lowestPrice = jsonObject.get("lowest_price");
        return new Item(name, jsonObject == null ?
                Double.MAX_VALUE :
                (jsonObject.get("lowest_price") == null ?
                        Double.MAX_VALUE :
                        Double.parseDouble(jsonObject.get("lowest_price").toString().substring(1))
                )
        );
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
