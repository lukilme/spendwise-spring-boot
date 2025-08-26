package com.ifpb.edu.spendwise.controller.interfaces;

import java.util.Optional;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifpb.edu.spendwise.model.Category;
import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;

import jakarta.validation.Valid;

public interface CategoryControllerInterface{
    /**
     * Exibe a página de listagem de categorias
     * 
     * @param model        Model para passar dados para a view
     * @param page         Página de index de referencia
     * @param size         Quantidade de categoria por página
     * @param categoryType Filtro opcional por tipo de categoria
     * @param active       Filtro opcional por status ativo/inativo
     * @param search       Termo de busca opcional
     * @return Nome da view para renderizar
     */
    String listCategoriesPage(Model model,
            Integer page, Integer size,
            Optional<CategoryTypes> categoryType,
            Optional<Boolean> active,
            Optional<String> search);

    /**
     * Exibe o formulário para criar uma nova categoria
     * 
     * @param model Model para passar dados para a view
     * @return Nome da view do formulário de criação
     */
    String createCategoryForm(Model model);

    /**
     * Processa a criação de uma nova categoria
     * 
     * @param Category           base com dados da categoria
     * @param bindingResult      Resultado da validação
     * @param model              Model para passar dados para a view
     * @param redirectAttributes Atributos para redirecionamento
     * @return Redirecionamento ou view com erros
     */
    String createCategory(@Valid Category categoryCreateDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes);

    /**
     * Exibe o formulário para editar uma categoria existente
     * 
     * @param id                 ID da categoria a ser editada
     * @param model              Model para passar dados para a view
     * @param redirectAttributes Atributos para redirecionamento
     * @return Nome da view do formulário de edição ou redirecionamento
     */
    String editCategoryForm(Long id,
            Model model,
            RedirectAttributes redirectAttributes);

    /**
     * Processa a atualização de uma categoria existente
     * 
     * @param id                 ID da categoria a ser atualizada
     * @param Category           DTO com dados atualizados
     * @param bindingResult      Resultado da validação
     * @param model              Model para passar dados para a view
     * @param redirectAttributes Atributos para redirecionamento
     * @return Redirecionamento ou view com erros
     */
    String updateCategory(Long id,
            @Valid Category categoryUpdateDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes);

    /**
     * Processa a exclusão física de uma categoria (apenas para admins)
     * 
     * @param id                 ID da categoria a ser deletada permanentemente
     * @param redirectAttributes Atributos para redirecionamento
     * @return Redirecionamento para a lista
     */
    String deleteCategory(Long id, RedirectAttributes redirectAttributes);

    /**
     * Lista todas as categorias com paginação e filtros
     * 
     * @param pageable     Parâmetros de paginação
     * @param categoryType Filtro opcional por tipo de categoria
     * @param active       Filtro opcional por status ativo/inativo
     * @param search       Termo de busca opcional
     * @return Page<Category> com página de categorias
     */
    Page<Category> getAllCategories(Pageable pageable,
            Optional<CategoryTypes> categoryType,
            Optional<Boolean> active,
            Optional<String> search);

}
