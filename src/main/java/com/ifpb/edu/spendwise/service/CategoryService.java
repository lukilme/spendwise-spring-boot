package com.ifpb.edu.spendwise.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ifpb.edu.spendwise.model.Category;
import com.ifpb.edu.spendwise.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public List<Category>  findAll(){
        return this.categoryRepository.findAll();
    }
}
