package com.ifpb.edu.spendwise.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifpb.edu.spendwise.model.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // List<Category> findByCustomerId(Long customerId);

    // List<Category> findByCustomer_IdAndType(Long customerId, CategoryTypes type);

    @SuppressWarnings("null")
    List<Category> findAll();

    @Modifying
    @Query("UPDATE Category c SET c.active = false WHERE c.id = :id")
    void deactivate(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Category c SET c.active = true WHERE c.id = :id")
    void activate(@Param("id") Long id);

    // @Query("SELECT COUNT(t) FROM Transaction t WHERE t.category.id = :categoryId")
    // int countUseByTransaction(@Param("categoryId") Long categoryId);

    // // @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND c.customer.id = :customerId")
    // // boolean existsByNameAndCustomerId(@Param("name") String name, @Param("customerId") Long customerId);

    // // List<Category> findByGlobalTrue();

    // List<Category> findByAdminTrue();

}
