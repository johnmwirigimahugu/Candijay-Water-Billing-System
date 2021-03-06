/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import java.util.List;

/**
 *
 * @author Bert
 */
public interface DataTableDao {
    <T>List<T> findWithDataTablesCriteria(DatatablesCriterias criterias, Class<T> clazz);
    Long getFilteredCount(DatatablesCriterias criterias, Class clazz);
    Long getTotalCount(Class clazz);
}
