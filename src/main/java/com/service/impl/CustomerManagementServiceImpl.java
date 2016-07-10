/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service.impl;

import com.dao.DataTableDao;
import com.dao.springdatajpa.AccountRepository;
import com.dao.springdatajpa.AddressRepository;
import com.dao.springdatajpa.CustomerRepository;
import com.dao.springdatajpa.DeviceRepository;
import com.dao.springdatajpa.TaxRepository;
import com.domain.Account;
import com.domain.Address;
import com.domain.Customer;
import com.domain.Device;
import com.domain.enums.AccountStatus;
import com.forms.AccountForm;
import com.forms.CustomerForm;
import com.service.CustomerManagementService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Bert
 */
@Service
public class CustomerManagementServiceImpl implements CustomerManagementService {
    
    private CustomerRepository customerRepo;
    private AccountRepository accountRepo;
    private TaxRepository taxRepo;
    private AddressRepository addressRepo;
    private DataTableDao dataTableQueryHelper;
    private DeviceRepository deviceRepo;
    
    @Autowired
    public CustomerManagementServiceImpl(CustomerRepository customerRepo, AccountRepository accountRepo, 
            TaxRepository taxRepo, AddressRepository addressRepo, DataTableDao dataTableQueryHelper, DeviceRepository deviceRepo) {
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
        this.taxRepo = taxRepo;
        this.addressRepo = addressRepo;
        this.dataTableQueryHelper = dataTableQueryHelper;
        this.deviceRepo = deviceRepo;
    }
             
    @Override
    @Transactional
    public Customer save(Customer customer) {
        return customerRepo.save(customer);
    }   

    @Override
    @Transactional
    public Customer save(CustomerForm customerForm) {
        Customer c = customerForm.getCustomer();
        return save(c);
    }

    @Transactional
    @Override
    public Account save(AccountForm accountForm) {
        Customer customer = customerRepo.findOne(accountForm.getCustomerId());
        Account account = accountForm.getAccount();
        Device device = accountForm.getDevice();
        if(account.getId() == null)
            device.setStartDate(new Date());
        device.setActive(true);
        Address address = accountForm.getAddress();
        account.setAddress(address);
        account.setCustomer(customer);
        account = accountRepo.save(account);
        device.setOwner(account);
        deviceRepo.save(device);
        address.getAddressGroup().setAccountsCount(address.getAddressGroup().getAccountsCount()+1);
        addressRepo.save(address);
        return account;
    }
    
    @Transactional
    @Override
    public void changeAccountStatus(Account account, AccountStatus status) {
       account.setStatus(status);
       accountRepo.save(account);
    }
    
    @Transactional
    @Override
    public Device saveDevice(String accountNumber, Device device) {
        device.setOwner(accountRepo.findByNumber(accountNumber));
        return deviceRepo.save(device);
    }

    @Override
    public void activateDevice(Device device) {
        List<Device> devices =  deviceRepo.findByOwner(device.getOwner());
        for(Device d : devices){
            if(!d.getId().equals(device.getId()))
                d.setActive(false);
            else d.setActive(true);
            deviceRepo.save(d);
        }

    }
}