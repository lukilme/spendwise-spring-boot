package com.ifpb.edu.spendwise.controller;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.ifpb.edu.spendwise.model.Category;
import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;
import com.ifpb.edu.spendwise.service.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController{

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    
    public String listCategoriesPage(Model model, Integer page, Integer size, Optional<CategoryTypes> categoryType,
            Optional<Boolean> active, Optional<String> search) {
        try {
            // Validação dos parâmetros de paginação
            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Parâmetros de paginação estão incorretos");
            }
            
            Sort sort = createSort("asc");
            PageRequest pageable = PageRequest.of(page, size, sort);
            
            // Busca as categorias com filtros
            Page<Category> categoryPage = getAllCategories(pageable, categoryType, active, search);
            
            // Adiciona atributos de paginação
            addPaginationAttributes(model, page, size, categoryPage);
            addCategoryAttributes(model, categoryPage, categoryType, active, search, sort);
            
            return "categories/list";
            
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Erro ao carregar categorias: " + exception.getMessage());
            return "error";
        }

    }

    @GetMapping("/form")
    
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categoryTypes", CategoryTypes.values());
        model.addAttribute("pageTitle", "Nova Categoria");
        return "categories/form";
    }
    
    @PostMapping("/new")
    
    public String createCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("categoryTypes", CategoryTypes.values());
                model.addAttribute("pageTitle", "Nova Categoria");
                return "categories/form";
            }
            
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Categoria criada com sucesso!");
            return "redirect:/categories";
            
        } catch (Exception e) {
            model.addAttribute("categoryTypes", CategoryTypes.values());
            model.addAttribute("pageTitle", "Nova Categoria");
            model.addAttribute("errorMessage", "Erro ao criar categoria: " + e.getMessage());
            return "categories/form";
        }
    }
    
    @GetMapping("/edit/{id}")
    
    public String editCategoryForm(@PathVariable Long id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Category> categoryOpt = categoryService.findById(id);
            
            if (categoryOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Categoria não encontrada!");
                return "redirect:/categories";
            }
            
            model.addAttribute("category", categoryOpt.get());
            model.addAttribute("categoryTypes", CategoryTypes.values());
            model.addAttribute("pageTitle", "Editar Categoria");
            return "categories/form";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar categoria: " + e.getMessage());
            return "redirect:/categories";
        }
    }
    
    @PostMapping("/edit/{id}")
    
    public String updateCategory(@PathVariable Long id,
                               @Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("categoryTypes", CategoryTypes.values());
                model.addAttribute("pageTitle", "Editar Categoria");
                return "categories/form";
            }
            
            // Verifica se a categoria existe
            if (!categoryService.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Categoria não encontrada!");
                return "redirect:/categories";
            }
            
            category.setId(id);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Categoria atualizada com sucesso!");
            return "redirect:/categories";
            
        } catch (Exception e) {
            model.addAttribute("categoryTypes", CategoryTypes.values());
            model.addAttribute("pageTitle", "Editar Categoria");
            model.addAttribute("errorMessage", "Erro ao atualizar categoria: " + e.getMessage());
            return "categories/form";
        }
    }
    
    @PostMapping("/delete/{id}")
    
    public String deleteCategory(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            if (!categoryService.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Categoria não encontrada!");
                return "redirect:/categories";
            }
            
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Categoria excluída com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir categoria: " + e.getMessage());
        }
        
        return "redirect:/categories";
    }

    public Page<Category> getAllCategories(Pageable pageable,
                                         Optional<CategoryTypes> categoryType,
                                         Optional<Boolean> active,
                                         Optional<String> search) {
        return categoryService.findCategoriesWithFilters(pageable, categoryType, active, search);
    }
    
    // Métodos auxiliares
    private Sort createSort(String order) {
        Sort sort = Sort.by("name"); // Ordenar por nome em vez de ID
        return "asc".equalsIgnoreCase(order) ? sort.ascending() : sort.descending();
    }
    
    private void addPaginationAttributes(Model model, int page, int size, Page<Category> categoryPage) {
        long totalItems = categoryPage.getTotalElements();
        int inicio = page * size + 1;
        long fim = Math.min((page + 1) * size, totalItems);
        
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        
        model.addAttribute("hasPrevious", categoryPage.hasPrevious());
        model.addAttribute("hasNext", categoryPage.hasNext());
        model.addAttribute("previousPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", categoryPage.hasNext() ? page + 1 : page);
    }
    
    private void addCategoryAttributes(Model model,
                                     Page<Category> categoryPage,
                                     Optional<CategoryTypes> categoryType,
                                     Optional<Boolean> active,
                                     Optional<String> search,
                                     Sort sort) {
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("categoryTypes", CategoryTypes.values());
        
        // Filtros atuais
        model.addAttribute("selectedCategoryType", categoryType.orElse(null));
        model.addAttribute("selectedActive", active.orElse(null));
        model.addAttribute("searchTerm", search.orElse(""));
        
        // Ordenação
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDirection", sort.getOrderFor("name").getDirection().toString().toLowerCase());
        
        // Para o formulário de filtros
        model.addAttribute("pageTitle", "Gerenciar Categorias");
    }

    
    public Page<Category> getAllCategories(org.springdoc.core.converters.models.Pageable pageable,
            Optional<CategoryTypes> categoryType, Optional<Boolean> active, Optional<String> search) {
        throw new UnsupportedOperationException("Unimplemented method 'getAllCategories'");
    }
}
