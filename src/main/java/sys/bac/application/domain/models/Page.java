package sys.bac.application.domain.models;

import java.util.List;

public class Page<T> {
    private List<T> result;
    private final int offset;
    private final int size;
    private long totalElements;

    public Page(List<T> result, int offset, int size, long total) {
        this.result = result;
        this.offset = offset;
        this.size = size;
        this.totalElements = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getOffset() {
        return offset;
    } 

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotal(long total) {
        this.totalElements = total;
    }

    public boolean isEmpty() {
        return result.isEmpty();
    }
}
