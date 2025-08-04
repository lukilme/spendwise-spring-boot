package com.ifpb.edu.spendwise.service.interfaces;

import java.util.Map;

import com.ifpb.edu.spendwise.model.Category;

public interface CategoryServiceInterface {

    Category createCategory(Category newCategory);

    Category findCategoryById(Long id);

    void deleteCategoryById(Long id);

    void deleteCategoryByName(String categoryName);

    Category updateCategory(Category updateCategory);

    Map<String, Integer> countTransactionsByCategory();
}
