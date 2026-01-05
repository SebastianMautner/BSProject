package sys.bac.application.domain.models;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if( this == o) return true;
        if(!(o instanceof Page)) return false;
        @SuppressWarnings("unchecked")
        Page<T> page = (Page<T>) o;
        return Objects.equals(this.result, page.result) &&
        this.offset == page.offset &&
        this.size == page.size &&
        this.totalElements == page.totalElements;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, offset, size, totalElements);
    }
}
