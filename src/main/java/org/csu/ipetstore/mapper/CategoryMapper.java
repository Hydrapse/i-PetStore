package org.csu.ipetstore.mapper;

import org.csu.ipetstore.domain.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper {
    List<Category> getCategoryList();

    Category getCategory(String categoryId);
}
