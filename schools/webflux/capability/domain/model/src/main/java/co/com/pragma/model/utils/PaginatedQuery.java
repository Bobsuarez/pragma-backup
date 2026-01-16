package co.com.pragma.model.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PaginatedQuery<f> {
    private Integer page;
    private Integer size;
    private f filters;

    public int offset() {
        int p = getPageOrDefault();
        int s = getSizeOrDefault();
        return (p - 1) * s;
    }

    public int getPageOrDefault() {
        return page == null || page < 1 ? 1 : page;
    }

    public int getSizeOrDefault() {
        return size == null || size < 1 ? 10 : size;
    }

    public long calculateTotalPages(long totalRecords) {
        int s = getSizeOrDefault();
        return (long) Math.ceil(totalRecords / (double) s);
    }


}
