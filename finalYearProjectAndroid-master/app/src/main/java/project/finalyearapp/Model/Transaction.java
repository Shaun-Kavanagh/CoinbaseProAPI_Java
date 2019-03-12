package project.finalyearapp.Model;

public class Transaction {
    private String Amount;
    private String Currency;
    private String Receiver;
    private String ShopA;
    private String ShopB;
    private Boolean custflag;
    private Boolean shopAflag;
    private Boolean shopBflag;
    private String Key;
    private String Creator;

    public Transaction() {
    }

    public Transaction(String amount, String currency, String receiver, String shopA, String shopB, Boolean custFlag, Boolean shopAFlag, Boolean shopBFlag, String key, String creator) {
        Amount = amount;
        Currency = currency;
        Receiver = receiver;
        ShopA = shopA;
        ShopB = shopB;
        custflag = custFlag;
        shopAflag = shopAFlag;
        shopBflag = shopBFlag;
        Key = key;
        Creator = creator;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getShopA() {
        return ShopA;
    }

    public void setShopA(String shopA) {
        ShopA = shopA;
    }

    public String getShopB() {
        return ShopB;
    }

    public void setShopB(String shopB) {
        ShopB = shopB;
    }

    public Boolean getCustflag() {
        return custflag;
    }

    public void setCustflag(Boolean custflag) {
        this.custflag = custflag;
    }

    public Boolean getShopAflag() {
        return shopAflag;
    }

    public void setShopAflag(Boolean shopAflag) {
        this.shopAflag = shopAflag;
    }

    public Boolean getShopBflag() {
        return shopBflag;
    }

    public void setShopBflag(Boolean shopBflag) {
        this.shopBflag = shopBflag;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }
}
