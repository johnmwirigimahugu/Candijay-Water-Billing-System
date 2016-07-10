/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao.springdatajpa;

import com.domain.Account;
import com.domain.MeterReading;
import com.domain.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Bert
 */
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    public List<MeterReading> findTop3ByAccountOrderByIdDesc(Account account);
    public MeterReading findByAccountAndSchedule(Account account, Schedule schedule);
    public List<MeterReading> findByScheduleAndAccount_Address_Brgy(Schedule sched, String brgy);
    public List<MeterReading> findBySchedule(Schedule sched);
}