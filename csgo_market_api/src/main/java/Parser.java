import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {
    public static List<Item> parseCSGOMarket(InputStream inputStream) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(
                    new InputStreamReader(inputStream));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        JSONArray array = (JSONArray) jsonObject.get("items");

        Iterator iterator = array.iterator();

        List<Item> result = new ArrayList<>();
        while (iterator.hasNext()) {
            JSONObject current = (JSONObject) iterator.next();
            if (Integer.parseInt(current.get("volume").toString()) > 0) {
                result.add(new Item(
                        current.get("market_hash_name").toString(),
                        Double.parseDouble(current.get("price").toString()))
                );
            }

        }

        return result;
    }

    public static List<Item> parseLootFarm(InputStream inputStream) {
        JSONParser jsonParser = new JSONParser();
        JSONArray array = null;
        try {
            array = (JSONArray) jsonParser.parse(
                    new InputStreamReader(inputStream));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        Iterator iterator = array.iterator();

        List<Item> result = new ArrayList<>();
        while (iterator.hasNext()) {
            JSONObject current = (JSONObject) iterator.next();
            if (Integer.parseInt(current.get("res").toString()) != 0) {
                result.add(new Item(
                        current.get("name").toString(),
                        (double) Integer.parseInt(current.get("price").toString()) / 100)
                );
            }
        }

        return result;
    }
}
