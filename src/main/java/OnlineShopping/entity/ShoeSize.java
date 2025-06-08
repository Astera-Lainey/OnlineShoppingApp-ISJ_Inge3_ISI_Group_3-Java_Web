package OnlineShopping.entity;

public enum ShoeSize {
    SIZE_16("16"),
    SIZE_17("17"),
    SIZE_18("18"),
    SIZE_19("19"),
    SIZE_20("20"),
    SIZE_21("21"),
    SIZE_22("22"),
    SIZE_23("23"),
    SIZE_24("24"),
    SIZE_25("25"),
    SIZE_26("26"),
    SIZE_27("27"),
    SIZE_28("28"),
    SIZE_29("29"),
    SIZE_30("30"),
    SIZE_31("31"),
    SIZE_32("32"),
    SIZE_33("33"),
    SIZE_34("34"),
    SIZE_35("35"),
    SIZE_36("36"),
    SIZE_37("37"),
    SIZE_38("38"),
    SIZE_39("39"),
    SIZE_40("40"),
    SIZE_41("41"),
    SIZE_42("42"),
    SIZE_43("43"),
    SIZE_44("44"),
    SIZE_45("45"),
    SIZE_46("46"),
    SIZE_47("47"),
    SIZE_48("48"),
    SIZE_49("49"),
    SIZE_50("50");

    private final String display;

    ShoeSize(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
