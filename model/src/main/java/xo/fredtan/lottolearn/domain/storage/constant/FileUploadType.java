package xo.fredtan.lottolearn.domain.storage.constant;

public enum FileUploadType {
    MEDIA(0),
    REGULAR(1);

    private Integer type;

    FileUploadType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }
}
