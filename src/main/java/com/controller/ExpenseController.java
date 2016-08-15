package com.controller;


import com.domain.Expense;
import com.domain.ModifiedExpense;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.service.DataTableService;
import com.service.ExpenseService;
import com.service.FormOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasper on 7/26/16.
 */
@RequestMapping("/admin/expenses")
@Controller
public class ExpenseController {

    private DataTableService dataTableService;
    private FormOptionsService formOptionsService;
    private ExpenseService expenseService;
    @Autowired
    public ExpenseController(DataTableService dataTableService, FormOptionsService formOptionsService,
                             ExpenseService expenseService){
        this.formOptionsService = formOptionsService;
        this.dataTableService = dataTableService;
        this.expenseService = expenseService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getAll(ModelMap model){
        HashMap options = formOptionsService.getExpenseFormOptions();
        model.put("expenseForm", new Expense());
        model.put("yearOptions", options.get("year"));
        model.put("monthOptions", options.get("month"));
        model.put("typeOptions", options.get("typeExpense"));
        return "expenses/expenseList";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody HashMap saveExpense(@ModelAttribute("expenseForm") @Valid Expense expenseForm, BindingResult result,
                                             @RequestParam Map<String, String> params){
        HashMap response = new HashMap();
        if(!result.hasErrors()){
            if(params.get("update").trim().length() > 0)
                expenseForm.setId(Long.valueOf(params.get("update")));
            expenseService.validateExpenseForm(expenseForm, result);
        }
        if(!result.hasErrors()){
            try{
                Expense expense = expenseService.saveExpense(expenseForm);
                response.put("expense",expense);
            }catch(JpaOptimisticLockingFailureException e){
                result.reject("global", "This record was modified by another user. Try refreshing the page.");
            }
        }
        if(result.hasErrors()){
            response.put("status", "FAILURE");
            response.put("result", result.getAllErrors());
            return response;
        }
        response.put("status", "SUCCESS");
        return response;
    }

    @RequestMapping(value="/find", method=RequestMethod.POST)
    public @ResponseBody HashMap findExpense(@RequestParam Map<String, String> params){
        HashMap response = new HashMap();
        Expense expense = null;
        try{
            Long id = Long.valueOf(params.get("id"));
            if(id != null)
                expense = expenseService.findExpense(id);
            if(expense != null){
                response.put("status", "SUCCESS");
                response.put("expense", expense);
            }
        }catch(Exception e){
            response.put("status", "FAILURE");
            return response;
        }
        return response;
    }

    @RequestMapping(value = "/datatable-search")
    public @ResponseBody
    DatatablesResponse<Expense> findAllForDataTablesFullSpring(@DatatablesParams DatatablesCriterias criterias){
        DataSet<Expense> dataSet = dataTableService.findWithDataTableCriterias(criterias, Expense.class);
        return DatatablesResponse.build(dataSet, criterias);
    }
    @RequestMapping(value = "/modified/datatable-search")
    public @ResponseBody
    DatatablesResponse<ModifiedExpense> findAllModifiedExpense(@DatatablesParams DatatablesCriterias criterias) {
        DataSet<ModifiedExpense> dataSet = dataTableService.findWithDataTableCriterias(criterias, ModifiedExpense.class);
        return DatatablesResponse.build(dataSet, criterias);
    }
}