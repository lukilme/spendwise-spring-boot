package com.ifpb.edu.spendwise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifpb.edu.spendwise.exception.category.CategoryException;
import com.ifpb.edu.spendwise.model.Category;
import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;
import com.ifpb.edu.spendwise.repository.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return categoryRepository.findByNameIgnoreCase(name.trim());
    }

    @Transactional(readOnly = true)
    public List<Category> findActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Category> findByCategoryType(CategoryTypes categoryType) {
        return categoryRepository.findByCategoryType(categoryType);
    }

    @Transactional(readOnly = true)
    public Page<Category> findCategoriesWithFilters(Pageable pageable,
            Optional<CategoryTypes> categoryType,
            Optional<Boolean> active,
            Optional<String> search) {
        CategoryTypes typeFilter = categoryType.orElse(null);
        Boolean activeFilter = active.orElse(null);
        String searchFilter = search.filter(s -> !s.trim().isEmpty()).orElse(null);

        return categoryRepository.findCategoriesWithFilters(typeFilter, activeFilter, searchFilter, pageable);
    }

    public Category save(Category category) {
        validateCategory(category);

        if (category.getName() != null) {
            category.setName(category.getName().trim());
        }
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }

        return categoryRepository.save(category);
    }

    public Category create(Category category) {
        if (category.getId() != null) {
            throw new IllegalArgumentException("Nova categoria não deve ter ID definido");
        }

        validateUniqueNameForCreate(category.getName());

        if (category.getActive() == null) {
            category.setActive(true);
        }

        return save(category);
    }

    public Category update(Long id, Category category) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo para atualização");
        }

        Optional<Category> existingCategory = findById(id);
        if (existingCategory.isEmpty()) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + id);
        }

        validateUniqueNameForUpdate(category.getName(), id);

        category.setId(id);
        return save(category);
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }

        Optional<Category> category = findById(id);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + id);
        }

        validateCategoryCanBeDeleted(id);

        categoryRepository.deleteById(id);
    }

    public Category deactivate(Long id) {
        Optional<Category> categoryOpt = findById(id);
        if (categoryOpt.isEmpty()) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + id);
        }

        Category category = categoryOpt.get();
        category.setActive(false);
        return save(category);
    }

    public Category activate(Long id) {
        Optional<Category> categoryOpt = findById(id);
        if (categoryOpt.isEmpty()) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + id);
        }

        Category category = categoryOpt.get();
        category.setActive(true);
        return save(category);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return categoryRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return categoryRepository.existsByNameIgnoreCase(name.trim());
    }

    @Transactional(readOnly = true)
    public long count() {
        return categoryRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveCategories() {
        return categoryRepository.countByActiveTrue();
    }

    @Transactional(readOnly = true)
    public long countByCategoryType(CategoryTypes categoryType) {
        return categoryRepository.countByCategoryType(categoryType);
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }

        if (category.getCategoryType() == null) {
            throw new IllegalArgumentException("Tipo da categoria é obrigatório");
        }

        String name = category.getName().trim();
        if (name.length() > 50) {
            throw new IllegalArgumentException("Nome da categoria não pode exceder 50 caracteres");
        }

        if (category.getDescription() != null && category.getDescription().length() > 255) {
            throw new IllegalArgumentException("Descrição não pode exceder 255 caracteres");
        }
    }

    private void validateUniqueNameForCreate(String name) {
        if (name != null && existsByName(name)) {
            throw new CategoryException("Já existe uma categoria com o nome: " + name.trim());
        }
    }

    private void validateUniqueNameForUpdate(String name, Long id) {
        if (name != null && categoryRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), id)) {
            throw new CategoryException("Já existe outra categoria com o nome: " + name.trim());
        }
    }

    private void validateCategoryCanBeDeleted(Long categoryId) {

        Long references = categoryRepository.countReferencesToCategory(categoryId);
        if (references > 0) {
            throw new CategoryException("Não é possível excluir a categoria pois existem registros que a referenciam");
        }
    }

    // @Override
    // public Map<String, Integer> countTransactionsByCategory() {
    // return categoryRepository.countTransactionsByCategory();
    // }
}
