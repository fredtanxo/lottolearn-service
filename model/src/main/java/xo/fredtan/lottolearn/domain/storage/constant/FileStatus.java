package xo.fredtan.lottolearn.domain.storage.constant;

public enum FileStatus {
    NOT_PROCESSED(0),
    PROCESSING(1),
    PROCESS_SUCCESS(2),
    PROCESS_FAIL(3),
    NO_NEED_PROCESS(4);

    private Integer type;

    FileStatus(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }
}
