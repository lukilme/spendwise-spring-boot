package com.ifpb.edu.spendwise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifpb.edu.spendwise.model.Category;
import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;

import jakarta.transaction.Transactional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Busca categoria pelo nome (case insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Verifica se existe categoria com o nome (excluindo o ID atual para updates)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    
    /**
     * Verifica se existe categoria com o nome
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Busca categorias por tipo
     */
    List<Category> findByCategoryType(CategoryTypes categoryType);
    
    /**
     * Busca categorias ativas
     */
    List<Category> findByActiveTrue();
    
    /**
     * Busca categorias por status ativo
     */
    List<Category> findByActive(Boolean active);
    
    /**
     * Busca categorias por tipo e status ativo
     */
    Page<Category> findByCategoryTypeAndActive(CategoryTypes categoryType, Boolean active, Pageable pageable);
    
    /**
     * Query personalizada para busca com múltiplos filtros
     */
    @Query("SELECT c FROM Category c WHERE " +
           "(:categoryType IS NULL OR c.categoryType = :categoryType) AND " +
           "(:active IS NULL OR c.active = :active) AND " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Category> findCategoriesWithFilters(@Param("categoryType") CategoryTypes categoryType,
                                           @Param("active") Boolean active,
                                           @Param("search") String search,
                                           Pageable pageable);

    // List<Category> findByCustomerId(Long customerId);

    // List<Category> findByCustomer_IdAndType(Long customerId, CategoryTypes type);

    List<Category> findAll();

    @Modifying
    @Query("UPDATE Category c SET c.active = false WHERE c.id = :id")
    void deactivate(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Category c SET c.active = true WHERE c.id = :id")
    void activate(@Param("id") Long id);


    // // @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND
    // c.customer.id = :customerId")???
    // // boolean existsByNameAndCustomerId(@Param("name") String name,
    // @Param("customerId") Long customerId);

    // // List<Category> findByGlobalTrue();
    @Modifying
    @Transactional
    @Query("DELETE FROM Category c WHERE c.name = :categoryName")
    void deleteByName(String categoryName);

    // @Query("SELECT c.name, COUNT(t) FROM Category c LEFT JOIN c.transactions t GROUP BY c.name")
    // List<Object[]> countByTransactionsCategoryId();

    // default Map<String, Integer> countTransactionsByCategory() {
    //     return countByTransactionsCategoryId().stream()
    //             .collect(Collectors.toMap(
    //                     obj -> (String) obj[0],
    //                     obj -> ((Long) obj[1]).intValue()));
    // }

    Category findByName(String name);
    // List<Category> findByAdminTrue();
    
    /**
     * Busca categorias ordenadas por nome
     */
    List<Category> findAllByOrderByNameAsc();
    
    /**
     * Busca categorias ativas ordenadas por nome
     */
    List<Category> findByActiveTrueOrderByNameAsc();
    
    /**
     * Conta categorias por tipo
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.categoryType = :categoryType")
    Long countByCategoryType(@Param("categoryType") CategoryTypes categoryType);
    
    /**
     * Conta categorias ativas
     */
    Long countByActiveTrue();
    
    /**
     * Query para verificar se categoria pode ser excluída (sem referências)
     * Adapte conforme suas entidades que referenciam Category
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.id = :categoryId")
    Long countReferencesToCategory(@Param("categoryId") Long categoryId);
}