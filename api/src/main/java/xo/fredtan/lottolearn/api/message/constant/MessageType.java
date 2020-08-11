package xo.fredtan.lottolearn.api.message.constant;

public enum MessageType {
    CHAT("CHAT"),
    SIGN("SIGN"),
    ELECT("ELECT");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
