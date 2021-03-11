import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Request request = new Request("https://market.csgo.com/api/v2/prices/USD.json");

        try {
            List<Item> marketItems = request.doRequest(Parser::parseCSGOMarket);

            request.setUrl("https://loot.farm/fullprice.json");
            List<Item> lootFarmItems = request.doRequest(Parser::parseLootFarm).stream().filter((el) -> el.getPrice() > 10 && el.getPrice() < 250).collect(Collectors.toList());
            Map<String, Double> mapLootFarm= lootFarmItems.stream().collect(Collectors.toMap(Item::getName, Item::getPrice));
            List<Item> diffItems = new ArrayList<>();
            for (Item el: marketItems) {
                Double currentPrice = mapLootFarm.get(el.getName());
                if (currentPrice != null) {
                    diffItems.add(new Item(el.getName(), el.getPrice() - currentPrice));
                }
            }

            diffItems = diffItems.stream()
                    .filter(el ->
                            !el.getName().contains("Sticker") &&
                                    !el.getName().contains("StatTrak™")
                                    && !el.getName().contains("Souvenir"))
                    .collect(Collectors.toList());
            diffItems.sort(Collections.reverseOrder());
            Iterator iterator = diffItems.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }

/*
            List<Double> diffItems = DoubleStream.of(IntStream.range(0, marketItems.size())
                    .mapToDouble(i -> marketItems.get(i).getPrice() - steamItems.get(i).getPrice())
                    .toArray()).boxed().collect(Collectors.toList());
            Collections.sort(diffItems);
            Collections.reverse(diffItems);

            for (Double el : diffItems) {
                System.out.println(el);
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
