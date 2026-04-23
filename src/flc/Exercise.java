package flc;

public class Exercise {
    private String name;
    private double price;

    public Exercise(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() { return name; }
}