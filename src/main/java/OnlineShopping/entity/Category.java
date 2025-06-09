package OnlineShopping.entity;

public enum Category {
    FASHION_MEN("Fashion", "Men's Fashion"),
    FASHION_WOMEN("Fashion", "Women's Fashion"),
    FASHION_CHILDREN("Fashion", "Children's Fashion"),
    ACCESSORIES("Fashion", "Accessories"),
    ELECTRONICS("Electronics", "Electronics"),
    HOME_AND_FURNITURE("Home & Furniture", "Home & Furniture"),
    TOYS("Toys", "Toys"),
    SPORTS("Sports", "Sports"),
    SHOES("Shoes", "Shoes");

    private final String group;
    private final String displayName;

    Category(String group, String displayName) {
        this.group = group;
        this.displayName = displayName;
    }

    public String getGroup() {
        return group;
    }

    public String getDisplayName() {
        return displayName;
    }
}

