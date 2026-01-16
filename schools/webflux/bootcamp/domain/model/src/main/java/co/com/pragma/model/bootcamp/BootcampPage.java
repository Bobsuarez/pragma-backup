package co.com.pragma.model.bootcamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class BootcampPage {

    private final List<BootcampList> content;

    private final Long totalElements;

    private int currentPage;

    private int totalPages;

    public BootcampPage withItems(List<BootcampList> newContent) {
        return new BootcampPage(newContent, this.totalElements, this.totalPages, this.currentPage);
    }
}

