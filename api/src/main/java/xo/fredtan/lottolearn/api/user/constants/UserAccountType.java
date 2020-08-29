package xo.fredtan.lottolearn.api.user.constants;

public enum UserAccountType {
    GITHUB(0),
    WEIBO(1),
    PASSWORD(2);

    private final Integer type;

    UserAccountType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
