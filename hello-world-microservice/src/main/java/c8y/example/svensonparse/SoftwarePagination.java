package c8y.example.svensonparse;

import java.util.List;
import java.util.stream.Collectors;

public class SoftwarePagination {
    private int pageSize;
    private int currentPage;

    public SoftwarePagination(int pageSize, int currentPage) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    public List<Software> paginate(List<Software> softwares) {
        int skip = (currentPage - 1) * pageSize;
        return softwares.stream()
                .skip(skip)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public int getTotalPages(int totalItems) {
        return (int) Math.ceil((double) totalItems / pageSize);
    }
}

