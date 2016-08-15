/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;

import com.domain.*;
import com.forms.PaymentForm;
import net.sf.jasperreports.engine.JRDataSource;
import org.springframework.validation.Errors;

import java.util.List;

/**
 *
 * @author 201244055
 */
public interface PaymentService {
    JRDataSource paymentHistoryDataSource(List<Long> paymentIds);
    Payment save(PaymentForm form);
    Payment updateAccountFromPayment(Payment payment);
    Invoice findLatestBill(Account account);
    Errors validate(PaymentForm form, Errors errors);
    Payment findPaymentById(Long id);
    boolean canEdit(Payment payment);
    boolean isAllowedToSetWarningToAccount(Account account, Integer debtsAllowed);
    List<Account> updateAccountsWithNoPayments(Address address);

}