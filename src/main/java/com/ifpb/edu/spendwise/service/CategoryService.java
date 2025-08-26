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
import com.ifpb.edu.spendwise.service.interfaces.CategoryServiceInterface;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Busca todas as categorias com paginação
     */
    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    /**
     * Busca todas as categorias sem paginação
     */
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Busca categoria por ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return categoryRepository.findById(id);
    }
    
    /**
     * Busca categoria por nome (case insensitive)
     */
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return categoryRepository.findByNameIgnoreCase(name.trim());
    }
    
    /**
     * Busca categorias ativas
     */
    @Transactional(readOnly = true)
    public List<Category> findActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }
    
    /**
     * Busca categorias por tipo
     */
    @Transactional(readOnly = true)
    public List<Category> findByCategoryType(CategoryTypes categoryType) {
        return categoryRepository.findByCategoryType(categoryType);
    }
    
    /**
     * Busca categorias com filtros aplicados
     */
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
    
    /**
     * Salva ou atualiza uma categoria
     */
    public Category save(Category category) {
        validateCategory(category);
        
        // Trim do nome e descrição
        if (category.getName() != null) {
            category.setName(category.getName().trim());
        }
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }
        
        return categoryRepository.save(category);
    }
    
    /**
     * Cria uma nova categoria
     */
    public Category create(Category category) {
        if (category.getId() != null) {
            throw new IllegalArgumentException("Nova categoria não deve ter ID definido");
        }
        
        validateUniqueNameForCreate(category.getName());
        
        // Define valores padrão se necessário
        if (category.getActive() == null) {
            category.setActive(true);
        }
        
        return save(category);
    }
    
    /**
     * Atualiza uma categoria existente
     */
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
    
    /**
     * Exclui uma categoria por ID
     */
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        Optional<Category> category = findById(id);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Categoria não encontrada com ID: " + id);
        }
        
        // Verifica se a categoria pode ser excluída (sem referências)
        validateCategoryCanBeDeleted(id);
        
        categoryRepository.deleteById(id);
    }
    
    /**
     * Desativa uma categoria (soft delete)
     */
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
    
    /**
     * Verifica se categoria existe por ID
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return categoryRepository.existsById(id);
    }
    
    /**
     * Verifica se categoria existe por nome
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return categoryRepository.existsByNameIgnoreCase(name.trim());
    }
    
    /**
     * Conta total de categorias
     */
    @Transactional(readOnly = true)
    public long count() {
        return categoryRepository.count();
    }
    
    /**
     * Conta categorias ativas
     */
    @Transactional(readOnly = true)
    public long countActiveCategories() {
        return categoryRepository.countByActiveTrue();
    }
    
    /**
     * Conta categorias por tipo
     */
    @Transactional(readOnly = true)
    public long countByCategoryType(CategoryTypes categoryType) {
        return categoryRepository.countByCategoryType(categoryType);
    }
    
    // Métodos de validação privados
    
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
        
        // Validações de tamanho (complementar às anotações JPA)
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
        // Implementar verificações específicas baseadas nas suas regras de negócio
        // Por exemplo, verificar se existem produtos, transações, etc. que referenciam esta categoria
        
        Long references = categoryRepository.countReferencesToCategory(categoryId);
        if (references > 0) {
            throw new CategoryException("Não é possível excluir a categoria pois existem registros que a referenciam");
        }
    }

   

    // @Override
    // public Map<String, Integer> countTransactionsByCategory() {
    //     return categoryRepository.countTransactionsByCategory();
    // }
}
