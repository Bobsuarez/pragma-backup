package co.com.pragma.model.capablity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class CapabilityPage {

    private final List<Capability> items;
    private final Long totalElements;
    private int totalPages;
    private int currentPage;

    public CapabilityPage withItems(List<Capability> newContent) {
        return new CapabilityPage(newContent, this.totalElements, this.totalPages, this.currentPage);
    }
};


