package OnlineShopping.entity;

public enum Category {
    FASHION_MEN("Fashion"),
    FASHION_WOMEN("Fashion"),
    FASHION_CHILDREN("Fashion"),
    ACCESSORIES("Fashion"),
    ELECTRONICS("Electronics"),
    HOME_AND_FURNITURE("Home & Furniture"),
    TOYS("Toys"),
    SPORTS("Sports"),
    SHOES("Shoes");

    private final String group;

    Category(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }
}

