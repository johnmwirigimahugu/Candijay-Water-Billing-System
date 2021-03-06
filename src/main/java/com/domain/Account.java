
package com.domain;
// Generated Apr 16, 2015 12:48:29 PM by Hibernate Tools 4.3.1

import com.dao.util.EnglishNumberToWords;
import com.domain.enums.AccountStatus;
import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Account generated by hbm2java
 */
@Entity
@Table(name="account")
public class Account  implements java.io.Serializable {

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Long id;
    @Column(name="number", unique=true, nullable=false)
    private String number;
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer; 
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="address_id", nullable=false)
    private Address address;
    @NotNull(message="This field is required")
    @Column(name="purok", nullable=false)
    private Integer purok;
    @Column(name="account_type", nullable=true, length=45)
    private String accountType;
    @Column(name="account_standing_balance", nullable=false, precision=9)
    private BigDecimal accountStandingBalance = new BigDecimal(0.00);
    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private AccountStatus status = AccountStatus.ACTIVE;
    @Column(name="status_updated", nullable=false)
    private boolean statusUpdated = true;
    @OneToMany(fetch=FetchType.LAZY, mappedBy="account", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Set<MeterReading> meterReadings = new HashSet<MeterReading>(0);
    @OneToMany(fetch=FetchType.LAZY, mappedBy="account", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Payment> payments = new HashSet<Payment>(0);
    @OneToMany(fetch=FetchType.LAZY, mappedBy="account", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Invoice> invoices = new HashSet<Invoice>(0);
    @OneToMany(fetch=FetchType.LAZY, mappedBy="owner", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Device> devices = new HashSet<Device>(0);
    
    public Account() { }

    public Account(String number, Customer customer, Address address, Integer purok) {
        this.number = number;
        this.customer = customer;
        this.address = address;
        this.purok = purok;
    }
        
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    @JsonManagedReference
    public Address getAddress() {
        return address;
    }
    @JsonProperty
    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getPurok() {
        return purok;
    }

    public void setPurok(Integer purok) {
        this.purok = purok;
    }

    @JsonManagedReference
    public Customer getCustomer() {
        return this.customer;
    }
    @JsonProperty
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @JsonIgnore
    public String getAccountType() {
        return this.accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getAccountStandingBalance() {
        return this.accountStandingBalance;
    }
    
    public void setAccountStandingBalance(BigDecimal accountStandingBalance) {
        this.accountStandingBalance = accountStandingBalance;
    }

    public boolean isStatusUpdated() {
        return statusUpdated;
    }

    public void setStatusUpdated(boolean statusUpdated) {
        this.statusUpdated = statusUpdated;
    }

    @JsonBackReference
    public Set<MeterReading> getMeterReadings() {
        return this.meterReadings;
    }
    @JsonProperty
    public void setMeterReadings(Set<MeterReading> meterReadings) {
        this.meterReadings = meterReadings;
    }

    @JsonBackReference
    public Set<Payment> getPayments() {
        return this.payments;
    }
    @JsonProperty
    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }
    @JsonBackReference
    public Set<Invoice> getInvoices() {
        return this.invoices;
    }
    @JsonProperty
    public void setInvoices(Set<Invoice> invoices) {
        this.invoices = invoices;
    }

    public void addMeterReading(MeterReading reading){
        meterReadings.add(reading);
    }

    @JsonIgnore
    public String getAccountStandingBalanceToString(){
        long decimalPart =this.accountStandingBalance.longValue();
        String stringVal = this.accountStandingBalance.toPlainString();
        int index = stringVal.indexOf(".");
        long fractionPart = Long.valueOf(stringVal.substring(index+1, stringVal.length()));
        String balanceWords = EnglishNumberToWords.convert(decimalPart)+" pesos";
        if(fractionPart > 0){
            balanceWords += " and "+EnglishNumberToWords.convert(fractionPart) + " centavos";
        }
        return "(P"+this.accountStandingBalance+") "+balanceWords;
    }

    @JsonIgnore
    public DateTime getDisconnectionDate(){
        DateTime currDate = new DateTime();
        DateTime disconnectionDate = new DateTime(
                currDate.getYear(), currDate.getMonthOfYear(), this.address.getDueDay(), 0 ,0);
        return disconnectionDate = disconnectionDate.plusDays(10);
    }
    
    @JsonBackReference
    public Set<Device> getDevices() {
        return devices;
    }
    
    @JsonProperty
    public void setDevices(Set<Device> devices) {
        this.devices = devices;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(this.id).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Account other = (Account) obj;
        return new EqualsBuilder().
                append(this.id, other.id).
                isEquals();
    }
}