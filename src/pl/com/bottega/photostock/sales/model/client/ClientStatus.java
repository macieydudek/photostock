package pl.com.bottega.photostock.sales.model.client;

public enum ClientStatus {
    STANDARD("Standardowy"),
    VIP("VIP"),
    GOLD("Złoty"),
    SILVER("Srebrny"),
    PLATINUM("Platynowy");


    private String statusName;

    ClientStatus(String name) {
        this.statusName = name;
    }

    public String getStatusName() {
        return statusName;
    }
}
