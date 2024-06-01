import java.io.Serializable;

public class SearchCriteria implements Serializable {
    private String area;
    private String dates;
    private int numberOfGuests;
    private double maxPrice;
    private int minStars;

    // Constructor
    public SearchCriteria(String area, String dates, int numberOfGuests, double maxPrice, int minStars) {
        this.area = area;
        this.dates = dates;
        this.numberOfGuests = numberOfGuests;
        this.maxPrice = maxPrice;
        this.minStars = minStars;
    }

    // Getters and Setters
    public String getArea() {
        return area;
    }

    public String getDates() {
        return dates;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public int getMinStars() {
        return minStars;
    }
}

