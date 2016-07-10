/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.dao.springdatajpa.AccountRepository;
import com.dao.springdatajpa.CustomerRepository;
import com.domain.Account;
import com.domain.Customer;
import com.forms.CustomerForm;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.service.CustomerManagementService;
import com.service.DataTableService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 *
 * @author Bert
 */
@Controller
@RequestMapping("/admin/customers")
@SessionAttributes({"customerForm"})
public class CustomerController {
    
    
    private CustomerManagementService custService;
    private CustomerRepository customerRepo;
    static final String BINDING_RESULT_NAME = "org.springframework.validation.BindingResult.customerForm";
    private DataTableService dataTableService;
    private AccountRepository accountRepo;
    @Autowired
    public CustomerController(CustomerManagementService custService, DataTableService dataTableService, 
            CustomerRepository customerRepo, AccountRepository accountRepo) {
        this.custService = custService;
        this.dataTableService = dataTableService;
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.setDisallowedFields("customer.id", "occupation.id", "address.province",
                                    "address.country", "customer.timestamp",
                                    "address.timestamp");
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String initCustomerForm(ModelMap model) {
        //System.out.println("init form");
        if(!model.containsAttribute(BINDING_RESULT_NAME)){
            System.out.println("init customer");
           model.addAttribute("customerForm", new CustomerForm());
        }
        return "customers/createOrUpdateCustomerForm";
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String processCustomerForm(@ModelAttribute("customerForm") @Valid CustomerForm customerForm,  
                                     BindingResult result,RedirectAttributes redirectAttributes,
                                     SessionStatus status) {
        Customer customer = null;
        if(!result.hasErrors()){
            try{
                customer = custService.save(customerForm);
            }catch(JpaOptimisticLockingFailureException e){
                result.reject("","This record was modified by another user. Try refreshing the page.");
            }
        }
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute(BINDING_RESULT_NAME, result);
            return "redirect:/admin/customers/new";
        }
        System.out.println("success");
        status.setComplete();
        return "redirect:/admin/customers/"+customer.getId();
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public String showCustomerList() {
        return "customers/customerList";
    }
    
    @RequestMapping(value="/{customerId}")
    public String viewCustomer(@PathVariable("customerId") Long id, ModelMap model){
        Customer customer = customerRepo.findOne(id);
        if ( customer == null){
            model.put("type", "Bad request URL");
            model.put("message", "Please avoid retrieving admin pages via URL");
            return "errors";
        }
        model.addAttribute("customer", customer);
        return "customers/viewCustomer";
    }
    
    @RequestMapping(value="/{customerId}/update", method=RequestMethod.GET)
    public String initUpdateForm(@PathVariable("customerId") Long id, ModelMap model){
        if (!model.containsAttribute(BINDING_RESULT_NAME)){
            Customer customer = customerRepo.findOne(id);
            if (customer == null){
                model.put("type", "Bad request URL");
                model.put("message", "Please avoid retrieving admin pages via URL");
                return "errors";
            }
            else{
                CustomerForm customerForm = new CustomerForm();
                customerForm.setCustomer(customer);
                model.addAttribute("customerForm", customerForm);
            }
        }
        return "customers/createOrUpdateCustomerForm";  
    }
    @RequestMapping(value="/{customerId}/update", method=RequestMethod.POST)
    public String processUpdate(@ModelAttribute("customerForm") @Valid CustomerForm customerForm,  
                                     BindingResult result,RedirectAttributes redirectAttributes,
                                     HttpServletRequest request, SessionStatus status){
        if(!result.hasErrors()){
            try{
                custService.save(customerForm);
            }catch(JpaOptimisticLockingFailureException e){
                result.reject("","This record was modified by another user. Try refreshing the page.");
            }
        }
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute(BINDING_RESULT_NAME, result);
            return "redirect:"+request.getRequestURI();
        }
        status.setComplete();
        redirectAttributes.addFlashAttribute("updateSuccess", 1);
        return "redirect:/admin/customers/"+customerForm.getCustomer().getId();
    }
    
    @RequestMapping("/datatable-search")
    public @ResponseBody
    DatatablesResponse<Customer> findAllForDataTablesFullSpring(@DatatablesParams DatatablesCriterias criterias) {
       DataSet<Customer> dataSet = dataTableService.findWithDataTableCriterias(criterias, Customer.class);
       return DatatablesResponse.build(dataSet, criterias);
    }
    
    @RequestMapping("/{customer_id}/accounts")
    public @ResponseBody List<Account> getAllAccounts(@PathVariable("customer_id") Long id){
        return accountRepo.findByCustomer(customerRepo.findOne(id));
    }
}