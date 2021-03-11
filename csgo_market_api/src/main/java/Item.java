public class Item implements Comparable<Item> {
    private String name;
    private Double price;

    public Item(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "name = " + name + ", price = " + price;
    }

    @Override
    public int compareTo(Item o) {
        return price.compareTo(o.getPrice());
    }
}
