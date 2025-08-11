package com.ifpb.edu.spendwise.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.model.Category;
import com.ifpb.edu.spendwise.repository.CategoryRepository;
import com.ifpb.edu.spendwise.service.interfaces.CategoryServiceInterface;

@Service
public class CategoryService implements CategoryServiceInterface{
    @Autowired
    CategoryRepository categoryRepository;

    public List<Category>  findAll(){
        return this.categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category newCategory) {
        if (categoryRepository.findByName(newCategory.getName()) != null) {
            throw new RuntimeException("Categoria já existe");
        }
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void deleteCategoryByName(String categoryName) {
        categoryRepository.deleteByName(categoryName);
    }

    @Override
    public Category updateCategory(Category updateCategory) {
        return categoryRepository.save(updateCategory);
    }

    // @Override
    // public Map<String, Integer> countTransactionsByCategory() {
    //     return categoryRepository.countTransactionsByCategory();
    // }
}
