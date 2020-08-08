package xo.fredtan.lottolearn.api.user.constant;

public enum UserAccountType {
    GITHUB(0),
    WEIBO(1),
    INTERNAL(2);

    private final Integer type;

    UserAccountType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
