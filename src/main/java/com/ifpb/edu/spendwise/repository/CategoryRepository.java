package com.ifpb.edu.spendwise.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifpb.edu.spendwise.model.Category;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

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

}
