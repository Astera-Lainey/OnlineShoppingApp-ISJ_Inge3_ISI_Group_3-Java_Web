package OnlineShopping.entity;

public enum Category {
    MENS_CLOTHES("Fashion", "Men", "Men's Clothes"),
    WOMENS_CLOTHES("Fashion", "Women", "Women's Clothes"),
    MENS_SHOES("Fashion", "Men", "Men's Shoes"),
    WOMENS_SHOES("Fashion", "Women", "Women's Shoes"),
    MENS_ACCESSORIES("Fashion", "Men", "Men's Accessories"),
    WOMENS_ACCESSORIES("Fashion", "Women", "Women's Accessories"),
    ELECTRONICS("Electronics", null, "Electronics"),
    HOME_FURNITURE("Home", null, "Furniture"),
    TOYS("Toys", null, "Toys"),
    BEAUTY("Beauty", null, "Beauty & Personal Care");

    private final String category;
    private final String gender; // Can be null if not gender-specific
    private final String displayName;

    Category(String category, String gender, String displayName) {
        this.category = category;
        this.gender = gender;
        this.displayName = displayName;
    }

    public String getCategory() {
        return category;
    }

    public String getGender() {
        return gender;
    }

    public String getDisplayName() {
        return displayName;
    }
}

